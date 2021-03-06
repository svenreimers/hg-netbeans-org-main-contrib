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

package complete.common;

import java.io.*;
import junit.framework.*;
import org.openide.util.Utilities;
import org.netbeans.junit.*;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;
import org.netbeans.jellytools.properties.*;

/** XTest / JUnit test class performing complete testing of Generic VCS mounting wizard.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class MountingWizard extends JellyTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    public static String PVCS_SET_PASSWORD = "PVCS|Set Password";
    public static String PVCS_LOCK = "PVCS|Lock";
    public static String PVCS_REFRESH = "PVCS|Refresh";
    public static String EMPTY_REFRESH = "Empty|Refresh";
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public MountingWizard(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return MountingWizard test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        suite.addTest(new MountingWizard("testProfileSelector"));
        suite.addTest(new MountingWizard("testButtons"));
        suite.addTest(new MountingWizard("testAdditionalProfilesLink"));
        suite.addTest(new MountingWizard("testAutoLocking"));
        suite.addTest(new MountingWizard("testAutoEditing"));
        suite.addTest(new MountingWizard("testOSCompatibility"));
        suite.addTest(new MountingWizard("testEnvironmentSetup"));
        suite.addTest(new MountingWizard("testSettingsPropagated"));
        suite.addTest(new MountingWizard("testSettingsActive"));
        return suite;
    }
    
    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** Method called before each testcase. Redirects system output.
     */
    protected void setUp() throws Exception {
        org.netbeans.jemmy.JemmyProperties.setCurrentOutput(org.netbeans.jemmy.TestOut.getNullOutput());
    }

    /** Tests profile selector. No item must be missing and behavior must be correct.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testProfileSelector() throws Exception {
        try {
            System.out.print(".. Testing profile selector ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizard = new VCSWizardProfile();
//            wizard.checkOnlyCompatibleProfiles(false);
            String profile = VCSWizardProfile.CVS_UNIX;
            wizard.setProfile(profile);
            wizard.verify(profile);
            profile = VCSWizardProfile.CVS_WIN_95;
            wizard.setProfileNoBlock(profile);
            String question = "Do you really want to discard current commands and variables and replace them with the predefined";
            new JLabelOperator(new JDialogOperator("Question"), "settings for " + profile + "?");
            new QuestionDialogOperator(question).no();
            wizard.verify(VCSWizardProfile.CVS_UNIX);
            String[] profiles = new String[] {VCSWizardProfile.CVS_WIN_95, VCSWizardProfile.CVS_WIN_NT, VCSWizardProfile.EMPTY_UNIX,
            VCSWizardProfile.EMPTY_WIN, VCSWizardProfile.PVCS_UNIX, VCSWizardProfile.PVCS_WIN_95, VCSWizardProfile.PVCS_WIN_NT,
            VCSWizardProfile.VSS_WIN_95, VCSWizardProfile.VSS_WIN_NT};
            for (int i=0; i<profiles.length; i++) {
                wizard.setProfileNoBlock(profiles[i]);
                new JLabelOperator(new JDialogOperator("Question"), "settings for " + profiles[i] + "?");
                new QuestionDialogOperator(question).yes();
                Thread.sleep(3000);
                long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
                org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
                try { new NbDialogOperator("Exception").ok(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
                org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
                wizard.verify(profiles[i]);
            }
            wizard.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new QuestionDialogOperator().no(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Tests "Browse..." and "Select..." buttons in the most complex profile.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testButtons() throws Exception {
        try {
            System.out.print(".. Testing browsing buttons ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizard = new VCSWizardProfile();
//            wizard.checkOnlyCompatibleProfiles(false);
            wizard.setProfile(VCSWizardProfile.VSS_WIN_95);
            String directory = getWorkDirPath() + File.separator + "test";
            String file = directory + File.separator + "Test.txt";
            new File(directory + File.separator + "another").mkdirs();
            new File(file).createNewFile();
            wizard.browseWorkingDirectory();
            JFileChooserOperator fileChooser = new JFileChooserOperator();
            fileChooser.chooseFile(directory);
            if (!wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_WORKING_DIRECTORY).getText().equals(directory))
                throw new Exception("Error: Unable to browse to desired working directory.");
            wizard.selectRelativeMountPoint();
            NbDialogOperator mountPoint = new NbDialogOperator("Relative Mount Point Browser");
            new Node(new JTreeOperator(mountPoint), "another").select();
            new JTextFieldOperator(mountPoint, "another");
            mountPoint.ok();
            if (!wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_RELATIVE_MOUNTPOINT).getText().equals("another"))
                throw new Exception("Error: Unable to select desired relative mount point.");
            wizard.browseVSSExecutable();
            fileChooser = new JFileChooserOperator();
            fileChooser.chooseFile(file);
            if (!wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_VSS_EXECUTABLE).getText().equals(file))
                throw new Exception("Error: Unable to select desired VSS executable.");
            wizard.browseVSSSSDIR();
            fileChooser = new JFileChooserOperator();
            fileChooser.chooseFile(directory);
            if (!wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_VSS_SSDIR).getText().equals(directory))
                throw new Exception("Error: Unable to browse to desired SSDIR directory.");
            wizard.browseVSSShell();
            fileChooser = new JFileChooserOperator();
            fileChooser.chooseFile(file);
            if (!wizard.txtJTextField(VCSWizardProfile.INDEX_TXT_VSS_SHELL).getText().equals(file))
                throw new Exception("Error: Unable to select desired VSS unix shell.");
            wizard.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Tests that hypertext link to additional profiles works correctly.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testAdditionalProfilesLink() throws Exception {
        try {
            System.out.print(".. Testing additional profiles link ..");
            OptionsOperator.invoke();
            OptionsOperator options = new OptionsOperator();
            options.selectOption("IDE Configuration|System|System Settings");
            org.netbeans.jellytools.properties.Property property = new org.netbeans.jellytools.properties.Property(options, "Web Browser");
            property.setValue("External Browser (Command Line)");
            options.selectOption("IDE Configuration|Server and External Tool Settings|Web Browsers|External Browser (Command Line)");
            property = new org.netbeans.jellytools.properties.Property(options, "Browser Executable");
            property.setValue("abcd");
            options.close();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            final VCSWizardProfile wizard = new VCSWizardProfile();
            new JLabelOperator(wizard, "http://vcsgeneric.netbeans.org/profiles/index.html").clickMouse();
            new NbDialogOperator("Warning").ok();
            wizard.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new NbFrameOperator("Options").close(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Tests that auto-locking checkbox work on Advanced panel of the wizard.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testAutoLocking() throws Exception {
        try {
            System.out.print(".. Testing auto locking feature ..");
            new File(getWorkDirPath()).mkdirs();
            File file = new File(getWorkDirPath() + File.separator + "A_File.java");
            file.createNewFile();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.setProfile(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
            wizardProfile.setWorkingDirectory(getWorkDirPath());
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            JCheckBoxOperator checkBox = wizardAdvanced.cbCallLOCKCommand();
            if (!(checkBox.isEnabled() && !checkBox.isSelected())) throw new Exception("Error: Call LOCK checkbox has incorrect state.");
            checkBox = wizardAdvanced.cbPromptForLOCK();
            if (!(!checkBox.isEnabled() && checkBox.isSelected())) throw new Exception("Error: Prompt for LOCK checkbox has incorrect state.");
            JTextFieldOperator question = wizardAdvanced.txtMessageLOCK();
            if (!question.getText().equals(""))
                throw new Exception("Error: LOCK question has incorrect text.");
            wizardAdvanced.checkCallLOCKCommand(true);
            if (!checkBox.isEnabled())
                throw new Exception("Error: Can't enable prompt for EDIT checkbox.");
            wizardAdvanced.checkPromptForLOCK(false);
            if (question.isEnabled())
                throw new Exception("Error: Can't disable LOCK question.");
            wizardAdvanced.checkPromptForLOCK(true);
            wizardAdvanced.txtMessageLOCK().enterText("Do you want to lock the file ?");
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.addCommand("Empty", "Status");
            commandEditor.selectCommand("Empty|Status");
            PropertySheetOperator sheet = new PropertySheetOperator(commandEditor);
            Property property = new Property(sheet, "Exec");
            property.setValue(Utilities.isUnix() ? "sh -c \"echo ${FILE} New\"" : "cmd /x /c \"echo ${FILE} New\"");
            property = new Property(sheet, "Data Regex");
            property.setValue("^(.*) (.*$)");
            property = new Property(sheet, "File Index");
            property.setValue("0");
            property = new Property(sheet, "Status Index");
            property.setValue("1");
            commandEditor.ok();
            wizardAdvanced.finish();
            Thread.sleep(2000);
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath());
            filesystemNode.expand();
            Node fileNode = new Node(filesystemNode, "A_File");
            new Action(null, "Empty|Status").perform(fileNode);
            new OpenAction().perform(fileNode);
            Thread.sleep(2000);
            EditorOperator editor = new EditorOperator(new EditorWindowOperator(), "A_File");
            java.awt.Robot robot = new java.awt.Robot();
            robot.keyPress(32);
            robot.delay(100);
            robot.keyRelease(32);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new QuestionDialogOperator("Do you want to lock the file ?").yes(); }
            catch (org.netbeans.jemmy.TimeoutExpiredException te) { throw new Exception("Error: Auto-locking does not work."); }
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            new Node(new ExplorerOperator().runtimeTab().getRootNode(), "VCS Commands|Empty " + getWorkDirPath() + "|Lock");
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath()));
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "Empty " + getWorkDirPath())); }
            catch (Exception te) {}
            try { new QuestionDialogOperator().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Tests that auto-editing checkbox work on Advanced panel of the wizard.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testAutoEditing() throws Exception {
        try {
            System.out.print(".. Testing auto editing feature ..");
            new File(getWorkDirPath()).mkdirs();
            File file = new File(getWorkDirPath() + File.separator + "A_File.java");
            file.createNewFile();
            file.setReadOnly();
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.setProfile(Utilities.isUnix() ? VCSWizardProfile.CVS_UNIX : VCSWizardProfile.CVS_WIN_NT);
            wizardProfile.txtJTextField(VCSWizardProfile.INDEX_TXT_WORKING_DIRECTORY).clearText();
            wizardProfile.setWorkingDirectory(getWorkDirPath());
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            JCheckBoxOperator checkBox = wizardAdvanced.cbCallEDITCommand();
            if (!(checkBox.isEnabled() && checkBox.isSelected())) throw new Exception("Error: Call EDIT checkbox has incorrect state.");
            checkBox = wizardAdvanced.cbPromptForEDIT();
            if (!(checkBox.isEnabled() && checkBox.isSelected())) throw new Exception("Error: Prompt for EDIT checkbox has incorrect state.");
            JTextFieldOperator question = wizardAdvanced.txtMessageEDIT();
            if (!question.getText().equals("Do you want to run the Edit command to make the file writable?"))
                throw new Exception("Error: EDIT question has incorrect text.");
            wizardAdvanced.checkPromptForEDIT(false);
            if (question.isEnabled())
                throw new Exception("Error: Can't disable EDIT question.");
            wizardAdvanced.checkCallEDITCommand(false);
            if (checkBox.isEnabled())
                throw new Exception("Error: Can't disable prompt for EDIT checkbox.");
            wizardAdvanced.checkCallEDITCommand(true);
            wizardAdvanced.checkPromptForEDIT(true);
            wizardAdvanced.txtMessageEDIT().enterText("Do you want to edit the file ?");
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.deleteCommand("CVS|Refresh");
            commandEditor.deleteCommand("CVS|Offline Refresh");
            commandEditor.selectCommand("CVS|Refresh");
            String executionString = "sh -c \"echo A_File.java Up-to-date\"";
            if (org.openide.util.Utilities.isWindows()) executionString = "cmd /x /c \"echo A_File.java Up-to-date\"";
            PropertySheetOperator sheet = new PropertySheetOperator(commandEditor);
            Property property = new Property(sheet, "Exec");
            property.setValue(executionString);
            Thread.sleep(1000);
            if (!new Property(sheet, "Exec").getValue().equals(executionString))
                throw new Exception("Error: Unable to set execution string.");
            property = new Property(sheet, "Data Regex");
            property.setValue("^(.*) (.*$)");
            Thread.sleep(1000);
            if (!new Property(sheet, "Data Regex").getValue().equals("^(.*) (.*$)"))
                throw new Exception("Error: Unable to set data regex property.");
            commandEditor.ok();
            wizardAdvanced.finish();
            Thread.sleep(2000);
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), "CVS " + getWorkDirPath());
            filesystemNode.expand();
            Thread.sleep(3000);
            new Action(null, "CVS|Refresh").perform(new Node(filesystemNode, "A_File"));
            Thread.sleep(2000);
            new OpenAction().perform(new Node(filesystemNode, "A_File"));
            EditorOperator editor = new EditorOperator("A_File");
            java.awt.Robot robot = new java.awt.Robot();
            robot.keyPress(32);
            robot.delay(100);
            robot.keyRelease(32);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new QuestionDialogOperator("Do you want to edit the file ?").yes(); }
            catch (org.netbeans.jemmy.TimeoutExpiredException te) { throw new Exception("Error: Auto-editing does not work."); }
            try { new NbDialogOperator("Information").ok(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            new Node(new ExplorerOperator().runtimeTab().getRootNode(), "VCS Commands|CVS " + getWorkDirPath() + "|Edit");
            new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "CVS " + getWorkDirPath()));
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "CVS " + getWorkDirPath())); }
            catch (Exception te) {}
            try { new QuestionDialogOperator().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Tests that operating system detection and profile selector work correctly.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testOSCompatibility() throws Exception {
        try {
            System.out.print(".. Testing operating system detection ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
//            wizardProfile.checkOnlyCompatibleProfiles(false);
            String profile = Utilities.isUnix() ? VCSWizardProfile.EMPTY_WIN : VCSWizardProfile.EMPTY_UNIX;
            wizardProfile.setProfile(profile);
//            if (wizardProfile.cbOnlyCompatibleProfiles().isEnabled())
//                throw new Exception("Error: Can't disable show only compatible profiles checkbox.");
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            new JLabelOperator(wizardAdvanced, System.getProperty("os.name"));
            wizardAdvanced.back();
            String[] profiles = new String[] {VCSWizardProfile.CVS_WIN_95, VCSWizardProfile.CVS_WIN_NT, VCSWizardProfile.EMPTY_UNIX,
            VCSWizardProfile.EMPTY_WIN, VCSWizardProfile.PVCS_UNIX, VCSWizardProfile.PVCS_WIN_95, VCSWizardProfile.PVCS_WIN_NT,
            VCSWizardProfile.VSS_WIN_95, VCSWizardProfile.VSS_WIN_NT};
            for (int i=0; i<profiles.length; i++) {
                profile = profiles[i];
                wizardProfile.setProfileNoBlock(profile);
                new QuestionDialogOperator().yes();
                Thread.sleep(3000);
                long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
                org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
                try { new NbDialogOperator("Exception").ok(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
                org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
                wizardProfile.next();
                String compatibleWith = "\"Windows\"";
                String incompatibleWith = "\"Windows 95\", \"Windows 98\", \"Windows Me\"";
                if (profile.indexOf("Unix") != -1) {
                    compatibleWith = "\"Unix\"";
                    incompatibleWith = "\"Windows\"";
                }
                if (profile.indexOf("95/98/ME") != -1) {
                    compatibleWith = incompatibleWith;
                    incompatibleWith = "";
                }
                if (!wizardAdvanced.txtCompatibleWithOS().getText().equals(compatibleWith))
                    throw new Exception("Error: Incorrect value of compatible OS textfield for " + profile +" profile.");
                if (!wizardAdvanced.txtIncompatibleWithOS().getText().equals(incompatibleWith))
                    throw new Exception("Error: Incorrect value of incompatible OS textfield for " + profile +" profile.");
                wizardAdvanced.back();
            }
            wizardProfile.cancel();
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Tests that environment variables can be setup on the last panel of the wizard.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testEnvironmentSetup() throws Exception {
        try {
            System.out.print(".. Testing environment setup ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.setProfile(Utilities.isUnix() ? VCSWizardProfile.EMPTY_UNIX : VCSWizardProfile.EMPTY_WIN);
            new File(getWorkDirPath()).mkdirs();
            wizardProfile.setWorkingDirectory(getWorkDirPath());
            wizardProfile.next();
            new VCSWizardAdvanced().next();
            VCSWizardEnvironment wizardEnvironment = new VCSWizardEnvironment();
            wizardEnvironment.insert();
            NewElement newVariable = new NewElement(NewElement.ELEMENT_ENVIRONMENT);
            newVariable.setElementName("MYNAME");
            newVariable.ok();
            if (wizardEnvironment.tabUserVariables().getRowCount() != 1)
                throw new Exception("Error: Can't create new environment variable.");
            wizardEnvironment.delete();
            new QuestionDialogOperator("Are you sure you want to delete the variable MYNAME?").no();
            if (wizardEnvironment.tabUserVariables().getRowCount() == 0)
                throw new Exception("Error: Environment variable was unintentionally deleted.");
            wizardEnvironment.delete();
            new QuestionDialogOperator("Are you sure you want to delete the variable MYNAME?").yes();
            if (wizardEnvironment.tabUserVariables().getRowCount() != 0)
                throw new Exception("Error: Environment variable can't be deleted.");
            wizardEnvironment.insert();
            newVariable = new NewElement(NewElement.ELEMENT_ENVIRONMENT);
            newVariable.setElementName("MYNAME");
            newVariable.ok();
            wizardEnvironment.tabUserVariables().clickForEdit(0, 1);
            new JTextFieldOperator(wizardEnvironment.tabUserVariables()).enterText("Jirka");
            JTableOperator systemVariableTable = wizardEnvironment.tabSystemVariables();
            int rowCount = systemVariableTable.getRowCount();
            for (int i=0; i<rowCount; i++) {
                String variable = (String) systemVariableTable.getValueAt(i, 0);
                if (variable.equalsIgnoreCase("PATH")) {
                    wizardEnvironment.tabSystemVariables().setValueAt(new Boolean(false), i, 2);
                    wizardEnvironment.tabSystemVariables().clickForEdit( i, 2);
                    break;
                }
                if (i == rowCount - 1) throw new Exception("Error: Can't find PATH variable.");
            }
            wizardEnvironment.back();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.editCommands();
            CommandEditor commandEditor = new CommandEditor();
            commandEditor.selectCommand("Empty|Lock");
            new Node(commandEditor.treeCommands(), "Empty|Lock").select();
            NbDialogOperator dialog = new NbDialogOperator("Command Editor");
            PropertySheetOperator sheet = new PropertySheetOperator(dialog);
            Property property = new Property(sheet, "Exec");
            String command = "cmd /x /c \"echo My name is: %MYNAME% && echo Path = %PATH%\"";
            property.setValue(Utilities.isUnix() ? "sh -c \"echo My name is: $MYNAME; echo Path = $PATH\"" : command);
            property = new Property(sheet, "Display Output");
            property.setValue("True");
            commandEditor.ok();
            wizardEnvironment.finish();
            Thread.sleep(2000);
            Node rootNode = new ExplorerOperator().repositoryTab().getRootNode();
            String filesystem = "Empty " + getWorkDirPath();
            new Action(null, "Empty|Lock").perform(new Node(rootNode, filesystem));
            Thread.sleep(3000);
            VCSCommandsOutputOperator outputWindow = new VCSCommandsOutputOperator("Lock");
            String output = outputWindow.txtStandardOutput().getText();
            String desired = Utilities.isUnix() ? "My name is: Jirka \nPath =\n" : "My name is: Jirka \nPath = %PATH%\n";
            outputWindow.close();
            new UnmountFSAction().perform(new Node(rootNode, filesystem));
            if (!output.equals(desired))
                throw new Exception("Error: Incorrect output result. Actual: |" + output + "| Requested: |" + desired + "|");
            System.out.println(". done !");
        } catch (Exception e) {
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            try { new CommandEditor().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            try { new VCSWizardProfile().cancel(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            throw e;
        }
    }

    /** Tests that all remaining checkboxes on "Advanced" panel of the wizard are propagated to filesystem.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testSettingsPropagated() throws Exception {
        try {
            System.out.print(".. Testing propagation of other advanced settings ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.setProfile(Utilities.isUnix() ? VCSWizardProfile.PVCS_UNIX : VCSWizardProfile.PVCS_WIN_NT);
            Thread.sleep(10000);
            new File(getWorkDirPath()).mkdirs();
            wizardProfile.setPVCSWorkfilesLocation(getWorkDirPath());
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.checkAdvancedMode(true);
            wizardAdvanced.checkOfflineMode(true);
            wizardAdvanced.checkPrintCommandOutput(true);
            wizardAdvanced.finish();
            String filesystem = "PVCS " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            filesystemNode.select();
            PropertySheetOperator sheet = new PropertySheetOperator();
            Property property = new Property(sheet, "Advanced Options");
            if (property.getValue().equals("False"))
                throw new Exception("Error: Can't propagate Advanced Mode setting from wizard to filesystem property.");
            property = new ComboBoxProperty(sheet.getPropertySheetTabOperator("Properties"), "Offline Mode");
            if (property.getValue().equals("False"))
                throw new Exception("Error: Can't propagate Offline Mode setting from wizard to filesystem property.");
            property = new ComboBoxProperty(sheet.getPropertySheetTabOperator("Properties"), "Print Command Output");
            if (property.getValue().equals("False"))
                throw new Exception("Error: Can't propagate Print Command Output setting from wizard to filesystem property.");
            new org.netbeans.jellytools.actions.CustomizeAction().perform(filesystemNode);
            Thread.sleep(5000);
            NbDialogOperator customizer = new NbDialogOperator("Customizer Dialog");
            new JTabbedPaneOperator(customizer).selectPage("Advanced");
            JCheckBoxOperator checkBox = new JCheckBoxOperator(customizer, "Advanced Mode");
            if (!checkBox.isSelected())
                throw new Exception("Error: Can't set Advanced Mode checkbox.");
            checkBox = new JCheckBoxOperator(customizer, "Offline Mode");
            if (!checkBox.isSelected())
                throw new Exception("Error: Can't set Offline Mode checkbox.");
            checkBox = new JCheckBoxOperator(customizer, "Print Command Output");
            if (!checkBox.isSelected())
                throw new Exception("Error: Can't set Print Command Output checkbox.");
            customizer.closeByButton();
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "PVCS " + getWorkDirPath())); }
            catch (Exception te) {}
            throw e;
        }
    }

    /** Tests that the same settings are working properly.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testSettingsActive() throws Exception {
        try {
            System.out.print(".. Testing functionality of other advanced settings ..");
            new ActionNoBlock(MOUNT_MENU, null).perform();
            VCSWizardProfile wizardProfile = new VCSWizardProfile();
            wizardProfile.setProfile(Utilities.isUnix() ? VCSWizardProfile.PVCS_UNIX : VCSWizardProfile.PVCS_WIN_NT);
            Thread.sleep(5000);
            new File(getWorkDirPath()).mkdirs();
            JTextFieldOperator textField = wizardProfile.txtJTextField(VCSWizardProfile.INDEX_TXT_PVCS_WORKFILES_LOCATION);
            textField.clearText();
            textField.setText(getWorkDirPath());
            wizardProfile.next();
            VCSWizardAdvanced wizardAdvanced = new VCSWizardAdvanced();
            wizardAdvanced.checkAdvancedMode(true);
            wizardAdvanced.checkOfflineMode(true);
            wizardAdvanced.checkPrintCommandOutput(true);
            wizardAdvanced.finish();
            String filesystem = "PVCS " + getWorkDirPath();
            Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
            new org.netbeans.jellytools.actions.Action(VERSIONING_MENU + "|" + PVCS_REFRESH, PVCS_REFRESH).perform(filesystemNode);
            new QuestionDialogOperator("Refresh is disabled in offline mode. Do you want to turn offline mode off?").no();
            new org.netbeans.jellytools.actions.Action(VERSIONING_MENU + "|" + PVCS_SET_PASSWORD, PVCS_SET_PASSWORD).perform(filesystemNode);
            new NbDialogOperator("Password").cancel();
            OutputWindowOperator outputWindow = new OutputWindowOperator();
            outputWindow.selectPage("Versioning");
            String output = outputWindow.getText();
            if (output.indexOf("Command Set Password finished.") == -1)
                throw new Exception("Error: Print Command Output property does not work.");
            filesystemNode.select();
            PropertySheetOperator sheet = new PropertySheetOperator();
            Property property = new Property(sheet, "Advanced Options");
            property.setValue("False");
            property = new Property(sheet, "Offline Mode");
            property.setValue("False");
            property = new Property(sheet, "Print Command Output");
            property.setValue("False");
            outputWindow.clearOutput();
            new org.netbeans.jellytools.actions.Action(VERSIONING_MENU + "|" + PVCS_LOCK, PVCS_LOCK).perform(filesystemNode);
            NbDialogOperator dialog = new NbDialogOperator("Lock -");
            boolean okay = false;
            try { new JLabelOperator(dialog, "Select what to lock:"); } catch(Exception e) {okay = true;}
            dialog.cancel();
            if (!okay) throw new Exception("Error: Advanced Option property does not work.");
            new org.netbeans.jellytools.actions.Action(VERSIONING_MENU + "|" + PVCS_SET_PASSWORD, PVCS_SET_PASSWORD).perform(filesystemNode);
            new NbDialogOperator("Password").cancel();
            output = outputWindow.getText();
            if (output.indexOf("Command Set Password finished.") != -1)
                throw new Exception("Error: Print Command Output property does not work.");
            new org.netbeans.jellytools.actions.Action(VERSIONING_MENU + "|" + PVCS_REFRESH, PVCS_REFRESH).perform(filesystemNode);
            long oldTimeout = org.netbeans.jemmy.JemmyProperties.getCurrentTimeout("DialogWaiter.WaitDialogTimeout");
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", 2000);
            okay = false;
            try { new QuestionDialogOperator("Refresh is disabled in offline mode. Do you want to turn offline mode off?").no(); } catch (org.netbeans.jemmy.TimeoutExpiredException te) {okay = true;}
            org.netbeans.jemmy.JemmyProperties.setCurrentTimeout("DialogWaiter.WaitDialogTimeout", oldTimeout);
            if (!okay) throw new Exception("Error: Offline Mode property does not work.");
            new UnmountFSAction().perform(filesystemNode);
            System.out.println(". done !");
        } catch (Exception e) {
            try { new UnmountFSAction().perform(new Node(new ExplorerOperator().repositoryTab().getRootNode(), "PVCS " + getWorkDirPath())); }
            catch (Exception te) {}
            throw e;
        }
    }
}
