/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.ui;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.modules.remotefs.core.ConnectAction;
import org.netbeans.modules.remotefs.ftpfs.FTPFileSystem;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.actions.*;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

class SiteNode extends FilterNode {

    private final FTPFileSystem site;
    private Logger logger = Logger.getLogger(SiteNode.class.getName());

    public SiteNode(FTPFileSystem site) throws DataObjectNotFoundException {
        super(DataObject.find(site.getRoot()).getNodeDelegate());
        this.site = site;
    }

    @Override
    public Image getIcon(int type) {
        return Utilities.loadImage("org/netbeans/modules/remotefs/ui/resources/globe-sextant-16x16.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public String getHtmlDisplayName() {
        return "<b><font color=\"00AA00\">" + getName() + "</font></b>";
    }

    @Override
    public String getDisplayName() {
        return site.getDisplayName();
    }

    @Override
    public String getName() {
        return site.getDisplayName();
    }

    @Override
    public Action[] getActions(boolean context) {
//        &Find... $org.openide.actions.FindAction
//        &New $org.openide.actions.NewTemplateAction
//        Rename... $org.openide.actions.RenameAction
//        Cu&t $org.openide.actions.CutAction
//        &Copy $org.openide.actions.CopyAction
//        &Paste $org.openide.actions.PasteAction
//        &Delete $org.openide.actions.DeleteAction
//        Filesystem Action $org.openide.actions.FileSystemAction
//        Tools $org.openide.actions.ToolsAction
//        &Properties $org.openide.actions.PropertiesAction

        Action[] actions = super.getActions(context);
        List<Action> newActions = new ArrayList<Action>();
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] != null) {
                String clazz = actions[i].getClass().getName();
                if("org.openide.actions.FileSystemAction".equals(clazz)){
                   newActions.add(actions[i]);
                }
                if("org.openide.actions.PropertiesAction".equals(clazz)){
                   newActions.add(actions[i]);
                }
            }
        }
//        newActions.add(getAction(RemoveSiteAction.class));
        return newActions.toArray(new SystemAction[0]);
//        return super.getActions(context);
    }

    private SystemAction getAction(Class clazz) {
        return (SystemAction) org.openide.util.SharedClassObject.findObject(clazz, true);
    }
    
    @Override
    public Action getPreferredAction() {
        return getAction(ConnectAction.class);
    }
    
    
    
    
    public Node.Property[] getProperties() {
        Node.Property[] props = new Node.Property[5];
        try {
            props[0] = new PropertySupport.Reflection(site, String.class, "server");
            props[0].setName("Server name or IP");
            props[1] = new PropertySupport.Reflection(site, String.class, "username");
            props[1].setName("Username");
            props[2] = new PropertySupport.Reflection(site, String.class, "password");
            props[2].setName("Password");
            props[3] = new PropertySupport.Reflection(site, int.class, "port");
            props[3].setName("Port");
            props[4] = new PropertySupport.Reflection(site, String.class, "startdir");
            props[4].setName("Root folder");
//            Property prop6 = new PropertySupport.Reflection(site, File.class, "cache");
//            prop6.setName("cache folder");
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return props;
    }

    @Override
    public Node.PropertySet[] getPropertySets() {
        Sheet.Set props = new Sheet.Set();
        props.setName("Basic Properties");
        props.put(getProperties());
        return new Node.PropertySet[]{props};
    }

//    @Override
//    protected Sheet createSheet() {
//        Sheet result = super.createSheet();
//        Sheet.Set set = Sheet.createPropertiesSet();
//        Node.Property[] props = getProperties();
//        for (int i = 0; i < props.length; i++) {
//            set.put(props[i]);
//        }
//        result.put(set);
//        return result;
//    }
}
