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
package beans2nbm;

import beans2nbm.gen.JarInfo;
import beans2nbm.gen.JarToCopyModel;
import beans2nbm.gen.LayerFileModel;
import beans2nbm.gen.LibDescriptorModel;
import beans2nbm.gen.ModuleInfoModel;
import beans2nbm.gen.ModuleModel;
import beans2nbm.gen.ModuleXMLModel;
import beans2nbm.gen.NbmFileModel;
import beans2nbm.gen.PaletteItemFileModel;
import beans2nbm.ui.AuthorInfoPage;
import beans2nbm.ui.BeanItem;
import beans2nbm.ui.InstructionsPage;
import beans2nbm.ui.LibDataPage;
import beans2nbm.ui.LocateJarPage;
import beans2nbm.ui.OutputLocationPage;
import beans2nbm.ui.SelectBeansPage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

/**
 *
 * @author Tim Boudreau
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0 && "reset".equals(args[0])) {
            Preferences.userNodeForPackage(
                    InstructionsPage.class).putBoolean(
                    InstructionsPage.KEY_SHOW_INSTRUCTIONS, true);
        }
        
        setLookAndFeel();
        
//        go (getPageList());

        WizardDisplayer.showWizard(new Brancher(new WRP()).createWizard());
    }

    static Class[] getPageList() {
        Class[] pages = InstructionsPage.shouldShowInstructions() ?
            new Class[] {
                InstructionsPage.class,
                LocateJarPage.class, 
                SelectBeansPage.class,
                LibDataPage.class, 
                AuthorInfoPage.class,
                OutputLocationPage.class,
        } : new Class[] {
                LocateJarPage.class, 
                SelectBeansPage.class,
                LibDataPage.class, 
                AuthorInfoPage.class,
                OutputLocationPage.class,
        };
        return pages;
    }
    
    public static void go (Class[] pages) {
        Wizard wiz = WizardPage.createWizard(pages, new WRP());
        File f = (File) WizardDisplayer.showWizard (wiz);
        if (f != null) {
            System.out.println("Created " + f.getPath());
        }
    }
    
    private static class BackgroundBuilder extends DeferredWizardResult {
        public BackgroundBuilder() {
            super (false);
        }
        
        public void start(final Map map, final DeferredWizardResult.ResultProgressHandle progress) {
            final int total = 15;
            progress.setProgress("Building NBM", 1, total);
            
            String destFolder = (String) map.get ("destFolder");
            String destFileName = (String) map.get ("destFileName");
            String description = (String) map.get ("description");
            String version = (String) map.get ("libversion");
            String homepage = (String) map.get ("homepage");
            JarInfo jarInfo = (JarInfo) map.get ("jarInfo");
            String codeName = (String) map.get ("codeName");
            String jarFileName = (String) map.get ("jarFileName");
            String author = (String) map.get ("author");
            String docsJar = (String) map.get ("docsJar");
            String sourceJar = (String) map.get ("sourceJar");
            String displayName = (String) map.get ("displayName");
            String license = (String) map.get ("license");
            String minJDK = (String) map.get ("javaVersion");

            
            
            File outDir = new File (destFolder);
            if (!outDir.isDirectory()) {
//                throw new WizardException ("Couldn't write to " + destFolder);
                throw new IllegalArgumentException ();
            }
            File f = new File (outDir, destFileName);
            if (f.exists()) {
                f.delete();
            }
            try {
                if (!f.createNewFile()) {
                    progress.failed("Could not create " + f.getPath(), true);
                    return;
                }
                progress.setProgress(2, total);
                char[] cname = codeName.toCharArray();
                for (int i=0; i < cname.length; i++) {
                    if (cname[i] == '.') {
                        cname[i] = '-';
                    }
                }
                String moduleJarName = new String (cname) + ".jar";

                String jarFileNameSimple = new File (jarFileName).getName();
                progress.setProgress(3, 10);
                NbmFileModel nbm = new NbmFileModel (f.getPath());
                progress.setProgress (3, total);
                
                ModuleModel module = new ModuleModel ("netbeans/modules/" + moduleJarName, codeName, description, version, displayName, minJDK);
                progress.setProgress(4, total);
                ModuleInfoModel infoXml = new ModuleInfoModel (module, homepage, author, license);
                progress.setProgress(5, total);
                
                nbm.add (module);
                nbm.add (infoXml);
                
                JarToCopyModel libJar = new JarToCopyModel ("netbeans/libs/" + jarFileNameSimple, jarFileName);
                progress.setProgress (7, total);
                nbm.add (libJar);

                String srcFileNameSimple = null;
                if (sourceJar != null && !"".equals(sourceJar)) {
                    srcFileNameSimple = new File (sourceJar).getName();
                    JarToCopyModel srcJarMdl = new JarToCopyModel ("netbeans/sources/" + srcFileNameSimple, sourceJar);
                    nbm.add (srcJarMdl);
                }
                progress.setProgress(8, total);
                String docsJarNameSimple = null;
                if (docsJar != null && !"".equals(docsJar)) {
                    docsJarNameSimple = new File (docsJar).getName();
                    JarToCopyModel docsJarMdl = new JarToCopyModel ("netbeans/docs/" + docsJarNameSimple, docsJar);
                    nbm.add (docsJarMdl);
                }
                progress.setProgress(9, total);
                cname = codeName.toCharArray();
                for (int i=0; i < cname.length; i++) {
                    if (cname[i] == '.') {
                        cname[i] = '/';
                    }
                }
                String codeNameSlashes = new String (cname);
                
                char[] c = displayName.toCharArray();
                for (int i=0; i < c.length; i++) {
                    if (Character.isWhitespace(c[i])) {
                        c[i] = '_';
                    }
                }
                
                String companyNameUnderscores = new String (c);
                
                LayerFileModel layer = new LayerFileModel (codeNameSlashes + "/layer.xml", companyNameUnderscores, codeName);
                module.addFileEntry(layer);
                progress.setProgress (10, total);
                
                layer.addLibraryName(companyNameUnderscores);
                
                LibDescriptorModel libDesc = new LibDescriptorModel (codeNameSlashes + "/" + companyNameUnderscores + ".xml", companyNameUnderscores, codeName, jarFileNameSimple, srcFileNameSimple, docsJarNameSimple);
                module.addFileEntry(libDesc);
                module.addFileDisplayName("org-netbeans-api-project-libraries/Libraries/" + companyNameUnderscores + ".xml", displayName);
                module.addFileDisplayName(companyNameUnderscores, displayName);
                progress.setProgress (11, total);
                
                if (jarInfo != null) {
                    for (Iterator i=jarInfo.getBeans().iterator(); i.hasNext();) {
                        String pathInBeanJar = (String) i.next();
                        BeanItem bi = new BeanItem (pathInBeanJar);
                        String beanClassName = bi.getClassName();
                        String beanSimpleName = bi.getSimpleName();
                        String paletteItemPathInLayer = "FormDesignerPalette/" + companyNameUnderscores + "/" + beanSimpleName + ".palette_item";
                        PaletteItemFileModel palMdl = new PaletteItemFileModel (codeNameSlashes + "/" + beanSimpleName + "_paletteItem.xml", companyNameUnderscores, beanClassName);
                        module.addFileEntry(palMdl);
                        layer.addBeanEntry(companyNameUnderscores, beanSimpleName);
                        module.addFileDisplayName (paletteItemPathInLayer, beanClassName);
                    }
                }
                progress.setProgress (11, total);
                module.addFileDisplayName(codeNameSlashes + "/" + companyNameUnderscores, displayName);
                
                ModuleXMLModel mxml = new ModuleXMLModel (codeName,version);
                nbm.add(mxml);
                progress.setProgress (12, total);
                
                OutputStream out = new BufferedOutputStream (new FileOutputStream (f));
                progress.setProgress (13, total);
                nbm.write(out);
                progress.setProgress (14, total);
                
                out.close();
                progress.setProgress (15, total);
                
            } catch (IOException ioe) {
                ioe.printStackTrace();
                progress.failed(ioe.getMessage(), true);
            }
            progress.finished(Summary.create ("Created " + 
                    f.getPath() + " successfully.", f));
        }
    }
 
    private static class WRP implements WizardResultProducer {
        public Object finish(Map map) throws WizardException {
            return new BackgroundBuilder();
//            outMap (map);
        }

        public boolean cancel (Map m) {
            System.exit(0);
            return true;
        }
    }
    
    private static final void outMap (Map wizardData) {
        for (Iterator i = wizardData.keySet().iterator(); i.hasNext();) {
            Object key = (Object) i.next();
            Object val = wizardData.get(key);
        }
    }
    
}
