/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.erlang.platform.api;

import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.erlang.makeproject.spi.support.EditableProperties;
import org.netbeans.modules.erlang.makeproject.spi.support.PropertyUtils;
import org.netbeans.modules.erlang.platform.Util;
import org.netbeans.modules.erlang.platform.api.RubyPlatform.Info;
import org.netbeans.modules.languages.execution.ExecutionService;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.Utilities;

/**
 * Represents one Ruby platform, i.e. installation of a Ruby interpreter.
 */
public final class RubyPlatformManager {

    public static final boolean PREINDEXING = Boolean.getBoolean("gsf.preindexing");
    
    private static final String[] RUBY_EXECUTABLE_NAMES = { "erl" }; // NOI18N
    
    /** For unit tests. */
    static Properties TEST_RUBY_PROPS;

    private static final String PLATFORM_PREFIX = "erlangplatform."; // NOI18N
    private static final String PLATFORM_INTEPRETER = ".interpreter"; // NOI18N
    private static final String PLATFORM_ID_DEFAULT = "default"; // NOI18N

    private static final Logger LOGGER = Logger.getLogger(RubyPlatformManager.class.getName());
    
    private static Set<RubyPlatform> platforms;
    /**
     * Change support for notifying of platform changes, using vetoable for 
     * making it possible to prevent removing of a used platform.
     */
    private static final VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(RubyPlatformManager.class);

    private RubyPlatformManager() {
        // static methods only
    }

    /**
     * So far, for unit tests only.
     * <p>
     * Resets platforms cache.
     */
    static void resetPlatforms() {
        platforms = null;
        firePlatformsChanged();
    }
    /**
     * Get a set of all registered platforms.
     */
    public static synchronized Set<RubyPlatform> getPlatforms() {
        return new HashSet<RubyPlatform>(getPlatformsInternal());
    }

    public static void performPlatformDetection() {
        // Check the path to see if we find any other Ruby installations
        String path = System.getenv("PATH"); // NOI18N
        if (path == null) {
            path = System.getenv("Path"); // NOI18N
        }

        if (path != null) {
            final Set<File> rubies = new LinkedHashSet<File>();
            Set<String> dirs = new TreeSet<String>(Arrays.asList(path.split(File.pathSeparator)));
            for (String dir : dirs) {
                for (String ruby : RUBY_EXECUTABLE_NAMES) {
                    File f = findPlatform(dir, ruby);
                    if (f != null) {
                        rubies.add(f);
                    }
                }
            }

            for (File ruby : rubies) {
                try {
                    if (getPlatformByFile(ruby) == null) {
                        addPlatform(ruby);
                    }
                } catch (IOException e) {
                    // tell the user that something goes wrong
                    LOGGER.log(Level.WARNING, e.getLocalizedMessage(), e);
                }
            }
        }
        Util.setFirstPlatformTouch(false);
        
    }

    private static void firePlatformsChanged() {
        try {
            vetoableChangeSupport.fireVetoableChange("platforms", null, null); //NOI18N
        } catch (PropertyVetoException ex) {
            // do nothing, vetoing not implemented yet
        }
    }
    private static File findPlatform(final String dir, final String erl) {
        File f = null;
        if (Utilities.isWindows()) {
            f = new File(dir, erl + ".exe"); // NOI18N
        } else {
            f = new File(dir, erl); // NOI18N
            // Don't include /usr/bin/ruby on the Mac - it's no good
            // Source: http://developer.apple.com/tools/rubyonrails.html
            //   "The version of Ruby that shipped on Mac OS X Tiger prior to 
            //    v10.4.6 did not work well with Rails.   If you're running 
            //    an earlier version of Tiger, you'll need to either upgrade 
            //    to 10.4.6 or upgrade your copy of Ruby to version 1.8.4 or 
            //    later using the open source distribution."
            if (erl.equals("erl") && Utilities.isMac() && "/usr/bin/erl".equals(f.getPath())) { // NOI18N
                String version = System.getProperty("os.version"); // NOI18N
                if (version == null || version.startsWith("10.4")) { // Only a problem on Tiger // NOI18N
                    return null;
                }
            }
        }
        if (f.isFile()) {
            return f;
        }
        return null;
    }

    private static Set<RubyPlatform> getPlatformsInternal() {
        if (platforms == null) {
            platforms = new HashSet<RubyPlatform>();

            // Test and preindexing hook
            String hardcodedRuby = System.getProperty("erlang.interpreter");
            if (hardcodedRuby != null) {
                Info info = new Info("User-specified Erlang", "0.1");

                FileObject gems = FileUtil.toFileObject(new File(hardcodedRuby)).getParent().getParent().getFileObject("lib/ruby/gems/1.8");
                if (gems != null) {
                    Properties props = new Properties();
                    props.setProperty(Info.RUBY_KIND, "User-specified Erlang");
                    props.setProperty(Info.RUBY_VERSION, "0.1");
                    String gemHome = FileUtil.toFile(gems).getAbsolutePath();
                    props.setProperty(Info.GEM_HOME, gemHome);
                    props.setProperty(Info.GEM_PATH, gemHome);
                    props.setProperty(Info.GEM_VERSION, "1.0.1 (1.0.1)");
                    info = new Info(props);
                }

                platforms.add(new RubyPlatform(PLATFORM_ID_DEFAULT, hardcodedRuby, info));
                return platforms;
            }
            
            Map<String, String> p = PropertyUtils.sequentialPropertyEvaluator(null,
                    PropertyUtils.globalPropertyProvider()).getProperties();
            if (p == null) { // #115909
                p = Collections.emptyMap();
            }
            boolean foundDefault = false;
            for (Map.Entry<String, String> entry : p.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(PLATFORM_PREFIX) && key.endsWith(PLATFORM_INTEPRETER)) {
                    String id = key.substring(PLATFORM_PREFIX.length(),
                            key.length() - PLATFORM_INTEPRETER.length());
                    String idDot = id + '.';
                    Properties props = new Properties();
                    String kind = p.get(PLATFORM_PREFIX + idDot + Info.RUBY_KIND);
                    if (kind == null) { // not supporting old 6.0 platform, skip
                        continue;
                    }
                    props.put(Info.RUBY_KIND, kind);
                    props.put(Info.RUBY_VERSION, p.get(PLATFORM_PREFIX + idDot + Info.RUBY_VERSION));
                    String jrubyVersion = p.get(PLATFORM_PREFIX + idDot + Info.JRUBY_VERSION);
                    if (jrubyVersion != null) {
                        props.put(Info.JRUBY_VERSION, jrubyVersion);
                    }
                    String patchLevel = p.get(PLATFORM_PREFIX + idDot + Info.RUBY_PATCHLEVEL);
                    if (patchLevel != null){
                        props.put(Info.RUBY_PATCHLEVEL, patchLevel);
                    }
                    String releaseDate = p.get(PLATFORM_PREFIX + idDot + Info.RUBY_RELEASE_DATE);
                    if (releaseDate != null) {
                        props.put(Info.RUBY_RELEASE_DATE, releaseDate);
                    }
//                    props.put(Info.RUBY_EXECUTABLE, p.get(PLATFORM_PREFIX + idDot + Info.RUBY_EXECUTABLE));
                    String platform = p.get(PLATFORM_PREFIX + idDot + Info.RUBY_PLATFORM);
                    if (platform != null) {
                        props.put(Info.RUBY_PLATFORM, platform);
                    }
                    String gemHome = p.get(PLATFORM_PREFIX + idDot + Info.GEM_HOME);
                    if (gemHome != null) {
                        props.put(Info.GEM_HOME, gemHome);
                        props.put(Info.GEM_PATH, p.get(PLATFORM_PREFIX + idDot + Info.GEM_PATH));
                        props.put(Info.GEM_VERSION, p.get(PLATFORM_PREFIX + idDot + Info.GEM_VERSION));
                    }
                    String interpreterPath = entry.getValue();
                    Info info = new Info(props);
                    platforms.add(new RubyPlatform(id, interpreterPath, info));
                    foundDefault |= id.equals(PLATFORM_ID_DEFAULT);
                }
            }
            if (!foundDefault) {
                String loc = RubyInstallation.getInstance().getInterpreterInEnv();
                if (loc != null) {
                    platforms.add(new RubyPlatform(PLATFORM_ID_DEFAULT, loc, Info.forDefaultPlatform(loc)));
                }
            }
            LOGGER.fine("ErlangPlatform initial list: " + platforms);
        }

        return platforms;
    }

    /** Typically bundled JRuby. */
    public static RubyPlatform getDefaultPlatform() {
        RubyPlatform defaultPlatform = RubyPlatformManager.getPlatformByID(PLATFORM_ID_DEFAULT);
        assert defaultPlatform != null : "Cannot find default platform";
        return defaultPlatform;
    }

    /**
     * Find a platform by its ID.
     * @param id an ID (as in {@link #getID})
     * @return the platform with that ID, or null
     */
    public static synchronized RubyPlatform getPlatformByID(String id) {
        for (RubyPlatform p : getPlatformsInternal()) {
            if (p.getID().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public static synchronized RubyPlatform getPlatformByFile(File interpreter) {
        for (RubyPlatform p : getPlatformsInternal()) {
            try {
                File current = new File(p.getInterpreter()).getCanonicalFile();
                File toFind = interpreter.getCanonicalFile();
                if (current.equals(toFind)) {
                    return p;
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
        return null;
    }
    
    public static synchronized RubyPlatform getPlatformByPath(String path) {
        return getPlatformByFile(new File(path));
    }

    public static RubyPlatform addPlatform(final File interpreter) throws IOException {
        final Info info = computeInfo(interpreter);
        if (info == null) {
            return null;
        }

        final String id = computeID(info.getKind());
        try {
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                public Void run() throws IOException {
                    if (getPlatformByID(id) != null) {
                        throw new IOException("ID " + id + " already taken"); // NOI18N
                    }
                    EditableProperties props = PropertyUtils.getGlobalProperties();
                    putPlatformProperties(id, interpreter, info, props);
                    PropertyUtils.putGlobalProperties(props);
                    return null;
                }
            });
        } catch (MutexException e) {
            throw (IOException) e.getException();
        }
        RubyPlatform plaf = new RubyPlatform(id, interpreter.getAbsolutePath(), info);
        synchronized (RubyPlatform.class) {
            getPlatformsInternal().add(plaf);
        }
        firePlatformsChanged();
        LOGGER.fine("ErlangPlatform added: " + plaf);
        return plaf;
    }

    public static void removePlatform(final RubyPlatform plaf) throws IOException {
        try {
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                public Void run() throws IOException {
                    EditableProperties props = PropertyUtils.getGlobalProperties();
                    clearProperties(plaf, props);
                    PropertyUtils.putGlobalProperties(props);
                    return null;
                }
            });
        } catch (MutexException e) {
            throw (IOException) e.getException();
        }
        synchronized (RubyPlatform.class) {
            getPlatformsInternal().remove(plaf);
        }
        firePlatformsChanged();
        LOGGER.fine("ErlangPlatform removed: " + plaf);
    }

    public static void storePlatform(final RubyPlatform plaf) throws IOException {
        try {
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                public Void run() throws IOException {
                    EditableProperties props = PropertyUtils.getGlobalProperties();
                    clearProperties(plaf, props);
                    putPlatformProperties(plaf.getID(), plaf.getInterpreterFile(), plaf.getInfo(), props);
                    PropertyUtils.putGlobalProperties(props);
                    return null;
                }
            });
        } catch (MutexException e) {
            throw (IOException) e.getException();
        }
        LOGGER.fine("ErlangPlatform stored: " + plaf);
    }

    private static void clearProperties(RubyPlatform plaf, EditableProperties props) {
        String id = PLATFORM_PREFIX + plaf.getID();
        props.remove(id + PLATFORM_INTEPRETER);
        String idDot = id + '.';
        props.remove(PLATFORM_PREFIX + idDot + Info.RUBY_KIND);
        props.remove(PLATFORM_PREFIX + idDot + Info.RUBY_VERSION);
        props.remove(PLATFORM_PREFIX + idDot + Info.JRUBY_VERSION);
        props.remove(PLATFORM_PREFIX + idDot + Info.RUBY_PATCHLEVEL);
        props.remove(PLATFORM_PREFIX + idDot + Info.RUBY_RELEASE_DATE);
//                    props.remove(PLATFORM_PREFIX + idDot + Info.RUBY_EXECUTABLE);
        props.remove(PLATFORM_PREFIX + idDot + Info.RUBY_PLATFORM);
        props.remove(PLATFORM_PREFIX + idDot + Info.GEM_HOME);
        props.remove(PLATFORM_PREFIX + idDot + Info.GEM_PATH);
        props.remove(PLATFORM_PREFIX + idDot + Info.GEM_VERSION);
    }
    
    private static void putPlatformProperties(final String id, final File interpreter,
            final Info info, final EditableProperties props) throws FileNotFoundException {
        String interpreterKey = PLATFORM_PREFIX + id + PLATFORM_INTEPRETER;
        props.setProperty(interpreterKey, interpreter.getAbsolutePath());
        if (!interpreter.isFile()) {
            throw new FileNotFoundException(interpreter.getAbsolutePath());
        }
        String idDot = id + '.';
        props.setProperty(PLATFORM_PREFIX + idDot + Info.RUBY_KIND, info.getKind());
        props.setProperty(PLATFORM_PREFIX + idDot + Info.RUBY_VERSION, info.getVersion());
        if (info.getPatchlevel() != null) {
            props.setProperty(PLATFORM_PREFIX + idDot + Info.RUBY_PATCHLEVEL, info.getPatchlevel());
        }
        if (info.getReleaseDate() != null) {
            props.setProperty(PLATFORM_PREFIX + idDot + Info.RUBY_RELEASE_DATE, info.getReleaseDate());
        }
        //                    props.setProperty(PLATFORM_PREFIX + idDot + Info.RUBY_EXECUTABLE, info.getExecutable());
        if (info.getPlatform() != null) {
            props.setProperty(PLATFORM_PREFIX + idDot + Info.RUBY_PLATFORM, info.getPlatform());
        }
        if (info.getGemHome() != null) {
            props.setProperty(PLATFORM_PREFIX + idDot + Info.GEM_HOME, info.getGemHome());
            props.setProperty(PLATFORM_PREFIX + idDot + Info.GEM_PATH, info.getGemPath());
            props.setProperty(PLATFORM_PREFIX + idDot + Info.GEM_VERSION, info.getGemVersion());
        }
    }

    private static String computeID(final String label) {
        String base = label.replaceAll("[\\. ]", "_"); // NOI18N
        String id = base;
        for (int i = 0; getPlatformByID(id) != null; i++) {
            id = base + '_' + i;
        }
        return id;
    }

    public static Iterator<RubyPlatform> platformIterator() {
        return getPlatformsInternal().iterator();
    }

    private static Info computeInfo(final File interpreter) {
        if (TEST_RUBY_PROPS != null) { // tests
            return new Info(TEST_RUBY_PROPS);
        }
        Info info = null;

        BufferedWriter stdWriter = null;
        BufferedReader stdReader = null;
        BufferedReader errReader = null;
        try {
            Process process = Runtime.getRuntime().exec("erl");

            stdWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            stdReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            stdWriter.append("code:root_dir().");
            stdWriter.newLine();
            stdWriter.append("init:stop().");
            stdWriter.newLine();
            stdWriter.flush();

            try {
                int sucessed = process.waitFor();
                if (sucessed != 0) {
                    //ErrorManager.().notify(new Exception(
                    //        "Erlang installation may not be set, or is invalid.\n" +
                    //        "Please set Erlang installation via [Tools]->[Options]->[Miscellanous]"));
                } else {
                    String line = null;
                    while ((line = errReader.readLine()) != null) {
                        System.out.println(line);
                    }
                    String id = null;
                    String version = "1.0";
                    while ((line = stdReader.readLine()) != null) {
                        System.out.println(line);
                        if (line.toLowerCase().contains("eshell")) {
                            id = line;
                        }
                        String[] groups = line.split(">");
                        if (groups.length >= 2 && groups[0].trim().equals("1")) {
                            String basePath = groups[1].trim();
                            basePath = basePath.replace("\"", "");
                            String erlExeFileName = Utilities.isWindows() ? "erl.exe" : "erl";
                            String erlPath = basePath + File.separator + "bin" + File.separator + erlExeFileName;                  
                            break;
                        }
                    }
                    if (id == null) {
                        id = "Erlang";
                    }
                    info = new Info(id, version);
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Not a erlang platform: " + interpreter.getAbsolutePath()); // NOI18N
        } finally {
            try {
                if (stdWriter != null) {
                    stdWriter.close();
                }
                if (stdReader != null) {
                    stdReader.close();
                }
                if (errReader != null) {
                    errReader.close();
                }
            } catch (IOException ex) {
            }
        }
            
        return info;
    }
    
    public static void addVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChangeSupport.addVetoableChangeListener(listener);
    }
    
    public static void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChangeSupport.removeVetoableChangeListener(listener);
    }

}
