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

package org.netbeans.modules.vcs.advanced;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import org.openide.ErrorManager;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.xml.XMLUtil;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.commands.ConditionedCommands;
import org.netbeans.modules.vcs.advanced.commands.UserCommandIO;
import org.netbeans.modules.vcs.advanced.commands.UserCommandIOCompat;
import org.netbeans.modules.vcs.advanced.variables.Condition;
import org.netbeans.modules.vcs.advanced.variables.ConditionIO;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;
import org.netbeans.modules.vcs.advanced.variables.VariableIO;
import org.netbeans.modules.vcs.advanced.variables.VariableIOCompat;

/**
 * The factory of VCS profiles.
 *
 * @author  Martin Entlicher
 */
public final class ProfilesFactory extends Object {
    
    /**
     * The name of property change event, that is fired when a new profile is
     * added. The name of the added profile can be obtained as the new value
     * of the property change event.
     */
    public static final String PROP_PROFILE_ADDED = "profileAdded"; // NOI18N
    /**
     * The name of property change event, that is fired when a profile is
     * removed. The name of the removed profile can be obtained as the old value
     * of the property change event.
     */
    public static final String PROP_PROFILE_REMOVED = "profileRemoved"; // NOI18N
    
    private static final String PROFILE_ROOT = "vcs/config"; // NOI18N
    
    private static ProfilesFactory factory;
    
    private FileObject profileRoot;
    private FileChangeListener profileRootFolderListener;
    
    private List profileNames;
    private List profileLabels;
    // Whether labels are processed through VcsUtilities.getBundleString().
    // This is not necessary to do until we're asked for them.
    private boolean areLabelsResolved = false;
    private Map profileLabelsByName;
    private Map compatibleOSsByName;
    private Map uncompatibleOSsByName;
    private Map resourceBundlesByName;
    private Map profilesByName;
    
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    /** Creates a new instance of ProfilesFactory */
    private ProfilesFactory() {
        FileSystem dfs = Repository.getDefault().getDefaultFileSystem ();
        profileRoot = dfs.findResource(PROFILE_ROOT);
        profileRootFolderListener = new ProfileRootFolderListener();
        profileRoot.addFileChangeListener(FileUtil.weakFileChangeListener(profileRootFolderListener,
                                                                  profileRoot));
        initProfilesInfo();
    }
    
    /**
     * Get the default instance of the profiles factory.
     */
    public static synchronized ProfilesFactory getDefault() {
        if (factory == null) {
            factory = new ProfilesFactory();
        }
        return factory;
    }
    
    private void initProfilesInfo() {
        //System.out.println("ProfilesCache.initProfileLabels() root = "+profileRoot);
        profileNames = VariableIO.readConfigurations(profileRoot);
        profileLabels = new ArrayList(profileNames.size());
        profileLabelsByName = new HashMap();
        compatibleOSsByName = new HashMap();
        uncompatibleOSsByName = new HashMap();
        resourceBundlesByName = new HashMap();
        for (int i = 0; i < profileNames.size(); i++) {
            String name = (String) profileNames.get(i);
            //StringBuffer compatibleOSs = new StringBuffer();
            //StringBuffer uncompatibleOSs = new StringBuffer();
            String[] labelAndOSs = VariableIO.getConfigurationLabelAndOS(profileRoot, name);
            if (labelAndOSs == null) {
                profileNames.remove(i--);
                continue;
            }
            String label = labelAndOSs[0];
            profileLabels.add(label);
            profileLabelsByName.put(name, label);
            compatibleOSsByName.put(name, (labelAndOSs[1] != null) ? parseOSs(labelAndOSs[1]) : Collections.EMPTY_SET);
            uncompatibleOSsByName.put(name, (labelAndOSs[2] != null) ? parseOSs(labelAndOSs[2]) : Collections.EMPTY_SET);
            if (labelAndOSs.length > 3) {
                String[] resourceBundles = new String[labelAndOSs.length - 3];
                System.arraycopy(labelAndOSs, 3, resourceBundles, 0, resourceBundles.length);
                resourceBundlesByName.put(name, resourceBundles);
            }
            //System.out.println("name = "+profileNames.get(i)+", label = "+getProfileLabel((String) profileNames.get(i)));
        }
        profilesByName = new HashMap();
    }
    
    private Set parseOSs(String oss) {
        Set set;
        if (oss.length() > 0) {
            set = new HashSet();
            String[] strs = VcsUtilities.getQuotedStrings(oss);
            for (int i = 0; i < strs.length; i++) {
                set.add(strs[i]);
            }
        } else {
            set = Collections.EMPTY_SET;
        }
        return set;
    }
    
    public synchronized String[] getProfilesNames() {
        String[] profiles = new String[profileNames.size()];
        profileNames.toArray(profiles);
        return profiles;
    }
    
    public synchronized String[] getProfilesDisplayNames() {
        if (!areLabelsResolved) {
            for (int i = 0; i < profileNames.size(); i++) {
                String name = (String) profileNames.get(i);
                String label = (String) profileLabels.get(i);
                label = VcsUtilities.getBundleString((String[]) resourceBundlesByName.get(name), label);
                profileLabels.set(i, label);
                profileLabelsByName.put(name, label);
            }
            areLabelsResolved = true;
        }
        String[] profiles = new String[profileLabels.size()];
        profileLabels.toArray(profiles);
        return profiles;
    }
    
    public synchronized String getProfileDisplayName(String profileName) {
        if (!areLabelsResolved) {
            String label = (String) profileLabelsByName.get(profileName);
            label = VcsUtilities.getBundleString((String[]) resourceBundlesByName.get(profileName), label);
            profileLabelsByName.put(profileName, label);
        }
        return (String) profileLabelsByName.get(profileName);
    }
    
    /**
     * Get the profile of the given name.
     * @return The profile or <code>null</code> when the profile can not be found.
     */
    public synchronized Profile getProfile(String profileName) {
        Reference profileRef = (Reference) profilesByName.get(profileName);
        Profile profile = null;
        if (profileRef != null) {
            profile = (Profile) profileRef.get();
        }
        if (profile == null) {
            FileObject profileFile = profileRoot.getFileObject(profileName);
            if (profileFile == null) return null;
            profile = new ProfilesFactory.ProfileImpl(profileFile);
            profileRef = new WeakReference(profile);
            profilesByName.put(profileName, profileRef);
        }
        return profile;
    }
    
    public synchronized boolean isOSCompatibleProfile(String profileName) {
        String osName = System.getProperty ("os.name"); // NOI18N
        Set compatible = (Set) compatibleOSsByName.get(profileName);
        Set uncompatible = (Set) uncompatibleOSsByName.get(profileName);
        if (compatible == null || uncompatible == null) return true;
        if (compatible.contains(osName)) return true;
        if (uncompatible.contains(osName)) return false;
        if (org.openide.util.Utilities.isUnix() && compatible.contains("Unix") ||
            org.openide.util.Utilities.isWindows() && compatible.contains("Windows")) return true;
        if (org.openide.util.Utilities.isUnix() && uncompatible.contains("Unix") ||
            org.openide.util.Utilities.isWindows() && uncompatible.contains("Windows")) return false;
        return (compatible.size() == 0);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }
    
    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /**
     * Remove an existing profile.
     */
    public void removeProfile(String profileName) throws IOException {
        FileObject profileFile = profileRoot.getFileObject(profileName);
        if (profileFile != null) {
            profileFile.delete();
        } else {
            throw new FileNotFoundException(profileName);
        }
        firePropertyChange(PROP_PROFILE_REMOVED, profileName, null);
    }
    
    /**
     * Add a new profile.
     */
    public synchronized Profile addProfile(String name, String displayName,
                                           Set compatibleOSs, Set uncompatibleOSs,
                                           ConditionedVariables variables,
                                           ConditionedCommands commands) throws IOException {
        FileObject profileFile = profileRoot.getFileObject(name, VariableIO.CONFIG_FILE_EXT);
        if (profileFile == null) {
            profileFile = profileRoot.createData(name, VariableIO.CONFIG_FILE_EXT);
        }
        name = profileFile.getNameExt();
        profileNames.add(name);
        profileLabels.add(displayName);
        profileLabelsByName.put(name, displayName);
        compatibleOSsByName.put(name, compatibleOSs);
        uncompatibleOSsByName.put(name, uncompatibleOSs);
        Profile profile = new ProfilesFactory.ProfileImpl(profileFile, variables, commands);
        profilesByName.put(name, new WeakReference(profile));
        firePropertyChange(PROP_PROFILE_ADDED, null, name);
        return profile;
    }
    
    
    public class ProfileImpl extends Profile implements FileChangeListener {
        
        private String profileName;
        private Condition[] conditions;
        private ConditionedVariables variables;
        private ConditionedCommands commands;
        private ConditionedCommands globalCommands;
        
        public ProfileImpl(FileObject profileFile, ConditionedVariables variables,
                           ConditionedCommands commands) {
            this(profileFile);
            this.variables = variables;
            this.commands = commands;
            try {
                saveConfig();
            } catch (org.w3c.dom.DOMException exc) {
                ErrorManager.getDefault().notify(exc);
            } catch (java.io.IOException ioexc) {
                ErrorManager.getDefault().notify(ioexc);
            }
        }
        
        public ProfileImpl(FileObject profileFile) {
            this.profileName = profileFile.getNameExt();
            profileFile.addFileChangeListener(FileUtil.weakFileChangeListener(this, profileFile));
        }
        
        public String getName() {
            return profileName;
        }
        
        public String getDisplayName() {
            return getProfileDisplayName(profileName);
        }
        
        public synchronized void preLoadContent(boolean conditions, boolean variables,
                                                boolean commands, boolean globalCommands) {
            if (this.conditions != null) conditions = false;
            if (this.variables != null) variables = false;
            if (this.commands != null) commands = false;
            if (this.globalCommands != null) globalCommands = false;
            if (conditions || variables || commands || globalCommands) {
                loadConfig(true, conditions, variables, commands, globalCommands);
            }
        }
        
        public Set getCompatibleOSs() {
            synchronized (ProfilesFactory.this) {
                return (Set) compatibleOSsByName.get(profileName);
            }
        }
        
        public Set getUncompatibleOSs() {
            synchronized (ProfilesFactory.this) {
                return (Set) uncompatibleOSsByName.get(profileName);
            }
        }
        
        public synchronized Condition[] getConditions() {
            if (conditions == null) {
                loadConfig(false, true, false, false, false);
            }
            return conditions;
        }
        
        public synchronized ConditionedVariables getVariables() {
            if (variables == null) {
                loadConfig(false, false, true, false, false);
            }
            return variables;
        }
        
        public synchronized ConditionedCommands getCommands() {
            if (commands == null) {
                loadConfig(false, false, false, true, false);
            }
            return commands;
        }
        
        public synchronized ConditionedCommands getGlobalCommands() {
            if (globalCommands == null) {
                loadConfig(false, false, false, false, true);
            }
            return globalCommands;
        }
        
        public boolean setConditions(Condition[] conditions) {
            synchronized (this) {
                preLoadContent(true, true, true, true);
                this.conditions = conditions;
                try {
                    saveConfig();
                } catch (org.w3c.dom.DOMException exc) {
                    ErrorManager.getDefault().notify(exc);
                    return false;
                } catch (java.io.IOException ioexc) {
                    ErrorManager.getDefault().notify(ioexc);
                    return false;
                }
            }
            this.firePropertyChange(PROP_CONDITIONS, null, conditions);
            return true;
        }
        
        public boolean setVariables(ConditionedVariables variables) {
            synchronized (this) {
                preLoadContent(true, true, true, true);
                this.variables = variables;
                try {
                    saveConfig();
                } catch (org.w3c.dom.DOMException exc) {
                    ErrorManager.getDefault().notify(exc);
                    return false;
                } catch (java.io.IOException ioexc) {
                    ErrorManager.getDefault().notify(ioexc);
                    return false;
                }
            }
            this.firePropertyChange(PROP_VARIABLES, null, variables);
            return true;
        }
        
        public boolean setCommands(ConditionedCommands commands) {
            synchronized (this) {
                preLoadContent(true, true, true, true);
                this.commands = commands;
                try {
                    saveConfig();
                } catch (org.w3c.dom.DOMException exc) {
                    ErrorManager.getDefault().notify(exc);
                    return false;
                } catch (java.io.IOException ioexc) {
                    ErrorManager.getDefault().notify(ioexc);
                    return false;
                }
            }
            this.firePropertyChange(PROP_COMMANDS, null, commands);
            return true;
        }
        
        public synchronized boolean setGlobalCommands(ConditionedCommands globalCommands) {
            synchronized (this) {
                preLoadContent(true, true, true, true);
                this.globalCommands = globalCommands;
                try {
                    saveConfig();
                } catch (org.w3c.dom.DOMException exc) {
                    ErrorManager.getDefault().notify(exc);
                    return false;
                } catch (java.io.IOException ioexc) {
                    ErrorManager.getDefault().notify(ioexc);
                    return false;
                }
            }
            this.firePropertyChange(PROP_GLOBAL_COMMANDS, null, globalCommands);
            return true;
        }
        
        void unimplementableFromOutside() {
        }
        
        private void loadConfig(boolean os, boolean conditions, boolean variables,
                                boolean commands, boolean globalCommands) {
            if (profileName == null) return ;
            if (profileName.endsWith(VariableIOCompat.CONFIG_FILE_EXT)) {
                Properties props = VariableIOCompat.readPredefinedProperties(profileRoot, profileName);
                if (props == null) return ;
                this.variables = new ConditionedVariables(VariableIOCompat.readVariables(props),
                                                     Collections.EMPTY_MAP, Collections.EMPTY_MAP);
                
                //  TODO commands = (CommandsTree) UserCommandIOCompat.readUserCommands(props, variableMap);
                
                //profileCommandsByLabel.put(profileDisplayName, commands);
                this.conditions = new Condition[0];
            } else {
                //System.out.println("loadConfig("+profileName+"; "+os+", "+conditions+", "+variables+", "+commands+", "+globalCommands+")");
                //long start = System.currentTimeMillis();
                FileObject profileFO = profileRoot.getFileObject(profileName);
                if (profileFO == null) {
                    org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
                        public void run() {
                            org.openide.ErrorManager.getDefault().notify(new FileNotFoundException("Problems while reading predefined properties.") {
                                public String getLocalizedMessage() {
                                    return NbBundle.getMessage(VariableIO.class, "EXC_Problems_while_reading_predefined_properties", profileName);
                                }
                            });
                        }
                    });
                    //E.err(g("EXC_Problems_while_reading_predefined_properties",name)); // NOI18N
                    return ;
                }
                ProfileContentHandler handler = new ProfileContentHandler(os, conditions, variables, commands, globalCommands, null);
                //System.out.println("VariableIO.getConfigurationLabel("+config+")");
                try {
                    XMLReader reader = XMLUtil.createXMLReader();
                    reader.setContentHandler(handler);
                    reader.setEntityResolver(handler);
                    InputSource source = new InputSource(profileFO.getInputStream());
                    reader.parse(source);
                } catch (SAXException exc) {
                    if (!ProfileContentHandler.END_OF_PARSING.equals(exc.getMessage())) {
                        org.openide.ErrorManager.getDefault().notify(
                            org.openide.ErrorManager.getDefault().annotate(
                                exc, NbBundle.getMessage(VariableIO.class, "EXC_Problems_while_reading_predefined_properties", profileName)));
                        return ;
                    }
                } catch (java.io.FileNotFoundException fnfExc) {
                    org.openide.ErrorManager.getDefault().notify(
                        org.openide.ErrorManager.getDefault().annotate(
                            fnfExc, NbBundle.getMessage(VariableIO.class, "EXC_Problems_while_reading_predefined_properties", profileName)));
                    return ;
                } catch (java.io.IOException ioExc) {
                    org.openide.ErrorManager.getDefault().notify(
                        org.openide.ErrorManager.getDefault().annotate(
                            ioExc, NbBundle.getMessage(VariableIO.class, "EXC_Problems_while_reading_predefined_properties", profileName)));
                    return ;
                }
                if (conditions) {
                    this.conditions = handler.getConditions();
                }
                if (variables) {
                    this.variables = handler.getVariables();
                }
                if (commands) {
                    this.commands = handler.getCommands();
                }
                if (globalCommands) {
                    this.globalCommands = handler.getGlobalCommands();
                }
                //long end = System.currentTimeMillis();
                //System.out.println("  loadConfig(,,,,) took "+(end - start)+" milliseconds.");
            }
        }
        
        private void saveConfig() throws org.w3c.dom.DOMException, java.io.IOException {
            org.w3c.dom.Document doc = org.openide.xml.XMLUtil.createDocument(VariableIO.CONFIG_ROOT_ELEM, null, null, null);
            String profileDisplayName = (String) profileLabelsByName.get(profileName);
            if (conditions != null) {
                ConditionIO.writeConditions(doc, conditions);
            }
            VariableIO.writeVariables(doc, profileDisplayName, variables,
                                      (Set) compatibleOSsByName.get(profileName),
                                      (Set) uncompatibleOSsByName.get(profileName));
            UserCommandIO.writeCommands(doc, commands);
            if (globalCommands != null) {
                UserCommandIO.writeGlobalCommands(doc, globalCommands);
            }
            FileObject file = profileRoot.getFileObject(profileName);//, VariableIO.CONFIG_FILE_EXT);
            if (file != null) {
                org.openide.filesystems.FileLock lock = null;
                java.io.OutputStream out = null;
                try {
                    lock = file.lock();
                    out = file.getOutputStream(lock);
                    org.openide.xml.XMLUtil.write(doc, out, null);
                    //XMLDataObject.write(doc, new BufferedWriter(new OutputStreamWriter(file.getOutputStream(lock))));
                } finally {
                    if (out != null) out.close();
                    if (lock != null) lock.releaseLock();
                }
            }
        }

        public void fileAttributeChanged(FileAttributeEvent fileAttributeEvent) {
        }
        
        public void fileChanged(FileEvent fileEvent) {
            synchronized (this) {
                variables = null;
                commands = null;
                globalCommands = null;
            }
            this.firePropertyChange(PROP_VARIABLES, null, null);
            this.firePropertyChange(PROP_COMMANDS, null, null);
            this.firePropertyChange(PROP_GLOBAL_COMMANDS, null, null);
        }
        
        public void fileDataCreated(FileEvent fileEvent) {
        }
        
        public void fileDeleted(FileEvent fileEvent) {
        }
        
        public void fileFolderCreated(FileEvent fileEvent) {
        }
        
        public void fileRenamed(FileRenameEvent fileRenameEvent) {
        }
        
    }
    
    public static class ProfileImplBeanInfo extends SimpleBeanInfo{
        
        /**
         * Gets the bean's <code>PropertyDescriptor</code>s.
         *
         * @return An array of PropertyDescriptors describing the editable
         * properties supported by this bean.  May return null if the
         * information should be obtained by automatic analysis.
         * <p>
         * If a property is indexed, then its entry in the result array will
         * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
         * A client of getPropertyDescriptors can use "instanceof" to check
         * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
         */
        public PropertyDescriptor[] getPropertyDescriptors() {
            PropertyDescriptor[] properties = null;
            try {
                properties = new PropertyDescriptor[] {
                    new PropertyDescriptor("conditions", ProfileImpl.class),
                    new PropertyDescriptor("variables", ProfileImpl.class),
                    new PropertyDescriptor("commands", ProfileImpl.class),
                    new PropertyDescriptor("globalCommands", ProfileImpl.class),
                };
                
                properties[0].setDisplayName(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("PROP_Conditions"));
                properties[0].setShortDescription(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("HINT_PPROP_Conditions"));
                properties[0].setPropertyEditorClass(UserConditionsEditor.class);
                properties[0].setValue("canEditAsText", Boolean.FALSE);
                properties[1].setDisplayName(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("PROP_Variables"));
                properties[1].setShortDescription(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("HINT_PPROP_Variables"));
                properties[1].setPropertyEditorClass(UserConditionedVariablesEditor.class);
                properties[1].setValue("canEditAsText", Boolean.FALSE);
                properties[2].setDisplayName(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("PROP_Commands"));
                properties[2].setShortDescription(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("HINT_PROP_Commands"));
                properties[2].setPropertyEditorClass(UserConditionedCommandsEditor.class);
                properties[2].setValue("canEditAsText", Boolean.FALSE);
                properties[3].setDisplayName(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("PROP_GlobalCommands"));
                properties[3].setShortDescription(NbBundle.getBundle(ProfileImplBeanInfo.class).getString("HINT_PROP_GlobalCommands"));
                properties[3].setPropertyEditorClass(UserConditionedCommandsEditor.class);
                properties[3].setValue("canEditAsText", Boolean.FALSE);
                
            } catch (java.beans.IntrospectionException intrexc) {
                ErrorManager manager = (ErrorManager)Lookup.getDefault().lookup(ErrorManager.class);
                manager.notify();
            }
            return properties;
        }
        /*
         * This method returns an image object that can be used to
         * represent the bean in toolboxes, toolbars, etc.   Icon images
         * will typically be GIFs, but may in future include other formats.
         *
         * @param  iconKind  The kind of icon requested.  This should be
         *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32,
         *    ICON_MONO_16x16, or ICON_MONO_32x32.
         * @return  An image object representing the requested icon.  May
         *    return null if no suitable icon is available.
         */
        public java.awt.Image getIcon(int iconKind) {
            // return Utilities.loadImage("org/netbeans/modules/vcs/advanced/vcsGeneric.gif"); // NOI18N
            return null; //for now we don't have appropriate icon
        }
        
    }
        
    private class ProfileRootFolderListener extends Object implements FileChangeListener {

        /**
         * The config's root folder's attribute changed.
         */
        public void fileAttributeChanged(FileAttributeEvent fileAttributeEvent) {
        }

        /**
         * The config's root folder changed.
         */
        public void fileChanged(FileEvent fileEvent) {
        }

        /**
         * Some data were created in the config's root folder.
         */
        public void fileDataCreated(FileEvent fileEvent) {
            FileObject newData = fileEvent.getFile();
            if (newData.getSize() == 0L || !VariableIO.isConfigFile(newData)) return ; // Ignore an empty file
            List currentLocales = VariableIO.getLocalizedConfigurations(new FileObject[] { newData });
            if (!currentLocales.contains(newData)) return ; // Ignore other locales
            String name = newData.getNameExt();
            String[] labelAndOSs = VariableIO.getConfigurationLabelAndOS(profileRoot, name);
            if (labelAndOSs == null) {
                return ;
            }
            String label = labelAndOSs[0];
            synchronized (ProfilesFactory.this) {
                profileNames.add(name);
                profileLabels.add(label);
                profileLabelsByName.put(name, label);
                compatibleOSsByName.put(name, (labelAndOSs[1] != null) ? parseOSs(labelAndOSs[1]) : Collections.EMPTY_SET);
                uncompatibleOSsByName.put(name, (labelAndOSs[2] != null) ? parseOSs(labelAndOSs[2]) : Collections.EMPTY_SET);
            }
            ProfilesFactory.this.firePropertyChange(PROP_PROFILE_ADDED, null, name);
        }

        /**
         * Some data were deleted in the config's root folder.
         */
        public void fileDeleted(FileEvent fileEvent) {
            FileObject oldData = fileEvent.getFile();
            String name = oldData.getNameExt();
            synchronized (ProfilesFactory.this) {
                profileNames.remove(name);
                String label = (String) profileLabelsByName.remove(name);
                profileLabels.remove(label);
                compatibleOSsByName.remove(name);
                uncompatibleOSsByName.remove(name);
            }
            ProfilesFactory.this.firePropertyChange(PROP_PROFILE_REMOVED, name, null);
        }

        /**
         * Some folder was created in the config's root folder.
         */
        public void fileFolderCreated(FileEvent fileEvent) {
        }

        /**
         * The config's root folder was renamed.
         */
        public void fileRenamed(FileRenameEvent fileRenameEvent) {
        }
        
    }
    
}
