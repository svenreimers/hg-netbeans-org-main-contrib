/* 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 1997-2016 Oracle and/or its affiliates. All rights reserved.
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

import org.netbeans.lib.callgraph.Arguments.InvalidArgumentsException;
import static org.netbeans.lib.callgraph.CallgraphControl.CMD_CLASSGRAPH;
import static org.netbeans.lib.callgraph.CallgraphControl.CMD_DISABLE_EIGHT_BIT_STRINGS;
import static org.netbeans.lib.callgraph.CallgraphControl.CMD_EXCLUDE;
import static org.netbeans.lib.callgraph.CallgraphControl.CMD_MAVEN;
import static org.netbeans.lib.callgraph.CallgraphControl.CMD_METHODGRAPH;
import static org.netbeans.lib.callgraph.CallgraphControl.CMD_NOSELF;
import static org.netbeans.lib.callgraph.CallgraphControl.CMD_OMIT_ABSTRACT;
import static org.netbeans.lib.callgraph.CallgraphControl.CMD_PACKAGEGRAPH;
import static org.netbeans.lib.callgraph.CallgraphControl.CMD_QUIET;
import static org.netbeans.lib.callgraph.CallgraphControl.CMD_REVERSE;
import static org.netbeans.lib.callgraph.CallgraphControl.CMD_SIMPLE;
import org.netbeans.lib.callgraph.io.JavaFilesIterator;
import org.netbeans.lib.callgraph.javac.JavacRunner;
import org.netbeans.lib.callgraph.javac.SourceElement;
import org.netbeans.lib.callgraph.javac.SourcesInfo;
import org.netbeans.lib.callgraph.util.MergeIterator;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Scans a source folder and runs javac against any Java sources present, and
 * builds graphs of what calls what within those. The library classpath does not
 * need to be set, and the folders do not need to be package roots - we are
 * instructing javac to treat errors as non-fatal. The available sources will be
 * attributed.
 *
 * @author Tim Boudreau
 */
public final class Callgraph {

    private boolean noself;
    private boolean maven;
    private boolean quiet;
    private boolean simple;
    private final Set<String> excludes = new HashSet<>();
    private final Set<File> folders = new HashSet<>();
    private File classgraphFile;
    private File methodgraphFile;
    private File packagegraphFile;
    private Listener listener;
    private boolean omitAbstract;
    private boolean useJavaStrings;
    private boolean reverse;

    private Callgraph() {
    }

    /**
     * Configure a Callgraph to build.
     *
     * @return A call graph to configure
     */
    public static Callgraph configure() {
        return new Callgraph();
    }

    public static void main(String[] args) throws IOException {
        CallgraphControl arguments = null;
        try {
            arguments = new Arguments(args);
        } catch (InvalidArgumentsException ex) {
            // this will be a help message describing usage and the invalid
            // arguments
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        assert arguments != null;
        invoke(arguments, arguments.isVerbose() ? new LoggingListener() : null);
    }

    /**
     * Run javac and produce output.
     *
     * @param arguments The parsed arguments
     * @return The list of all methods found, sorted by qname
     * @throws IOException If i/o fails
     */
    static List<SourceElement> invoke(CallgraphControl arguments, Listener listener) throws IOException {
        SourcesInfo info = new SourcesInfo(arguments.isDisableEightBitStrings(), arguments.isAggressive());
        // Build an iterable of all Java sources (without collecting them all ahead of time)
        List<Iterable<File>> iterables = new LinkedList<>();
        for (File folder : arguments) {
            iterables.add(JavaFilesIterator.iterable(folder));
        }
        // The thing that will run javac
        JavacRunner runner = new JavacRunner(info, MergeIterator.toIterable(iterables), listener);
        AtomicReference<File> lastFile = new AtomicReference<>();
        Consumer<File> monitor = lastFile::set;
        // run javac
        Set<SourceElement> allElements = runner.go(monitor, lastFile);

        List<SourceElement> all = new ArrayList<>(allElements);
        // Sort, so textual output is more human-friendly
        Collections.sort(all);
        // Now write files and print output
        if (!all.isEmpty()) {
            PrintStream outStream = createPrintStreamIfNotNull(arguments.methodGraphFile());
            PrintStream packageStream = createPrintStreamIfNotNull(arguments.packageGraphFile());
            PrintStream classStream = createPrintStreamIfNotNull(arguments.classGraphFile());
            // duplicate avoidance
            Set<CharSequence> emittedPackageLines = new HashSet<>();
            Set<CharSequence> emittedClassLines = new HashSet<>();
            List<Object> clazz = new ArrayList<>(5);
            CharSequence lastClass = null;

            List<Object> pkg = new ArrayList<>(5);
            CharSequence lastPackage = null;
            
            try {
                // Iterate every method
                outer:
                for (SourceElement sce : all) {
                    if (arguments.isExcluded(sce.qname().toString())) { // Ignore matches
                        continue;
                    }
                    List<SourceElement> outbounds = new ArrayList<>(arguments.isReverse() ? sce.getInboundReferences() : sce.getOutboundReferences());
                    Collections.sort(outbounds); // also sort connections
                    // Iterate the current method's connections
                    List<Object> mth = new ArrayList<>(5);
                    CharSequence currClazz = arguments.isShortNames() ? sce.typeName() : info.strings.concat(sce.packageName(), info.strings.DOT, sce.typeName());
                    if (!currClazz.equals(lastClass)) {
                        if (classStream != null) {
                            writeLine(clazz, info, emittedClassLines, classStream);
                        }
                        clazz.clear();
                        lastClass = currClazz;
                    }
                    if (clazz.isEmpty()) {
                        clazz.add(currClazz);
                        if (arguments.isExtendedProperties()) {
                            clazz.add(sce.isAbstract());
                        }
                    }
                    CharSequence currPkg = sce.packageName();
                    if (pkg.isEmpty()) {
                        pkg.add(currPkg);
                    }
                    if (!currPkg.equals(lastPackage)) {
                        if (packageStream != null) {
                            writeLine(pkg, info, emittedPackageLines, packageStream);
                        }
                        lastPackage = currPkg;
                        pkg.clear();
                    }
                    for (SourceElement outbound : outbounds) {
                        if (arguments.isExcluded(outbound.qname().toString())) { // Ignore matches
                            continue;
                        }
                        // If we are ignoring abstract methods, do that - has no effect on classes
                        if (arguments.isOmitAbstract() && (outbound.isAbstract() | sce.isAbstract())) {
                            continue;
                        }
                        // If the argument is set, ignore cases where it's a class' method
                        // referencing another method on that class
                        if (!arguments.isSelfReferences() && sce.typeName().equals(outbound.typeName())) {
                            continue;
                        }
                        if (outStream != null || !arguments.isQuiet()) {
                            if (arguments.isShortNames()) {
                                mth.add(outbound.shortName());
                            } else {
                                mth.add(outbound.qname());
                            }
                            if (arguments.isExtendedProperties()) {
                                mth.add(outbound.isAbstract());
                            }
                        }
                        // Build the package graph output if necessary
                        if (packageStream != null) {
                            if (!outbound.packageName().equals(currPkg) || arguments.isSelfReferences()) {
                                pkg.add(outbound.packageName());
                            }
                        }
                        // Build the class graph output if necessary
                        if (classStream != null) {
                            CharSequence type1 = sce.typeName();
                            CharSequence type2 = outbound.typeName();
                            if (!arguments.isShortNames()) {
//                                type1 = info.strings.concat(sce.packageName(), info.strings.DOT, type1);
                                type2 = info.strings.concat(outbound.packageName(), info.strings.DOT, type2);
                            }
                            if (!type1.equals(type2) && !clazz.contains(type2)) {
                                clazz.add(type2);
                                if (arguments.isExtendedProperties()) {
                                    clazz.add(outbound.isAbstract());
                                }
                            }
                        }
                    }
                    if (!arguments.isQuiet() || outStream != null) {
                        if (!mth.isEmpty()) {
                            CharSequence nm = arguments.isShortNames() ? sce.shortName() : sce.qname();
                            List<Object> l = mth;
                            if (arguments.isExtendedProperties()) {
                                mth.add(0, sce.isAbstract());
                            }
                            l.add(0, nm);
                            CharSequence line = info.strings.concatQuoted(l);
                            if (!arguments.isQuiet()) {
                                System.out.println(line);
                            }
                            if (outStream != null) {
                                outStream.println(line);
                            }
                        }
                    }
                }
                if (classStream != null && !clazz.isEmpty()) {
                    writeLine(clazz, info, emittedClassLines, classStream);
                }
                if (packageStream != null && !pkg.isEmpty()) {
                    writeLine(pkg, info, emittedPackageLines, packageStream);
                }
            } finally {
                for (PrintStream ps : new PrintStream[]{outStream, packageStream, classStream}) {
                    if (ps != null) {
                        ps.close();
                    }
                }
            }
        }
        return all;
    }
    private static void writeLine(List<Object> clazz, SourcesInfo info, Set<CharSequence> emittedClassLines, PrintStream classStream) {
        if (!clazz.isEmpty()) {
            CharSequence cs = info.strings.concatQuoted(clazz);
            if (!emittedClassLines.contains(cs)) {
                classStream.println(cs);
                emittedClassLines.add(cs);
            }
        }
    }

    private static PrintStream createPrintStreamIfNotNull(File outputFile) throws IOException {
        PrintStream outStream = null;
        if (outputFile != null) {
            if (!outputFile.exists()) {
                if (!outputFile.createNewFile()) {
                    throw new IllegalStateException("Could not create " + outputFile);
                }
            }
            outStream = new PrintStream(outputFile);
        }
        return outStream;
    }

    public List<String> toCommandLineArguments() {
        List<String> args = new ArrayList<>();
        addBoolean(CMD_NOSELF, noself, args);
        addBoolean(CMD_MAVEN, maven, args);
        addBoolean(CMD_QUIET, quiet, args);
        addBoolean(CMD_SIMPLE, simple, args);
        addBoolean(CMD_REVERSE, reverse, args);
        addBoolean(CMD_DISABLE_EIGHT_BIT_STRINGS, useJavaStrings, args);
        addBoolean(CMD_OMIT_ABSTRACT, omitAbstract, args);
        if (!excludes.isEmpty()) {
            StringBuilder concat = new StringBuilder();
            for (Iterator<String> it = excludes.iterator(); it.hasNext();) {
                String next = it.next();
                concat.append(next);
                if (it.hasNext()) {
                    concat.append(",");
                }
            }
            args.add("--" + CMD_EXCLUDE);
            args.add(concat.toString());
        }
        addFile(CMD_CLASSGRAPH, classgraphFile, args);
        addFile(CMD_METHODGRAPH, methodgraphFile, args);
        addFile(CMD_PACKAGEGRAPH, packagegraphFile, args);
        for (File fld : folders) {
            args.add(fld.getAbsolutePath());
        }
        return args;
    }

    CallgraphControl build() throws IOException {
        List<String> args = toCommandLineArguments();
        // We use regular string processing so we get the argument validation
        String[] argList = args.toArray(new String[args.size()]);
        Arguments arguments = new Arguments(false, argList);
        return arguments;
    }

    /**
     * Run this callgraph, writing output and returning the set of all methods
     * found, sorted in qname order. Call SourceElement.getInboundReferences()
     * and SourceElement.getOutboundReferences() to explore the graph.
     *
     * @return Sorted set of source elements
     * @throws IOException If i/o fails
     */
    public List<SourceElement> run() throws IOException {
        return Callgraph.invoke(build(), listener);
    }

    private void addFile(String command, File file, List<String> args) {
        if (file != null) {
            args.add("--" + command);
            args.add(file.getAbsolutePath());
        }
    }

    private void addBoolean(String command, boolean val, List<String> args) {
        if (val) {
            args.add("--" + command);
        }
    }

    public Callgraph setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public Callgraph packageGraphOutput(File file) {
        packagegraphFile = file;
        return this;
    }

    public Callgraph methodGraphOutput(File file) {
        methodgraphFile = file;
        return this;
    }

    public Callgraph classGraphOutput(File file) {
        classgraphFile = file;
        return this;
    }

    public Callgraph addSourceParent(File folder) {
        folders.add(folder);
        return this;
    }

    public Callgraph excludePrefix(String prefix) {
        excludes.add(prefix);
        return this;
    }

    public Callgraph useSimpleClassNames() {
        simple = true;
        return this;
    }

    public Callgraph quiet() {
        quiet = true;
        return this;
    }

    public Callgraph ignoreSelfReferences() {
        noself = true;
        return this;
    }

    public Callgraph scanFoldersForMavenProjects() {
        maven = true;
        return this;
    }

    public Callgraph reverse() {
        reverse = true;
        return this;
    }

    public Callgraph useJavaStrings() {
        useJavaStrings = true;
        return this;
    }

    public Callgraph omitAbstract() {
        omitAbstract = true;
        return this;
    }

    private static final class LoggingListener implements Listener {

        @Override
        public void onStart() {
            //do nothing
        }

        @Override
        public void onFinish() {
            System.out.println("Done.");
        }

        @Override
        public void onStartActivity(String activity, int steps) {
            if (steps > 0) {
                System.out.println(activity + " (" + steps + " steps)");
            }
        }

        @Override
        public void onStep(String step) {
            System.out.println("\t" + step);
        }
    }
}
