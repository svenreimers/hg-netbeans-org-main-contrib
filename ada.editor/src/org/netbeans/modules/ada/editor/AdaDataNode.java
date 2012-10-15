/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.ada.editor;

import java.awt.Image;
import java.text.DateFormat;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author Andrea Lucarelli
 */
public class AdaDataNode extends SourceDataNode implements FileChangeListener {

    private static final String ADS_ICON = "/org/netbeans/modules/ada/editor/resources/icons/ads-16.png";
    private static final String ADB_ICON = "/org/netbeans/modules/ada/editor/resources/icons/adb-16.png";
    private static final String ADA_SPEC_ICON = "/org/netbeans/modules/ada/editor/resources/icons/ada-spec-16.png";
    private static final String ADA_BODY_ICON = "/org/netbeans/modules/ada/editor/resources/icons/ada-body-16.png";
    private AdaDataObject obj;
    private String displayName;
    private String tooltip;
    private Image icon;

    public AdaDataNode(SourceDataObject obj) {
        super(obj, obj.getLookup(), ADS_ICON);
        //Add file change listener to the FileObject:
        //obj.getPrimaryFile().addFileChangeListener(this);
        //Set default icon:
        if (obj.getPrimaryFile().getExt().equalsIgnoreCase("ads")) {
            setIconBaseWithExtension(ADS_ICON);
            icon = ImageUtilities.loadImage(ADS_ICON);
        } else if (obj.getPrimaryFile().getExt().equalsIgnoreCase("adb")) {
            setIconBaseWithExtension(ADB_ICON);
            icon = ImageUtilities.loadImage(ADB_ICON);
        } else {
            // TODO: manage the contents for set the icon
            setIconBaseWithExtension(ADA_SPEC_ICON);
            icon = ImageUtilities.loadImage(ADA_SPEC_ICON);
        }

        //Set default tooltip:
        tooltip = obj.getPrimaryFile().getNameExt();
        setShortDescription (tooltip);
    }

    @Override
    public String getDisplayName() {
        if (null != displayName) {
            return displayName;
        }
        return super.getDisplayName();
    }

    @Override
    public String getShortDescription() {
        if (null != tooltip) {
            return tooltip;
        }
        return super.getShortDescription();
    }

    @Override
    public Image getIcon(int arg0) {
        if (null != icon) {
            return icon;
        }
        return super.getIcon(arg0);
    }

    //When the file changes...
    @Override
    public void fileChanged(FileEvent arg0) {

        //Get the milliseconds and format it:
        long mills = System.currentTimeMillis();
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(
                DateFormat.LONG,
                DateFormat.LONG);
        String formatted = dateFormatter.format(mills);

        //Save the current display name:
        String oldDisplayName = displayName;

        //Save the current tooltip:
        String oldShortDescription = tooltip;

        //Set the new display name:
        displayName = "Change (" + formatted + ")";

        //Set the new tooltip:
        tooltip = formatted;

        //Fire change events on the node,
        //which will immediately refresh it with the new values:
        fireDisplayNameChange(oldDisplayName, displayName);
        fireShortDescriptionChange(oldShortDescription, tooltip);
        fireIconChange();
    }

    @Override
    public void fileFolderCreated(FileEvent arg0) {
    }

    @Override
    public void fileDataCreated(FileEvent arg0) {
    }

    @Override
    public void fileDeleted(FileEvent arg0) {
    }

    @Override
    public void fileRenamed(FileRenameEvent arg0) {
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent arg0) {
    }
}
