/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.ejbfreeform;

import org.netbeans.api.project.Project;
import org.netbeans.modules.ant.freeform.spi.HelpIDFragmentProvider;
import org.netbeans.modules.ant.freeform.spi.ProjectAccessor;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.spi.ejbjar.support.EjbEnterpriseReferenceContainerSupport;
import org.netbeans.spi.project.AuxiliaryConfiguration;
import org.netbeans.spi.project.LookupProvider;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.AntProjectListener;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author mkleint
 */
public class LookupProviderImpl implements LookupProvider {
    
    private static final String HELP_ID_FRAGMENT = "ejb"; // NOI18N
   
    /** Creates a new instance of LookupProviderImpl */
    public LookupProviderImpl() {
    }
    
    public Lookup createAdditionalLookup(Lookup baseContext) {
        Project prj = (Project)baseContext.lookup(Project.class);
        ProjectAccessor acc = (ProjectAccessor)baseContext.lookup(ProjectAccessor.class);
        AuxiliaryConfiguration aux = (AuxiliaryConfiguration)baseContext.lookup(AuxiliaryConfiguration.class);
        assert aux != null;
        assert prj != null;
        assert acc != null;
        return new ProjectLookup(prj, acc.getHelper(), acc.getEvaluator(), aux);
    }
    
    private static Lookup initLookup(Project project, AntProjectHelper projectHelper, PropertyEvaluator projectEvaluator, AuxiliaryConfiguration aux) {
        
        EJBFreeformProvider ejbFFProvider = new EJBFreeformProvider(project, projectHelper, projectEvaluator);
        EJBFreeformModule ejbFFModule = new EJBFreeformModule(project, projectHelper, projectEvaluator);
        ejbFFProvider.setJ2eeModule(ejbFFModule);
                
        return Lookups.fixed(new Object[] {
            ejbFFProvider,
            ejbFFModule,
            new EJBModules(project, projectHelper, projectEvaluator), // EJBModuleProvider, ClassPathProvider
            new PrivilegedTemplatesImpl(), // List of templates in New action popup
            EjbEnterpriseReferenceContainerSupport.createEnterpriseReferenceContainer(project, projectHelper),
            new EjbFreeFormActionProvider(project, projectHelper, aux),
            new ProjectOpenedHookImpl(project),
            new LookupMergerImpl(),
            new HelpIDFragmentProviderImpl(),
        });
    }
    
    private static final class HelpIDFragmentProviderImpl implements HelpIDFragmentProvider {
        public String getHelpIDFragment() {
            return HELP_ID_FRAGMENT;
        }
    }
    
    private static final class ProjectLookup extends ProxyLookup implements AntProjectListener {

        private AntProjectHelper helper;
        private PropertyEvaluator evaluator;
        private Project project;
        private AuxiliaryConfiguration aux;
        private boolean isMyProject;
        
        public ProjectLookup(Project project, AntProjectHelper helper, PropertyEvaluator evaluator, AuxiliaryConfiguration aux) {
            super(new Lookup[0]);
            this.project = project;
            this.helper = helper;
            this.evaluator = evaluator;
            this.aux = aux;
            this.isMyProject = EJBProjectNature.isMyProject(aux);
            updateLookup();
            helper.addAntProjectListener(this);
        }
        
        private void updateLookup() {
            Lookup l = Lookup.EMPTY;
            if (isMyProject) {
                l = initLookup(project, helper, evaluator, aux);
            }
            setLookups(new Lookup[]{l});
        }
        
        public void configurationXmlChanged(AntProjectEvent ev) {
            if (EJBProjectNature.isMyProject(aux) != isMyProject) {
                isMyProject = !isMyProject;
                updateLookup();
            }
        }
        
        public void propertiesChanged(AntProjectEvent ev) {
            // ignore
        }
        
    }
    
    private static final class ProjectOpenedHookImpl extends ProjectOpenedHook {
        
        private Project project;
        
        public ProjectOpenedHookImpl(Project project) {
            this.project = project;
        }
        
        protected void projectOpened() {
            // initialize the server configuration
            J2eeModuleProvider j2eeModule = (J2eeModuleProvider)project.getLookup().lookup(J2eeModuleProvider.class);
            j2eeModule.getConfigSupport().ensureConfigurationReady();
        }

        protected void projectClosed() {
        }
    }

    private static final class PrivilegedTemplatesImpl implements PrivilegedTemplates, RecommendedTemplates {
        
        private static final String[] PRIVILEGED_NAMES = new String[] {
            "Templates/J2EE/Session", // NOI18N
            "Templates/J2EE/Entity",  // NOI18N
            "Templates/J2EE/ServiceLocator.java", // NOI18N
            "Templates/Classes/Class.java" // NOI18N
        };
        
        private static final String[] RECOMENDED_TYPES = new String[] {
            "java-classes",         // NOI18N
            "ejb-types",            // NOI18N
            "ejb-types_2_1",        // NOI18N
            "j2ee-types",           // NOI18N
            "java-beans",           // NOI18N
            "oasis-XML-catalogs",   // NOI18N
            "XML",                  // NOI18N
            "wsdl",                 // NOI18N
            "ant-script",           // NOI18N
            "ant-task",             // NOI18N
            "junit",                // NOI18N
            "simple-files"          // NOI18N
        };
        
        public String[] getPrivilegedTemplates() {
            return PRIVILEGED_NAMES;
        }

        public String[] getRecommendedTypes() {
            return RECOMENDED_TYPES;
        }
     
    
    
    }
    

}
