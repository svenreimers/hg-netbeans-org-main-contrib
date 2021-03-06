/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.wizard.components.sequences;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.installer.wizard.components.panels.sunstudio.PostInstallSummaryPanel;
import org.netbeans.installer.wizard.components.panels.sunstudio.PreInstallSummaryPanel;
import org.netbeans.installer.product.components.Product;
import org.netbeans.installer.product.Registry;
import org.netbeans.installer.utils.ErrorManager;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.ResourceUtils;
import org.netbeans.installer.utils.SystemUtils;
import org.netbeans.installer.utils.env.CheckStatus;
import org.netbeans.installer.utils.helper.ExecutionMode;
import org.netbeans.installer.utils.helper.Platform;
import org.netbeans.installer.utils.silent.SilentLogManager;
import org.netbeans.installer.wizard.Utils;
import org.netbeans.installer.wizard.components.WizardAction;
import org.netbeans.installer.wizard.components.WizardComponent;
import org.netbeans.installer.wizard.components.WizardSequence;
import org.netbeans.installer.wizard.components.actions.CreateBundleAction;
import org.netbeans.installer.wizard.components.actions.CreateNativeLauncherAction;
import org.netbeans.installer.wizard.components.actions.DownloadConfigurationLogicAction;
import org.netbeans.installer.wizard.components.actions.DownloadInstallationDataAction;
import org.netbeans.installer.wizard.components.actions.sunstudio.InstallAction;
import org.netbeans.installer.wizard.components.actions.sunstudio.UninstallAction;
import org.netbeans.installer.wizard.components.actions.sunstudio.RegistrationAction;
import org.netbeans.installer.wizard.components.actions.sunstudio.ServiceTagCreateAction;
import org.netbeans.installer.wizard.components.panels.PostCreateBundleSummaryPanel;
import org.netbeans.installer.wizard.components.panels.PreCreateBundleSummaryPanel;
import org.netbeans.installer.wizard.components.panels.sunstudio.WelcomePanel;


/**
 *
 * @author Kirill Sorokin
 */
public class MainSequence extends WizardSequence {
    /////////////////////////////////////////////////////////////////////////////////
    // Instance
    private DownloadConfigurationLogicAction downloadConfigurationLogicAction;    
    private PreInstallSummaryPanel nbPreInstallSummaryPanel;
    private UninstallAction uninstallAction;
    private DownloadInstallationDataAction downloadInstallationDataAction;
    private InstallAction installAction;
    private PostInstallSummaryPanel nbPostInstallSummaryPanel;
    private PreCreateBundleSummaryPanel preCreateBundleSummaryPanel;
    private CreateBundleAction createBundleAction;
    private CreateNativeLauncherAction createNativeLauncherAction;
    
    private PostCreateBundleSummaryPanel postCreateBundleSummaryPanel;
    private ServiceTagCreateAction serviceTagAction;
    private RegistrationAction nbRegistrationAction;
    private Map<Product, ProductWizardSequence> productSequences;
    
    public MainSequence() {
        downloadConfigurationLogicAction = new DownloadConfigurationLogicAction();        
        nbPreInstallSummaryPanel = new PreInstallSummaryPanel();
        uninstallAction = new UninstallAction();
        downloadInstallationDataAction = new DownloadInstallationDataAction();
        installAction = new InstallAction();
        nbPostInstallSummaryPanel = new PostInstallSummaryPanel();
        preCreateBundleSummaryPanel = new PreCreateBundleSummaryPanel();
        createBundleAction = new CreateBundleAction();
        createNativeLauncherAction = new CreateNativeLauncherAction();
        
        
        postCreateBundleSummaryPanel = new PostCreateBundleSummaryPanel();
        serviceTagAction = new ServiceTagCreateAction();
        nbRegistrationAction = new RegistrationAction ();
        productSequences = new HashMap<Product, ProductWizardSequence>();
        
        installAction.setProperty(InstallAction.TITLE_PROPERTY,
                DEFAULT_IA_TITLE);
        installAction.setProperty(InstallAction.DESCRIPTION_PROPERTY,
                DEFAULT_IA_DESCRIPTION);
    }
    
    @Override
    public void executeForward() {
        final Registry registry = Registry.getInstance();
        final List<Product> toInstall = registry.getProductsToInstall();
        final List<Product> toUninstall = registry.getProductsToUninstall();
        
        // remove all current children (if there are any), as the components
        // selection has probably changed and we need to rebuild from scratch
        getChildren().clear();
        
        // the set of wizard components differs greatly depending on the execution
        // mode - if we're installing, we ask for input, run a wizard sequence for
        // each selected component and then download and install; if we're creating
        // a bundle, we only need to download and package things
        switch (ExecutionMode.getCurrentExecutionMode()) {
            case NORMAL:
                if (toInstall.size() > 0) {                    
                    addChild(downloadConfigurationLogicAction);                    
                    //addChild(licensesPanel);
                    
                    for (Product product: toInstall) {
                        if (!productSequences.containsKey(product)) {
                            productSequences.put(
                                    product,
                                    new ProductWizardSequence(product));
                        }
                        
                        addChild(productSequences.get(product));
                    }
                }
                
                addChild(nbPreInstallSummaryPanel);
                
                if (toUninstall.size() > 0) {
                    addChild(serviceTagAction);
                    addChild(uninstallAction);                    
                }
                
                if (toInstall.size() > 0) {
                    addChild(downloadInstallationDataAction);
                    addChild(installAction);
                    addChild(serviceTagAction);
                
                    addChild(new WizardAction() {
                        @Override
                        public void execute() {
                            final Registry registry = Registry.getInstance();

                            File root = registry.getProducts("ss-base").get(0).getInstallationLocation();
                            String dir = Utils.getMainDirectory();
                            File tmpF = new File(root.getAbsolutePath() + File.separator + dir);
                            tmpF.mkdir();
                            LogManager.log("Creating dir=" + tmpF);
                            for (File filetoMove : root.listFiles(new FilenameFilter() {

                                public boolean accept(File dir, String name) {
                                    return name.endsWith("install.sh");
                                }
                            })) {                                   
                                LogManager.log("Moving: " + filetoMove + " to " + root.getAbsolutePath() 
                                        + File.separator + dir + File.separator + filetoMove.getName());
                                filetoMove.renameTo(new File(root.getAbsolutePath() + File.separator + dir 
                                        + File.separator + filetoMove.getName()));
                            }
                            new File(root, "dummy").delete();
                        }
                    });
                  
                }
                
                addChild(nbPostInstallSummaryPanel);
                if (toInstall.size() > 0) {
                    addChild(nbRegistrationAction);
                }
                
                StringBuilder list = new StringBuilder();
                for (Product product: toInstall) {
                    list.append(product.getUid() + "," + product.getVersion() + ";");
                }
                System.setProperty(
                        LIST_OF_PRODUCTS_TO_INSTALL_PROPERTY,
                        list.toString());
                
                list = new StringBuilder();
                for (Product product: toUninstall) {
                    list.append(product.getUid() + "," + product.getVersion() + ";");
                }
                System.setProperty(
                        LIST_OF_PRODUCTS_TO_UNINSTALL_PROPERTY,
                        list.toString());
                
                list = new StringBuilder();
                for (Product product: toInstall) {
                    for (WizardComponent component: productSequences.get(product).getChildren()) {
                        list.append(component.getClass().getName() + ";");
                    }
                }
                System.setProperty(
                        PRODUCTS_PANEL_FLOW_PROPERTY,
                        list.toString());
                
                if (SilentLogManager.isLogManagerActive() && toInstall.isEmpty()) {
                    SilentLogManager.forceLog(CheckStatus.ERROR, DEFAULT_ERROR_EVERYTHING_IS_INSTALLED);
                    SilentLogManager.forceLog(CheckStatus.ERROR, SystemCheckSequence.CRITICAL_ERROR_MESSAGE);
                }                        
                
                break;
            case CREATE_BUNDLE:
                addChild(preCreateBundleSummaryPanel);
                addChild(downloadConfigurationLogicAction);
                addChild(downloadInstallationDataAction);
                addChild(createBundleAction);
                addChild(createNativeLauncherAction);
                addChild(postCreateBundleSummaryPanel);
                break;
            default:
                // there is no real way to recover from this fancy error, so we
                // inform the user and die
                ErrorManager.notifyCritical(
                        "A terrible and weird error happened - installer's " +
                        "execution mode is not recognized");
        }
        
        super.executeForward();
    }
    
    @Override
    public boolean canExecuteForward() {
        return true;
    }
    
    /////////////////////////////////////////////////////////////////////////////////
    // Constants
    public static final String DEFAULT_ERROR_EVERYTHING_IS_INSTALLED =
            ResourceUtils.getString(MainSequence.class,
            "MS.error.everything.is.installed"); // NOI18N    
    public static final String DEFAULT_IA_TITLE =
            ResourceUtils.getString(
            MainSequence.class,
            "MS.IA.title"); // NOI18N
    public static final String DEFAULT_IA_DESCRIPTION =
            ResourceUtils.getString(
            MainSequence.class,
            "MS.IA.description"); // NOI18N
    
    public static final String LIST_OF_PRODUCTS_TO_INSTALL_PROPERTY =
            "nbi.products.to.install"; // NOI18N
    
    public static final String LIST_OF_PRODUCTS_TO_UNINSTALL_PROPERTY =
            "nbi.products.to.uninstall"; // NOI18N
    
    public static final String PRODUCTS_PANEL_FLOW_PROPERTY =
            "nbi.products.panel.flow"; // NOI18N
}
