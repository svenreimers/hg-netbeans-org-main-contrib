/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 1997-2015 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.lib.callgraph;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.lib.callgraph.util.ComparableCharSequence;

/**
 * Parses and validates command-line arguments.
 *
 * @author Tim Boudreau
 */
final class Arguments implements CallgraphControl {

    private Set<File> folders = new ConcurrentSkipListSet<>();
    private static final Command[] COMMANDS = new Command[]{
        new NoSelfReferencesCommand(),
        new ShortNamesCommand(),
        new ExtendedPropertiesCommand(),
        new AntCommand(),
        new MavenCommand(),
        new GradleCommand(),
        new IgnoreCommand(),
        new OutfileCommand(),
        new ExcludeCommand(),
        new PackageGraphFileCommand(),
        new ClassGraphFileCommand(),
        new OmitAbstractCommand(),
        new DisableEightBitStringsCommand(),
        new QuietCommand(),
        new ReverseCommand(),
        new AggressiveCommand(),
        new VerboseCommand(),
        new IgnoreShallowPackagesCommand(),
        new IgnoreAnonymousClassesCommand(),
        new IncludeWeightsCommand(),
        new NoOrphansCommand()
    };

    static {
        if (new HashSet<>(Arrays.asList(COMMANDS)).size() != COMMANDS.length) {
            throw new AssertionError("Command list contains duplicates");
        }
        //sanity check
        for (int i = 0; i < COMMANDS.length; i++) {
            Command ca = COMMANDS[i];
            for (int j = i + 1; j < COMMANDS.length; j++) {
                Command cb = COMMANDS[j];
                if (ca.shortcut.equals(cb.shortcut)) {
                    throw new AssertionError("Conflicting shortcut '" + ca.shortcut
                            + "' for " + ca.getClass().getSimpleName() + " and "
                            + cb.getClass().getSimpleName());
                }
            }
        }
    }
    private boolean ignoreShallowPackages = false;
    private boolean noSelfReferences = false;
    private boolean weights = false;
    private boolean shortNames = false;
    private boolean maven = false;
    private boolean gradle = false;
    private boolean ant = false;
    private boolean xprop = false;
    private boolean aggressive = false;
    private File outfile;
    private Set<String> exclude = new HashSet<>();
    private Set<String> ignore = new HashSet<>();
    private boolean quiet;
    private File classGraphFile;
    private File packageGraphFile;
    private boolean verbose;
    private boolean omitAbstract;
    private boolean disableEightBitStrings;
    private boolean reverse;
    final Set<File> ignoreFolders = new HashSet<>();
    private boolean ignoreAnonymousClasses;

    Arguments(String... args) throws IOException {
        this(true, args);
    }

    Arguments(boolean abortIfWillNotOutput, String... args) throws IOException {
        List<String> unknownArguments = new LinkedList<>();
        List<String> errors = new LinkedList<>();
        for (int i = 0; i < args.length;) {
            int oldPos = i;
            for (Command c : COMMANDS) {
                try {
                    @SuppressWarnings("LeakingThisInConstructor")
                    int increment = c.parse(i, args, this);
                    if (increment > 0) {
                        i += increment;
                        break;
                    }
                } catch (IllegalArgumentException ex) {
                    errors.add(c.name + ": " + ex.getMessage());
                }
            }
            if (oldPos == i) {
                unknownArguments.add(args[i]);
                i++;
            }
        }
        Set<String> folders = new HashSet<>();
        for (String unknown : unknownArguments) {
            if (unknown.startsWith("-")) {
                // bad line switch
                errors.add("Unknown argument '" + unknown + "'");
            } else {
                folders.add(unknown);
            }
        }
        if (folders.isEmpty()) {
            errors.add("No folders of Java sources specified");
        } else {
            for (String folderName : folders) {
                File folder = new File(folderName);
                if (!folder.exists()) {
                    errors.add("Folder does not exist: " + folderName);
                } else if (!folder.isDirectory()) {
                    errors.add("Not a folder: " + folderName);
                } else {
                    this.folders.add(folder.getCanonicalFile());
                }
            }
        }
        if (verbose && (maven || gradle || ant)) {
            System.err.println("Scanning for projects in " + this.folders());
        }
        if ((maven && gradle) || (ant && gradle) || (ant && maven)) {
            errors.add("--maven, --ant and --gradle are mutually exclusive");
        } else if (maven) {
            findMavenSubfolders(errors);
        } else if (gradle) {
            findGradleSubfolders(errors);
        } else if (ant) {
            findAntSubfolders(errors);
        }
        Set<File> toIgnore = new HashSet<>();
        for (String ig : ignore) {
            File ff = new File(ig);
            if (ff.exists() && ff.isDirectory()) {
                ignoreFolders.add(ff);
                ff = ff.getCanonicalFile();
                ignoreFolders.add(ff);
                for (File f : this.folders()) {
                    File f1 = f.getCanonicalFile();
                    if (f1.equals(ff)) {
                        toIgnore.add(f1);
                    } else {
                        if (f1.getPath().startsWith(ff.getPath())) {
                            toIgnore.add(f1);
                        }
                    }
                }
            } else {
                for (String u : unknownArguments) {
                    if (!u.startsWith("-")) {
                        File f = new File(u);
                        if (f.exists()) {
                            f = f.getCanonicalFile();
                            File maybeIgnore = new File(f, ig);
                            if (maybeIgnore.exists() && maybeIgnore.isDirectory()) {
                                maybeIgnore = maybeIgnore.getCanonicalFile();
                                for (File fld : this.folders()) {
                                    if (fld.equals(maybeIgnore)) {
                                        toIgnore.add(fld);
                                    } else if (fld.getAbsolutePath().startsWith(maybeIgnore.getAbsolutePath())) {
                                        toIgnore.add(fld);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (verbose) {
            if (toIgnore.isEmpty()) {
                System.err.println("Not ignoring any folders");
            } else {
                System.err.println("Ignoring " + toIgnore.size() + " folders due to -i");
            }
        }
//        if (verbose && !toIgnore.isEmpty()) {
//            System.err.println("Ignoring the following projects:");
//            for (File ti : toIgnore) {
//                System.err.println(" - " + ti.getAbsolutePath());
//            }
//        }
        this.folders.removeAll(toIgnore);
        if (verbose && !this.folders.isEmpty()) {
            System.err.println("Will scan the following source roots:");
            StringBuilder sb = new StringBuilder();

            for (Iterator<File> it = folders().iterator(); it.hasNext();) {
                File f = it.next();
                sb.append(f.getAbsolutePath());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            System.err.println(sb);
            if (maven || gradle || ant) {
                System.err.println("Found " + folders().size() + " source roots.");
            }
        }

        if (packageGraphFile != null) {
            File parent = packageGraphFile.getParentFile();
            if (parent == null || !parent.exists() || !parent.isDirectory()) {
                errors.add("Parent folder for package graph output file does not exist: " + parent);
            }
        }
        if (classGraphFile != null) {
            File parent = classGraphFile.getParentFile();
            if (parent == null || !parent.exists() || !parent.isDirectory()) {
                errors.add("Parent folder for class graph output file does not exist: " + parent);
            }
        }
        if (outfile != null) {
            File parent = outfile.getParentFile();
            if (parent == null || !parent.exists() || !parent.isDirectory()) {
                errors.add("Parent folder for output file does not exist: " + parent);
            }
        } else if (abortIfWillNotOutput && quiet && packageGraphFile == null && classGraphFile == null) {
            errors.add("-q or --quiet specified, but no output file specified - would not produce any output at all");
        }
        // Ensure no folders are nested inside each other
        Set<File> all = new HashSet<>(this.folders);
        int children = 0;
        for (File f1 : all) {
            for (File f2 : all) {
                if (f1 == f2) {
                    continue;
                }
                if (f2.getAbsolutePath().startsWith(f1.getAbsolutePath())) {
                    children++;
                    this.folders.remove(f2);
                }
            }
        }
        if (verbose) {
            System.err.println("Pruned " + children + " due to being children of other folders");
        }
        if (!errors.isEmpty()) {
            throw new InvalidArgumentsException(help(errors), errors);
        }
    }

    @Override
    public boolean accept(File f) {
        if (ignoreFolders.contains(f)) {
            return false;
        }
        for (File ig : ignoreFolders) {
            if (f.getAbsolutePath().startsWith(ig.getAbsolutePath())) {
                return false;
            }
        }
        return true;
    }

    public boolean isIgnoreAbstract() {
        return omitAbstract;
    }

    public boolean isIgnoreAnonymous() {
        return ignoreAnonymousClasses;
    }
    
    public boolean isWeights() {
        return weights;
    }

    private void findMavenSubfolders(List<String> errors) {
        Set<File> flds = new HashSet<>(this.folders);
        this.folders.clear();
        for (File f : flds) {
            recurseSubfoldersLookingForMavenProjects(f);
        }
        if (this.folders.isEmpty()) {
            errors.add("Did not find any maven projects (looked for pom.xml and src/main/java in all subfolders of folder list)");
        }
    }

    private void recurseSubfoldersLookingForMavenProjects(File file) {
        if (file.isDirectory()) {
            if (hasPom(file)) {
                File sources = srcMainJavaFolder(file);
                if (sources != null) {
                    this.folders.add(sources);
                }
            }
            for (File child : file.listFiles()) {
                recurseSubfoldersLookingForMavenProjects(child);
            }
        }
    }

    private boolean hasPom(File fld) {
        File pom = new File(fld, "pom.xml");
        return pom.exists() && pom.isFile() && pom.canRead();
    }

    void findAntSubfolders(List<String> errors) {
        Set<File> flds = new HashSet<>(this.folders);
        this.folders.clear();
        for (File f : flds) {
            recurseSubfoldersLookingForAntProjects(f);
        }
        if (this.folders.isEmpty()) {
            errors.add("Did not find any ant projects (looked for build.xml and src/ in all subfolders of folder list)");
        }
    }

    private void recurseSubfoldersLookingForAntProjects(File file) {
        if (file.isDirectory()) {
            if (hasBuildXml(file)) {
                File sources = new File(file, "src");
                if (sources.exists() && sources.isDirectory()) {
                    this.folders.add(sources);
                }
            }
            List<File> kids = Arrays.asList(file.listFiles(File::isDirectory));
            if (kids.size() > 5) {
                for (File child : file.listFiles()) {
                    recurseSubfoldersLookingForAntProjects(child);
                }
            } else {
                kids.parallelStream().forEach((File f) -> {
                    recurseSubfoldersLookingForAntProjects(f);
                });
            }
        }
    }

    private boolean hasBuildXml(File fld) {
        File pom = new File(fld, "build.xml");
        return pom.exists() && pom.isFile() && pom.canRead();
    }

    void findGradleSubfolders(List<String> errors) {
        Set<File> flds = new HashSet<>(this.folders);
        this.folders.clear();
        for (File f : flds) {
            recurseSubfoldersLookingForGradleProjects(f);
        }
        if (this.folders.isEmpty()) {
            errors.add("Did not find any gradle projects (looked for *.gradle and src/main/java in all subfolders of folder list)");
        }
    }

    private void recurseSubfoldersLookingForGradleProjects(File file) {
        if (file.isDirectory()) {
            if (hasGradleFile(file)) {
                File sources = srcMainJavaFolder(file);
                if (sources != null) {
                    this.folders.add(sources);
                }
            }
            for (File child : file.listFiles()) {
                recurseSubfoldersLookingForGradleProjects(child);
            }
        }
    }

    boolean hasGradleFile(File fld) {
        return fld.listFiles((File pathname) -> {
            return pathname.getName().endsWith(".gradle") && pathname.isFile() && pathname.canRead();
        }).length > 0;
    }

    public boolean isGradle() {
        return gradle;
    }

    private File srcMainJavaFolder(File projectFolder) {
        File src = new File(projectFolder, "src");
        File main = new File(src, "main");
        File java = new File(main, "java");
        if (java.exists() && java.isDirectory()) {
            return java;
        }
        return null;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isDisableEightBitStrings() {
        return disableEightBitStrings;
    }

    public boolean isReverse() {
        return reverse;
    }

    @Override
    public File methodGraphFile() {
        return outfile;
    }

    @Override
    public Set<File> folders() {
        return Collections.unmodifiableSet(folders);
    }

    @Override
    public boolean isSelfReferences() {
        return noSelfReferences == false;
    }

    @Override
    public boolean isShortNames() {
        return shortNames;
    }

    @Override
    public boolean isMaven() {
        return maven;
    }

    @Override
    public boolean isAnt() {
        return ant;
    }

    @Override
    public boolean isExtendedProperties() {
        return xprop;
    }

    @Override
    public boolean isAggressive() {
        return aggressive;
    }

    @Override
    public File classGraphFile() {
        return classGraphFile;
    }

    @Override
    public File packageGraphFile() {
        return packageGraphFile;
    }

    @Override
    public Iterator<File> iterator() {
        return folders().iterator();
    }

    @Override
    public Set<String> excludePrefixes() {
        return Collections.unmodifiableSet(exclude);
    }

    public boolean isOmitAbstract() {
        return omitAbstract;
    }
    
    private boolean noOrphans;
    public boolean isNoOphans() {
        return noOrphans;
    }

    private static final Pattern ANONYMOUS = Pattern.compile(".*?\\$\\.\\d+.*");

    public boolean isExcluded(CharSequence qname) {
        if (exclude.size() > 0) {
            if (qname instanceof ComparableCharSequence) {
                ComparableCharSequence ccs = (ComparableCharSequence) qname;
                for (String ex : exclude) {
                    if (ccs.startsWith(ex)) {
                        return true;
                    }
                }
            } else {
                String qn = qname.toString();
                for (String ex : exclude) {
                    if (qn.startsWith(ex)) {
                        return true;
                    }
                }
            }
        }
        if (isIgnoreAnonymous()) {
            Matcher m = ANONYMOUS.matcher(qname);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isQuiet() {
        return quiet;
    }

    private String help(List<String> errors) {
        StringBuilder sb = new StringBuilder("Callgraph prints a graph of things that call each other in a tree of Java sources,\n"
                + "and can output graphs of what methods / classes / packages (or all of the above) call each other\nwithin that"
                + "source tree."
                + "\n\nUsage:\njava -jar callgraph.jar ");
        for (Command c : COMMANDS) {
            if (c.optional) {
                sb.append('[');
            }
            sb.append("--").append(c.name).append(" | -").append(c.shortcut);
            if (c.takesArgument) {
                sb.append(" ").append(c.name);
            }
            if (c.optional) {
                sb.append(']');
            }
            sb.append(' ');
        }
        sb.append("dir1 [dir2 dir3 ...]");
        sb.append('\n');
        for (Command c : COMMANDS) {
            sb.append("\n\t");
            sb.append("--").append(c.name).append(" / -").append(c.shortcut).append(" :\t").append(c.help());
        }
        if (!errors.isEmpty()) {
            sb.append("\nErrors:\n");
            for (String err : errors) {
                sb.append('\t').append(err).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean isIgnoreSinglePackage() {
        return ignoreShallowPackages;
    }

    private static abstract class Command {

        protected final String name;
        protected final String shortcut;
        protected final boolean optional;
        private final boolean takesArgument;

        Command(String name, String shortcut, boolean optional, boolean takesArgument) {
            this.name = name;
            this.shortcut = shortcut;
            this.optional = optional;
            this.takesArgument = takesArgument;
        }

        public int parse(int position, String[] args, Arguments toSet) {
            boolean match = ("-" + shortcut).equals(args[position])
                    || ("--" + name).equals(args[position]);
            if (!match) {
                return 0;
            }
            return doParse(position, args, toSet);
        }

        protected abstract int doParse(int i, String[] args, Arguments toSet);

        protected abstract String help();

        public String toString() {
            return name;
        }
    }
    
    private static final class NoOrphansCommand extends Command {
        NoOrphansCommand() {
            super(CMD_NO_ORPHANS, "N", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.noOrphans = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Do not include elements in the graph which have no "
                    + "connections at all";
        }
    }
    
    private static final class IncludeWeightsCommand extends Command {
        IncludeWeightsCommand() {
            super(CMD_WEIGHTS, "w", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.weights = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Include edge weights based on the number of times one element references another";
        }
    }

    private static final class NoSelfReferencesCommand extends Command {

        NoSelfReferencesCommand() {
            super(CMD_NOSELF, "n", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.noSelfReferences = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Hide intra-class calls (i.e. if Foo.bar() calls Foo.baz(), don't include it)";
        }
    }

    private static final class DisableEightBitStringsCommand extends Command {

        DisableEightBitStringsCommand() {
            super(CMD_DISABLE_EIGHT_BIT_STRINGS, "u", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.noSelfReferences = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Disable string memory optimizations - runs faster and supports unicode class names, but may run out of memory";
        }
    }

    private static final class ReverseCommand extends Command {

        ReverseCommand() {
            super(CMD_REVERSE, "r", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.reverse = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Reverse the edges of graph nodes";
        }
    }

    private static final class AggressiveCommand extends Command {

        AggressiveCommand() {
            super(CMD_AGGRESSIVE_MEMORY, "z", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.aggressive = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Aggressively optimize the 8-bit string intern table "
                    + "for large graphs, sacrificing performace for space";
        }
    }

    private static final class ShortNamesCommand extends Command {

        ShortNamesCommand() {
            super(CMD_SIMPLE, "s", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.shortNames = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Use simple class names without the package (may confuse results if two classes have the same name)";
        }
    }

    private static final class ExtendedPropertiesCommand extends Command {

        ExtendedPropertiesCommand() {
            super(CMD_EXTENDED_PROPERTIES, "x", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.xprop = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Find all maven projects that are children of the passed folders, and scan their src/main/java subfolders";
        }
    }

    private static final class AntCommand extends Command {

        AntCommand() {
            super(CMD_ANT, "a", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.ant = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Find all ant projects that are children of the passed folders, and scan their src/ subfolders";
        }
    }

    private static final class MavenCommand extends Command {

        MavenCommand() {
            super(CMD_MAVEN, "m", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.maven = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Find all maven projects that are children of the passed folders, and scan their src/main/java subfolders";
        }
    }

    private static final class GradleCommand extends Command {

        GradleCommand() {
            super(CMD_GRADLE, "g", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.gradle = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Find all gradle projects that are children of the passed folders, and scan their src/main/java subfolders";
        }
    }

    private static final class OmitAbstractCommand extends Command {

        OmitAbstractCommand() {
            super(CMD_OMIT_ABSTRACT, "b", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.omitAbstract = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Don't emit calls to abstract methods";
        }
    }

    private static final class VerboseCommand extends Command {

        VerboseCommand() {
            super(CMD_VERBOSE, "v", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.verbose = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Print output about what is being processed";
        }
    }

    private static final class IgnoreShallowPackagesCommand extends Command {

        IgnoreShallowPackagesCommand() {
            super(CMD_IGNORE_SINGLE_PACKAGE, "h", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.ignoreShallowPackages = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Ignore classes in packages just below the default package, "
                    + "a pattern frequently used in demo code.";
        }
    }

    private static final class IgnoreAnonymousClassesCommand extends Command {

        IgnoreAnonymousClassesCommand() {
            super(CMD_IGNORE_ANONYMOUS, "y", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.ignoreAnonymousClasses = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Ignore classes in packages just below the default package, "
                    + "a pattern frequently used in demo code.";
        }
    }

    private static final class QuietCommand extends Command {

        QuietCommand() {
            super(CMD_QUIET, "q", true, false);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            toSet.quiet = true;
            return 1;
        }

        @Override
        protected String help() {
            return "Ignore anonymous classes, e.g. com.foo.Bar.$4";
        }
    }

    private static final class OutfileCommand extends Command {

        OutfileCommand() {
            super(CMD_METHODGRAPH, "o", true, true);
        }

        @Override
        protected int doParse(int index, String[] args, Arguments toSet) {
            if (args.length == index + 1) {
                throw new IllegalArgumentException("--outfile or -o present but no output file specified");
            }
            toSet.outfile = new File(args[index + 1]);
            return 2;
        }

        @Override
        protected String help() {
            return "Set the output file for the method call graph";
        }
    }

    private static final class ClassGraphFileCommand extends Command {

        ClassGraphFileCommand() {
            super(CMD_CLASSGRAPH, "c", true, true);
        }

        @Override
        protected int doParse(int index, String[] args, Arguments toSet) {
            if (args.length == index + 1) {
                throw new IllegalArgumentException("--outfile or -o present but no output file specified");
            }
            toSet.classGraphFile = new File(args[index + 1]);
            return 2;
        }

        @Override
        protected String help() {
            return "Set the output file for the class call graph";
        }
    }

    private static final class PackageGraphFileCommand extends Command {

        PackageGraphFileCommand() {
            super(CMD_PACKAGEGRAPH, "p", true, true);
        }

        @Override
        protected int doParse(int index, String[] args, Arguments toSet) {
            if (args.length == index + 1) {
                throw new IllegalArgumentException("--outfile or -o present but no output file specified");
            }
            toSet.packageGraphFile = new File(args[index + 1]);
            return 2;
        }

        @Override
        protected String help() {
            return "Set the output file for the package call graph";
        }
    }

    private static final class ExcludeCommand extends Command {

        ExcludeCommand() {
            super("exclude", "e", true, true);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            if (args.length == i + 1) {
                throw new IllegalArgumentException("--exclude or -e present but no exclusion list present");
            }
            for (String s : args[i + 1].split(",")) {
                toSet.exclude.add(s);
                System.out.println("EXCLUDE '" + s + "'");
            }
            return 2;
        }

        @Override
        protected String help() {
            return "Exclude any relationships where the fully qualified class name starts with any pattern in this comma-delimited list of strings, e.g. -e foo.bar,foo.baz";
        }
    }

    private static final class IgnoreCommand extends Command {

        IgnoreCommand() {
            super(CMD_IGNORE, "i", true, true);
        }

        @Override
        protected int doParse(int i, String[] args, Arguments toSet) {
            if (args.length == i + 1) {
                throw new IllegalArgumentException("--exclude or -e present but no exclusion list present");
            }
            for (String s : args[i + 1].split(",")) {
                toSet.ignore.add(s.trim());
            }
            return 2;
        }

        @Override
        protected String help() {
            return "Comma delimited list of folders or subfolders to ignore, absolute or relative to the base directory.";
        }
    }

    static final class InvalidArgumentsException extends IllegalArgumentException {

        private final List<String> errors;

        InvalidArgumentsException(String msg, List<String> errors) {
            super(msg);
            this.errors = errors;
        }

        public List<String> errors() {
            return errors;
        }

        public boolean errorContains(String test) { //for tests
            for (String err : errors) {
                if (err.contains(test)) {
                    return true;
                }
            }
            return false;
        }
    }
}
