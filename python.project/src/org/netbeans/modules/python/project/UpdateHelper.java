/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.python.project;

import java.io.IOException;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.w3c.dom.Element;

/**
 * Helper class to support migration from
 * older version of ant based python project
 * to the newer version of ant base python project.
 * Currently there is no older version of ant based
 * python project, so the class has no upgrade logic.
 * Based on the UpdateHelper (java.common.api)
 */
public final class UpdateHelper {
    
    
    private final UpdateImplementation updateProject;
    private final AntProjectHelper helper;
    
    public UpdateHelper (final UpdateImplementation update, final AntProjectHelper helper) {
        assert update != null;
        assert helper != null;
        this.updateProject = update;
        this.helper = helper;
        
    }
    
    /**
     * In the case that the project is of current version or the properties
     * are not {@link AntProjectHelper#PROJECT_PROPERTIES_PATH} it calls
     * {@link AntProjectHelper#getProperties(String)} otherwise it asks for updated project properties.
     * @param path a relative URI in the project directory.
     * @return a set of properties.
     */
    public EditableProperties getProperties(final String path) {
        return ProjectManager.mutex().readAccess(new Mutex.Action<EditableProperties>() {
            @Override
            public EditableProperties run() {
                if (!isCurrent() && AntProjectHelper.PROJECT_PROPERTIES_PATH.equals(path)) {
                    // only project properties were changed
                    return updateProject.getUpdatedProjectProperties();
                }
                return helper.getProperties(path);
            }
        });
    }
    
    
    /**
     * In the case that the project is of current version or the properties
     * are not {@link AntProjectHelper#PROJECT_PROPERTIES_PATH} it calls
     * {@link AntProjectHelper#putProperties(String, EditableProperties)} otherwise it asks to update project.
     * If the project can be updated, it does the update and calls
     * {@link AntProjectHelper#putProperties(String, EditableProperties)}.
     * @param path a relative URI in the project directory.
     * @param props a set of properties.
     */
    public void putProperties(final String path, final EditableProperties props) {
        ProjectManager.mutex().writeAccess(
            new Runnable() {
                @Override
                public void run() {
                    if (isCurrent() || !AntProjectHelper.PROJECT_PROPERTIES_PATH.equals(path)) {
                        // only project props should cause update
                        helper.putProperties(path, props);
                    } else if (updateProject.canUpdate()) {
                        try {
                            updateProject.saveUpdate(props);
                            helper.putProperties(path, props);
                        } catch (IOException ioe) {
                            Exceptions.printStackTrace(ioe);
                        }
                    }
                }
            });
    }

    /**
     * In the case that the project is of current version or shared is <code>false</code> it delegates to
     * {@link AntProjectHelper#getPrimaryConfigurationData(boolean)}.
     * Otherwise it creates an in memory update of shared configuration data and returns it.
     * @param shared if <code>true</code>, refers to <e>project.xml</e>, else refers to
     *               <e>private.xml</e>.
     * @return the configuration data that is available.
     */
    public Element getPrimaryConfigurationData(final boolean shared) {
        return ProjectManager.mutex().readAccess(new Mutex.Action<Element>() {
            @Override
            public Element run() {
                if (!shared || isCurrent()) { // only shared props should cause update
                    return helper.getPrimaryConfigurationData(shared);
                }
                return updateProject.getUpdatedSharedConfigurationData();
            }
        });
    }

    /**
     * In the case that the project is of current version or shared is <code>false</code> it calls
     * {@link AntProjectHelper#putPrimaryConfigurationData(Element, boolean)}.
     * Otherwise the project can be updated, it does the update and calls
     * {@link AntProjectHelper#putPrimaryConfigurationData(Element, boolean)}.
     * @param element the configuration data
     * @param shared if true, refers to <code>project.xml</code>, else refers to
     * <code>private.xml</code>
     */
    public void putPrimaryConfigurationData(final Element element, final boolean shared) {
        ProjectManager.mutex().writeAccess(new Runnable() {
            @Override
            public void run () {
                if (!shared || isCurrent()) {
                    helper.putPrimaryConfigurationData(element, shared);
                } else if (updateProject.canUpdate()) {
                    try {
                        updateProject.saveUpdate(null);
                        helper.putPrimaryConfigurationData(element, shared);
                    } catch (IOException ioe) {
                        Exceptions.printStackTrace(ioe);
                    }
                }
            }
        });
    }

    /**
     * Request saving of update. If the project is not of current version and the project can be updated, then
     * the update is done.
     * @return <code>true</code> if the metadata are of current version or updated.
     * @throws IOException if error occurs during saving.
     */
    public boolean requestUpdate() throws IOException {
        try {
            return ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws IOException {
                    if (isCurrent()) {
                        return true;
                    }
                    if (!updateProject.canUpdate()) {
                        return false;
                    }
                    updateProject.saveUpdate(null);
                    return true;
                }
            });

        } catch (MutexException ex) {
            Exception inner = ex.getException();
            if (inner instanceof IOException) {
                throw (IOException) inner;
            }
            throw (RuntimeException) inner;
        }
    }

    /**
     * Return <code>true</code> if the project is of current version.
     * @return <code>true</code> if the project is of current version.
     */
    public boolean isCurrent() {
        return updateProject.isCurrent();
    }

    /**
     * Get the {@link AntProjectHelper} that is proxied.
     * @return the {@link AntProjectHelper} that is proxied.
     */
    public AntProjectHelper getAntProjectHelper() {
        return helper;
    }

}
