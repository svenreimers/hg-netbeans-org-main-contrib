/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jnlpmodules;
import java.io.IOException;
import java.io.InputStream;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.netbeans.ProxyClassLoader;
import org.netbeans.Events;
import org.netbeans.InvalidException;
import org.netbeans.Module;
import org.netbeans.Module.PackageExport;
import org.netbeans.ModuleManager;
import org.netbeans.Util;
import org.netbeans.core.startup.ModuleHistory;
import org.openide.ErrorManager;
import org.openide.modules.Dependency;
import org.openide.util.NbBundle;

/**
 */
public final class ClasspathModule extends Module {
    
    static final ErrorManager err = ErrorManager.getDefault().getInstance("org.netbeans.modules.jnlpmodules"); // NOI18N
    
    /** Map from extension JARs to sets of JAR that load them via Class-Path.
     * Used only for debugging purposes, so that a warning is printed if two
     * different modules try to load the same extension (which would cause them
     * to both load their own private copy, which may not be intended).
     */
    private static final Map extensionOwners = new HashMap(); // Map<File,Set<File>>

    /** Set of locale-variants JARs for this module (or null).
     * Added explicitly to classloader, and can be used by execution engine.
     */
    private Set localeVariants = null; // Set<String>
    /** Set of extension JARs that this module loads via Class-Path (or null).
     * Can be used e.g. by execution engine. (#9617)
     */
    private Set plainExtensions = null; // Set<String>
    /** Set of localized extension JARs derived from plainExtensions (or null).
     * Used to add these to the classloader. (#9348)
     * Can be used e.g. by execution engine.
     */
    private Set localeExtensions = null; // Set<String>
    /** Patches added at the front of the classloader (or null).
     * Files are assumed to be JARs; directories are themselves.
     */
    private Set patches = null; // Set<String>
    
    /** localized properties, only non-null if requested from disabled module */
    private Properties localizedProps;

    private JNLPModuleFactory factory;
    
    /** module classloaders that might have been created before */
//    private final Set oldClassLoaders = new WeakSet(3); // Set<OneModuleClassLoader>
    
    String prefixURL; // non null for prefix modules
    private String location; // non null for prefix modules
    private ClassLoader delegate; // non null for prefix modules


    /** Use ModuleManager.create as a factory. */
    public ClasspathModule(ModuleManager mgr, Events ev, Manifest manifest, Object history, String prefixURL, String location, ClassLoader delegate, JNLPModuleFactory factory) throws IOException {
        super(mgr, ev, new ModuleHistory(prefixURL), false, 
                (location != null) && (location.equals("modules/autoload")), 
                (location != null) && (location.equals("modules/eager")));
        this.factory = factory;
        this.manifest = manifest;
        this.prefixURL = prefixURL;
        this.location = location;
        this.delegate = delegate;
//        loadLocalizedPropsClasspath();
        parseManifest();
        findExtensionsAndVariants(manifest);
    }
    
    private void setupClassloader() {
        try {
            // Calculate the parents to initialize the classloader with.
            Dependency[] dependencies = getDependenciesArray();
            Set parents = new HashSet(dependencies.length * 4 / 3 + 1);
            for (int i = 0; i < dependencies.length; i++) {
                Dependency dep = dependencies[i];
                if (dep.getType() != Dependency.TYPE_MODULE) {
                    // Token providers do *not* go into the parent classloader
                    // list. The providing module must have been turned on first.
                    // But you cannot automatically access classes from it.
                    continue;
                }
                String name = (String)Util.parseCodeName(dep.getName())[0];
                Module parent = getManager().get(name);
                // Should not happen:
                if (parent == null) throw new IOException("Parent " + name + " not found!"); // NOI18N
                parents.add(parent);
            }
            classLoaderUp(parents);
        } catch (IOException ioe) {
            IllegalStateException ie = new IllegalStateException(ioe.toString());
            err.annotate(ie, ioe);
            throw ie;
        }
    }
    
    /** Get a localized attribute.
     * First, if OpenIDE-Module-Localizing-Bundle was given, the specified
     * bundle file (in all locale JARs as well as base JAR) is searched for
     * a key of the specified name.
     * Otherwise, the manifest's main attributes are searched for an attribute
     * with the specified name, possibly with a locale suffix.
     * If the attribute name contains a slash, and there is a manifest section
     * named according to the part before the last slash, then this section's attributes
     * are searched instead of the main attributes, and for the attribute listed
     * after the slash. Currently this would only be useful for localized filesystem
     * names. E.g. you may request the attribute org/foo/MyFileSystem.class/Display-Name.
     * In the future certain attributes known to be dangerous could be
     * explicitly suppressed from this list; should only be used for
     * documented localizable attributes such as OpenIDE-Module-Name etc.
     */
    public Object getLocalizedAttribute(String attr) {
        String locb = getManifest().getMainAttributes().getValue("OpenIDE-Module-Localizing-Bundle"); // NOI18N
        boolean usingLoader = false;
        if (locb != null) {
            if (classloader != null) {
                if (locb.endsWith(".properties")) { // NOI18N
                    usingLoader = true;
                    String basename = locb.substring(0, locb.length() - 11).replace('/', '.');
                    try {
                        ResourceBundle bundle = NbBundle.getBundle(basename, Locale.getDefault(), classloader);
                        try {
                            return bundle.getString(attr);
                        } catch (MissingResourceException mre) {
                            // Fine, ignore.
                        }
                    } catch (MissingResourceException mre) {
                        err.notify(mre);
                    }
                } else {
                    err.log(ErrorManager.WARNING, "WARNING - cannot efficiently load non-*.properties OpenIDE-Module-Localizing-Bundle: " + locb);
                }
            }
            if (!usingLoader) {
                if (localizedProps != null) {
                    String val = localizedProps.getProperty(attr);
                    if (val != null) {
                        return val;
                    }
                } else {
                    err.log("Trying to get localized attr " + attr + " from disabled module " + getCodeNameBase());
                }
            }
        }
        // Try in the manifest now.
        int idx = attr.lastIndexOf('/'); // NOI18N
        if (idx == -1) {
            // Simple main attribute.
            return NbBundle.getLocalizedValue(getManifest().getMainAttributes(), new Attributes.Name(attr));
        } else {
            // Attribute of a manifest section.
            String section = attr.substring(0, idx);
            String realAttr = attr.substring(idx + 1);
            Attributes attrs = getManifest().getAttributes(section);
            if (attrs != null) {
                return NbBundle.getLocalizedValue(attrs, new Attributes.Name(realAttr));
            } else {
                return null;
            }
        }
    }
    
    /** Check if this is a "fixed" module.
     * Fixed modules are installed automatically (e.g. based on classpath)
     * and cannot be uninstalled or manipulated in any way.
     */
    public boolean isFixed() {
        return getCodeName().equals("org.netbeans.bootstrap/1") ||
               getCodeName().equals("org.netbeans.core.startup/1") ||
               getCodeName().equals("org.netbeans.modules.jnlpmodules") ||
               getCodeName().equals("org.openide.modules") ||
               getCodeName().equals("org.openide.util") ||
               getCodeName().equals("org.openide.filesystems"); // ??? getCodeName().equals("org.netbeans.libs.xerces/1") ||
    }

    /** Find any extensions loaded by the module, as well as any localized
     * variants of the module or its extensions.
     */
    private void findExtensionsAndVariants(Manifest m) {
        localeVariants = null;
        List l = findLocaleVariantsOf(location, prefixURL);
        if (!l.isEmpty()) localeVariants = new HashSet(l);
        plainExtensions = null;
        localeExtensions = null;
        String classPath = m.getMainAttributes().getValue(Attributes.Name.CLASS_PATH);
        if (classPath != null) {
            StringTokenizer tok = new StringTokenizer(classPath);
            while (tok.hasMoreTokens()) {
                String ext = tok.nextToken();
                if (ext.indexOf("../") != -1) { // NOI18N
                    err.log(ErrorManager.WARNING, "WARNING: Class-Path value " + ext + " from " + this + " is illegal according to the Java Extension Mechanism: must be relative and not move up directories");
                }
                String extName = ext.substring(0, ext.lastIndexOf('.')); // without suffix
                String subLocation = null; // location of the extension
                if (extName.lastIndexOf('/') < 0) {
                    subLocation = location;
                } else {
                    subLocation = location + "/" + extName.substring(0, extName.lastIndexOf('/'));
                    extName = extName.substring(extName.lastIndexOf('/')+1, extName.length());
                }
                err.log("subLocation " + subLocation + " extName " + extName);
                Set s = factory.getPrefixNonModules(subLocation);
                err.log("    set under subLocation " + s);
                String extPrefix = null;
                for (Iterator it = s.iterator(); it.hasNext(); ) {
                    String p = (String)it.next();
                    if (p.indexOf("RM")>=0) { // using "RM"+ is an ugly hack that should be removed
                        if (p.indexOf("RM"+extName+".jar")>=0) { 
                            extPrefix = p;
                            break;
                        }
                    } else { // "RM" is not part of the URL, try without this "anchor"
                        if (p.indexOf(extName+".jar")>=0) { 
                            extPrefix = p;
                            break;
                        }
                    }
                }
                if (extPrefix == null) {
                    // Ignore unloadable extensions.
                    continue;
                }                    
                //No need to sync on extensionOwners - we are in write mutex
                    Set owners = (Set)extensionOwners.get(extPrefix);
                    if (owners == null) {
                        owners = new HashSet(2);
                        owners.add(prefixURL);
                        extensionOwners.put(extPrefix, owners);
                    } else if (! owners.contains(prefixURL)) {
                        owners.add(prefixURL);
                        events.log(Events.EXTENSION_MULTIPLY_LOADED, extName, owners);
                    } // else already know about it (OK or warned)
                if (plainExtensions == null) plainExtensions = new HashSet();
                plainExtensions.add(extPrefix);
                l = findLocaleVariantsOf(subLocation, extPrefix);
                if (!l.isEmpty()) {
                    if (localeExtensions == null) localeExtensions = new HashSet();
                    localeExtensions.addAll(l);
                }
            }
        }
        // #9273: load any modules/patches/this-code-name/*.jar files first:
        String patchdir = location + "/patches/" + getCodeName().replace('.', '-').replace('/', '-');
        patches = factory.getPrefixNonModules(patchdir);
        
        err.log("localeVariants of " + prefixURL + ": " + localeVariants);
        err.log("plainExtensions of " + prefixURL + ": " + plainExtensions);
        err.log("localeExtensions of " + prefixURL + ": " + localeExtensions);
        err.log("patches of " + prefixURL + ": " + patches);
        if (patches != null) {
            Iterator it = patches.iterator();
            while (it.hasNext()) {
                events.log(Events.PATCH, it.next());
            }
        }
    }
    
   /** Find existing locale variants of f, in search order.
     * Returns list of prefixes.
     */
    private List findLocaleVariantsOf(String loc, String name) {
        Set s = factory.getPrefixNonModules(loc + "/locale"); //NOI18N
        if (s == null || s.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        String jarFileName = factory.getJarFileName(name);
        if (jarFileName == null) {
            return Collections.EMPTY_LIST;
        }
        // cut off .jar
        jarFileName = jarFileName.substring(0, jarFileName.length() - 4);
        List l = new ArrayList(7); // List<String>
        Iterator it = NbBundle.getLocalizingSuffixes();
        while (it.hasNext()) {
            String suffix = (String)it.next();
            String entry = jarFileName + suffix + ".jar";
            for (Iterator i = s.iterator(); i.hasNext(); ) {
                String p = (String)i.next();
                if (p.indexOf(entry) >= 0) {
                    l.add(p);
                }
            }
        }
        return l;
    }
    
    private void loadLocalizedPropsClasspath() throws InvalidException {
        Attributes attr = manifest.getMainAttributes();
        String locbundle = attr.getValue("OpenIDE-Module-Localizing-Bundle"); // NOI18N
        if (locbundle != null) {
            err.log("Localized props in " + locbundle + " for " + attr.getValue("OpenIDE-Module"));
            try {
                int idx = locbundle.lastIndexOf('.'); // NOI18N
                String name, ext;
                if (idx == -1) {
                    name = locbundle;
                    ext = ""; // NOI18N
                } else {
                    name = locbundle.substring(0, idx);
                    ext = locbundle.substring(idx);
                }
                List suffixes = new ArrayList(10);
                Iterator it = NbBundle.getLocalizingSuffixes();
                while (it.hasNext()) {
                    suffixes.add(it.next());
                }
                Collections.reverse(suffixes);
                it = suffixes.iterator();
                while (it.hasNext()) {
                    String suffix = (String)it.next();
                    String resource = name + suffix + ext;
                    InputStream is = classloader.getResourceAsStream(resource);
                    if (is != null) {
                        err.log("Found " + resource);
                        if (localizedProps == null) {
                            localizedProps = new Properties();
                        }
                        localizedProps.load(is);
                    }
                }
                if (localizedProps == null) {
                    throw new IOException("Could not find localizing bundle: " + locbundle); // NOI18N
                }
            } catch (IOException ioe) {
                InvalidException e = new InvalidException(ioe.toString());
                err.annotate(e, ioe);
                throw e;
            }
        }
    }
    
    /** Set whether this module is supposed to be reloadable.
     * Has no immediate effect, only impacts what happens the
     * next time it is enabled (after having been disabled if
     * necessary).
     * Must be called from within a write mutex.
     * @param r whether the module should be considered reloadable
     */
    public void setReloadable(boolean r) {
        throw new IllegalStateException();
    }
    
    public List getAllJars() {
        return Collections.EMPTY_LIST;
    }
 
    /** Reload this module. Access from ModuleManager.
     * If an exception is thrown, the module is considered
     * to be in an invalid state.
     */
    public void reload() throws IOException {
        throw new IllegalStateException();
    }
    
    // Access from ModuleManager:
    /** Turn on the classloader. Passed a list of parent modules to use.
     * The parents should already have had their classloaders initialized.
     */
    public void classLoaderUp(Set parents) throws IOException {
        err.log("classLoaderUp on " + this + " with parents " + parents);
        // Find classloaders for dependent modules and parent to them.
        List loaders = new ArrayList(parents.size() + 1); // List<ClassLoader>
        loaders.add(factory.getClasspathDelegateClassLoader(getManager(), delegate));
        Iterator it = parents.iterator();
        while (it.hasNext()) {
            Module parent = (Module)it.next();
            PackageExport[] exports = parent.getPublicPackages();
            if (exports != null && exports.length == 0) {
                // Check if there is an impl dep here.
                Dependency[] deps = getDependenciesArray();
                boolean implDep = false;
                for (int i = 0; i < deps.length; i++) {
                    if (deps[i].getType() == Dependency.TYPE_MODULE &&
                            deps[i].getComparison() == Dependency.COMPARE_IMPL &&
                            deps[i].getName().equals(parent.getCodeName())) {
                        implDep = true;
                        break;
                    }
                }
                if (!implDep) {
                    // Nothing exported from here at all, no sense even adding it.
                    // Shortcut; would not harm anything to add it now, but we would
                    // never use it anyway.
                    // Cf. #27853.
                    continue;
                }
            }
            ClassLoader l = parent.getClassLoader();
            if (parent.isFixed() && loaders.contains(l)) {
                err.log("#24996: skipping duplicate classloader from " + parent);
                continue;
            }
            if (l == delegate) {
                continue;
            }
            loaders.add(l);
        }
        List classp = new ArrayList(3); // List<File|JarFile>
        classp.add(prefixURL);
        computePrefixes(classp);
        // #27853:
        getManager().refineClassLoader(this, loaders);
        
        try {
                if ((delegate != null) &&
                    ( (getCodeName().equals("org.netbeans.bootstrap/1")) ||
                      (getCodeName().equals("org.netbeans.core.startup/1")) ||
                      (getCodeName().equals("org.netbeans.modules.jnlpmodules")) || 
                      (getCodeName().equals("org.openide.modules")) ||
                      (getCodeName().equals("org.openide.util")) ||
                      (getCodeName().equals("org.openide.filesystems")) //  ??? (getCodeName().equals("org.netbeans.libs.xerces/1")) ||
                    )
                ) {
                    classloader = delegate;
                } else {
                    classloader = new OneModuleClassLoader2(classp, (ClassLoader[])loaders.toArray(new ClassLoader[loaders.size()]), delegate);
                }
        } catch (IllegalArgumentException iae) {
            // Should not happen, but just in case.
            IOException ioe = new IOException(iae.toString());
            err.annotate(ioe, iae);
            throw ioe;
        }
        //oldClassLoaders.add(classloader);
    }
    
    /**
     * Gathers prefixes from localeVariants, localeExtensions and
     * plainExtensions. 
     */
    void computePrefixes(List prefixes) {
        if (patches != null) {
            prefixes.addAll(0, patches);
        }
        if (localeVariants != null) {
            prefixes.addAll(localeVariants);
        }
        if (localeExtensions != null) {
            prefixes.addAll(localeExtensions);
        }
        if (plainExtensions != null) {
            prefixes.addAll(plainExtensions);
        }
    }
    
    /** Turn off the classloader and release all resources. */
    public void classLoaderDown() {
        if (classloader instanceof ProxyClassLoader) {
            ((ProxyClassLoader)classloader).destroy();
        }
        classloader = null;
        err.log("classLoaderDown on " + this + ": releaseCount=" + releaseCount + " released=" + released);
        released = false;

    }
    /** Should be called after turning off the classloader of one or more modules & GC'ing. */
    public void cleanup() {
        if (isEnabled()) throw new IllegalStateException("cleanup on enabled module: " + this); // NOI18N
        if (classloader != null) throw new IllegalStateException("cleanup on module with classloader: " + this); // NOI18N
        if (! released) {
            err.log("Warning: not all resources associated with module " + this + " were successfully released.");
            released = true;
        } else {
            err.log ("All resources associated with module " + this + " were successfully released.");
        }
    }
    
    /** Notify the module that it is being deleted. */
    public void destroy() {
    }
    
    /** String representation for debugging. */
    public String toString() {
        String s = "ClasspathModule:" + getCodeNameBase() + " prefix: " + prefixURL; // NOI18N
        if (!isValid()) s += "[invalid]"; // NOI18N
        return s;
    }
    
    /** PermissionCollection with an instance of AllPermission. */
    private static PermissionCollection modulePermissions;
    /** @return initialized @see #modulePermission */
    private static synchronized PermissionCollection getAllPermission() {
        if (modulePermissions == null) {
            modulePermissions = new Permissions();
            modulePermissions.add(new AllPermission());
            modulePermissions.setReadOnly();
        }
        return modulePermissions;
    }
    
    /** Used as a flag to tell if this module was really successfully released.
     * Currently does not work, so if it cannot be made to work, delete it.
     * (Someone seems to be holding a strong reference to the classloader--who?!)
     */
    private transient boolean released;
    /** Count which release() call is really being checked. */
    private transient int releaseCount = 0;

    /** Class loader to load a single module.
     * Auto-localizing, multi-parented, permission-granting, the works.
     * Second copy extends DelegatingClassLoader instead of JarClassLoader.
     */
    private class OneModuleClassLoader2 extends URLPrefixClassLoader implements Util.ModuleProvider, Util.PackageAccessibleClassLoader {
        private int rc;
        /** Create a new loader for a module.
         * @param classp the List of all module jars of code directories;
         *      includes the module itself, its locale variants,
         *      variants of extensions and Class-Path items from Manifest.
         *      The items are prefixes on the delegate class loader
         * @param parents a set of parent classloaders (from other modules)
         */
        public OneModuleClassLoader2(List prefixes, ClassLoader[] parents, ClassLoader delegate) throws IllegalArgumentException {
            super(parents, false, prefixes, delegate);
            rc = releaseCount++;
        }
        
        public Module getModule() {
            return ClasspathModule.this;
        }
        
        /** Inherited.
         * @param cs is ignored
         * @return PermissionCollection with an AllPermission instance
         */
        protected PermissionCollection getPermissions(CodeSource cs) {
            return getAllPermission();
        }
        
        /** look for JNI libraries also in modules/bin/ */
        protected String findLibrary(String libname) {
            return null;
        }

        protected boolean isSpecialResource(String pkg) {
            if (getManager().isSpecialResource(pkg)) {
                return true;
            }
            return super.isSpecialResource(pkg);
        }
        
        protected boolean shouldDelegateResource(String pkg, ClassLoader parent) {
            if (!super.shouldDelegateResource(pkg, parent)) {
                return false;
            }
            Module other;
            if (parent instanceof Util.ModuleProvider) {
                other = ((Util.ModuleProvider)parent).getModule();
            } else {
                other = null;
            }
            return getManager().shouldDelegateResource(ClasspathModule.this, other, pkg);
        }
        
        public String toString() {
            return super.toString() + "[" + getCodeNameBase() + "]"; // NOI18N
        }

        protected void finalize() throws Throwable {
            super.finalize();
            err.log("Finalize for " + this + ": rc=" + rc + " releaseCount=" + releaseCount + " released=" + released); // NOI18N
            if (rc == releaseCount) {
                // Hurrah! release() worked.
                released = true;
            } else {
                err.log("Now resources for " + getCodeNameBase() + " have been released."); // NOI18N
            }
        }
    }

}
