/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.wsdlextensions.ldap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.naming.NamingException;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.api.project.Project;
import org.netbeans.modules.wsdlextensions.ldap.impl.LDAPTree;
import org.netbeans.modules.wsdlextensions.ldap.impl.ResultSetAttribute;
import org.netbeans.modules.wsdlextensions.ldap.impl.SearchFilterAttribute;
import org.netbeans.modules.wsdlextensions.ldap.impl.UpdateSetAttribute;
import org.netbeans.modules.wsdlextensions.ldap.ldif.GenerateWSDL;
import org.netbeans.modules.wsdlextensions.ldap.ldif.GenerateXSD;
import org.netbeans.modules.wsdlextensions.ldap.ldif.LdifObjectClass;
import org.netbeans.modules.wsdlextensions.ldap.utils.LdapConnection;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

public final class LDAPServerBrowserVisualPanel3 extends JPanel {

    private LDAPTree ldapTree = new LDAPTree();
    private LdapConnection conn;
    private String base = "";
    private String mFunction = "Search";
    private Map mObjectClassesMap = new HashMap();
    private Map mSelectedObjectMap = new HashMap();
    private List mSelectedAttrList = new ArrayList();
    private List mResultSetAttrList = new ArrayList();
    private String mSelectedDN="";
    private String mMainAttrInAdd="";

    /** Creates new form LDAPServerBrowserVisualPanel3 */
    public LDAPServerBrowserVisualPanel3() {
        initComponents();
        initiate();
    }

    @Override
    public String getName() {
        return "Operation setting";
    }

    private void changeCompareOp(String compareOp) {
        JList jListSelected = jListSearchFilterSeleted;
        if (mFunction.equals("Search")) {
            jListSelected = jListSearchFilterSeleted;
        } else if (mFunction.equals("Update")) {
            jListSelected = jListUpdateFilterSeleted;
        }
        Object[] selected = jListSelected.getSelectedValues();
        if (selected.length == 0) {
            JOptionPane.showMessageDialog(null, "Please selecte a item", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (selected.length > 1) {
            JOptionPane.showMessageDialog(null, "Only one item could be selected", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String str = DisplayFormatControl.toAttribute((String) selected[0]);
        int index = str.indexOf(".");
        String obj = str.substring(0, index);
        String att = str.substring(index + 1);
        //update mSelectedAttrList
        for (int j = 0; j < mSelectedAttrList.size(); j++) {
            SearchFilterAttribute sfa = (SearchFilterAttribute) mSelectedAttrList.get(j);
            if (sfa.getObjName().equals(obj) & sfa.getAttributeName().equals(att)) {
                sfa.setCompareOp(compareOp);
            }
            sfa = null;
        }
        redisplaySearchFilter(obj);
        obj = null;
        att = null;
        jListSelected = null;
    }

    private void redisplaySearchFilter(String comboboxItemName) {
        JComboBox jCombobox = null;
        JList jListAvailable = null;
        JList jListSelected = null;
        if (mFunction.equals("Search")) {
            jCombobox = jComboBoxSearchFilter;
            jListAvailable = jListSearchFilterAvailable;
            jListSelected = jListSearchFilterSeleted;
        } else {
            jCombobox = jComboBoxUpdateFilter;
            jListAvailable = jListUpdateFilterAvailable;
            jListSelected = jListUpdateFilterSeleted;
        }
        jCombobox.getModel().setSelectedItem(comboboxItemName);
        List listAvailable = getUnselectedAttribute(comboboxItemName);
        List jListData = DisplayFormatControl.filterListToJList(mSelectedAttrList);

        jListAvailable.removeAll();
        jListAvailable.setListData(new Vector(listAvailable));

        jListSelected.removeAll();
        jListSelected.setListData(new Vector(jListData));

        listAvailable = null;
        jListData = null;
        jCombobox = null;
        jListAvailable = null;
        jListSelected = null;
    }

    private void initiatePopupMenu() {
        JMenuItem jMenuitem1 = new JMenuItem("=");
        JMenuItem jMenuitem3 = new JMenuItem(">=");
        JMenuItem jMenuitem4 = new JMenuItem("<=");
        JMenuItem jMenuitem7 = new JMenuItem("add()");
        JMenuItem jMenuitem8 = new JMenuItem("remove()");

        jMenuitem1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                changeCompareOp("=");
            }
        });

        jMenuitem3.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                changeCompareOp(">=");
            }
        });

        jMenuitem4.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                changeCompareOp("<=");
            }
        });

        jMenuitem7.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addBracket();
            }
        });

        jMenuitem8.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removeBracket();
            }
        });

        jPopupMenu.add(jMenuitem1);
        jPopupMenu.add(jMenuitem3);
        jPopupMenu.add(jMenuitem4);
        jPopupMenu.add(jMenuitem7);
        jPopupMenu.add(jMenuitem8);

        JMenuItem jMenuitemAdd = new JMenuItem("change to Add");
        JMenuItem jMenuitemReplace = new JMenuItem("change to Replace");
        JMenuItem jMenuitemRemove = new JMenuItem("change to Remove");
        JMenuItem jMenuitemRemoveAll = new JMenuItem("change to RemoveAll");

        jMenuitemAdd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                changeUpdateSetOptype("Add");
            }
        });
        jMenuitemReplace.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                changeUpdateSetOptype("Replace");
            }
        });
        jMenuitemRemove.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                changeUpdateSetOptype("Remove");
            }
        });
        jMenuitemRemoveAll.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                changeUpdateSetOptype("RemoveAll");
            }
        });
        jPopupMenu1.add(jMenuitemAdd);
        jPopupMenu1.add(jMenuitemReplace);
        jPopupMenu1.add(jMenuitemRemove);
//        jPopupMenu1.add(jMenuitemRemoveAll);

        JMenuItem jMenuitemSetToMain = new JMenuItem("set to RDN");
        jMenuitemSetToMain.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setToMainAttr();
            }
        });
        jPopupMenu2.add(jMenuitemSetToMain);
    }

    private void initiate() {
        initiatePopupMenu();
        jListSearchFilterSeleted.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && e.getClickCount() == 1) {
                    showPopupMenu(e);
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() && e.getClickCount() == 1) {
                    showPopupMenu(e);
                }
            }
        });

        jListUpdateFilterSeleted.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && e.getClickCount() == 1) {
                    showPopupMenu(e);
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() && e.getClickCount() == 1) {
                    showPopupMenu(e);
                }
            }
        });

        jListUpdateSetSelected.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && e.getClickCount() == 1) {
                    showPopupMenu1(e);
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() && e.getClickCount() == 1) {
                    showPopupMenu1(e);
                }
            }
        });

        jListAddAttributeSelect.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && e.getClickCount() == 1) {
                    showPopupMenu2(e);
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() && e.getClickCount() == 1) {
                    showPopupMenu2(e);
                }
            }
        });


    }

    private void changeUpdateSetOptype(String opType) {
        Object[] selected = jListUpdateSetSelected.getSelectedValues();
        if (selected.length < 1) {
            return;
        }
//        if (selected.length > 1) {
//            JOptionPane.showMessageDialog(null, "Please selecte only one item", "Message", JOptionPane.INFORMATION_MESSAGE);
//            return;
//        }
        for (int k = 0; k < selected.length; k++) {
            String str = DisplayFormatControl.jListUpdateSettoAttr((String) selected[k]);
            int index = str.indexOf(".");
            String objName = str.substring(0, index);
            String attName = str.substring(index + 1);
            LdifObjectClass obj = (LdifObjectClass) mSelectedObjectMap.get(objName);
            if (obj != null) {
                List results = obj.getResultSet();
                if (results != null) {
                    for (int j = 0; j < results.size(); j++) {
                        UpdateSetAttribute usa = (UpdateSetAttribute) results.get(j);
                        if (usa.getAttrName().equals(attName)) {
                            usa.setOpType(opType);
                        }
                    }
                }
                results = null;
            }
        }
        refreshUpdateSetSelectList();
    }

    /*
     * add Bracket to the selected Items in JList;
     * */
    private void addBracket() {
        JList jListSelected = jListSearchFilterSeleted;
        if (mFunction.equals("Search")) {
            jListSelected = jListSearchFilterSeleted;
        } else if (mFunction.equals("Update")) {
            jListSelected = jListUpdateFilterSeleted;
        }
        Object[] selected = jListSelected.getSelectedValues();
        if (selected.length < 2) {
            JOptionPane.showMessageDialog(null, "Please selecte more than one item!", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int beginIndex = jListSelected.getMinSelectionIndex();
        int endIndex = jListSelected.getMaxSelectionIndex();
        SearchFilterAttribute sfaBegin = (SearchFilterAttribute) mSelectedAttrList.get(beginIndex);
        SearchFilterAttribute sfaEnd = (SearchFilterAttribute) mSelectedAttrList.get(endIndex);

        if (selected.length != endIndex - beginIndex + 1) {
            JOptionPane.showMessageDialog(null, "Not correct selected!", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (sfaBegin.isBeginBracket() & sfaEnd.isEndBracket()) {
            JOptionPane.showMessageDialog(null, "Bracket already exists!", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        boolean canAddBracket = true;
        int beginBracketDepth = 0;
        int endBracketDepth = 0;
        for (int i = beginIndex; i <= endIndex; i++) {
            SearchFilterAttribute sfa = (SearchFilterAttribute) mSelectedAttrList.get(i);
            beginBracketDepth += sfa.getBracketBeginDepth();
            endBracketDepth += sfa.getBracketEndDepth();
            if (beginBracketDepth < endBracketDepth) {
                canAddBracket = false;
                break;
            }
            sfa = null;
        }
        if (beginBracketDepth != endBracketDepth) {
            canAddBracket = false;
        }

        if (!canAddBracket) {
            JOptionPane.showMessageDialog(null, "Can't add Bracket to selected items!", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        sfaBegin.increaseBracketBeginDepth();
        sfaEnd.increaseBracketEndDepth();
        sfaBegin = null;
        sfaEnd = null;
        String finalObjName = "";
        for (int i = 0; i < selected.length; i++) {
            String str = DisplayFormatControl.toAttribute((String) selected[i]);
            int index = str.indexOf(".");
            String obj = str.substring(0, index);
            String att = str.substring(index + 1);
            finalObjName = obj;
            //increaseBracketDepth from mSelectedAttrList
            for (int j = 0; j < mSelectedAttrList.size(); j++) {
                SearchFilterAttribute sfa = (SearchFilterAttribute) mSelectedAttrList.get(j);
                if (sfa.getObjName().equals(obj) & sfa.getAttributeName().equals(att)) {
                    sfa.increaseBracketDepth();
                }
                sfa = null;
            }
            obj = null;
            att = null;
        }

        jListSelected = null;
        redisplaySearchFilter(finalObjName);
    }

    private void removeBracket() {
        JList jListSelected = jListSearchFilterSeleted;
        if (mFunction.equals("Search")) {
            jListSelected = jListSearchFilterSeleted;
        } else if (mFunction.equals("Update")) {
            jListSelected = jListUpdateFilterSeleted;
        }
        Object[] selected = jListSelected.getSelectedValues();
        if (selected.length < 2) {
            JOptionPane.showMessageDialog(null, "Please selecte more than one item!", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int beginIndex = jListSelected.getMinSelectionIndex();
        int endIndex = jListSelected.getMaxSelectionIndex();
        SearchFilterAttribute sfaBegin = (SearchFilterAttribute) mSelectedAttrList.get(beginIndex);
        SearchFilterAttribute sfaEnd = (SearchFilterAttribute) mSelectedAttrList.get(endIndex);


        if (!sfaBegin.isBeginBracket() | !sfaEnd.isEndBracket()) {
            JOptionPane.showMessageDialog(null, "Selected items have not bracket!", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        sfaBegin.reduceBracketBeginDepth();
        sfaEnd.reduceBracketEndDepth();
        sfaBegin = null;
        sfaEnd = null;

        String finalObjName = "";
        for (int i = 0; i < selected.length; i++) {
            String str = DisplayFormatControl.toAttribute((String) selected[i]);
            int index = str.indexOf(".");
            String obj = str.substring(0, index);
            String att = str.substring(index + 1);
            finalObjName = obj;
            //reduceBracketDepth from mSelectedAttrList
            for (int j = 0; j < mSelectedAttrList.size(); j++) {
                SearchFilterAttribute sfa = (SearchFilterAttribute) mSelectedAttrList.get(j);
                if (sfa.getObjName().equals(obj) & sfa.getAttributeName().equals(att) & sfa.getBracketDepth() > 0) {
                    sfa.reduceBracketDepth();
                }
                sfa = null;
            }
            obj = null;
            att = null;
        }
        jListSelected = null;
        redisplaySearchFilter(finalObjName);
    }

    private void showPopupMenu(MouseEvent e) {
        jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showPopupMenu1(MouseEvent e) {
        jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showPopupMenu2(MouseEvent e) {
        jPopupMenu2.show(e.getComponent(), e.getX(), e.getY());
    }

    public void read(WizardDescriptor wd) {
        conn = (LdapConnection) wd.getProperty("LDAP_CONNECTION");

        base = conn.getDn();
        mSelectedDN=base;
        ldapTree.initiate(conn);
        DefaultTreeModel treeModel = ldapTree.getTreeModel();
        jTreeSearch.setModel(treeModel);
        jTreeSearch.invalidate();
        jTextFieldSearchBaseDN.setText(base);

        jTreeAdd.setModel(treeModel);
        jTreeAdd.invalidate();
        jTextFieldAddBaseDN.setText(base);

        jTreeUpdate.setModel(treeModel);
        jTreeUpdate.invalidate();
        jTextFieldUpdateBaseDN.setText(base);

        jTreeRemove.setModel(treeModel);
        jTreeRemove.invalidate();
        jTextFieldRemoveBaseDN.setText(base);

        treeModel = null;

        List objectList = null;
        try {
            objectList = conn.getObjectNames();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (null != objectList) {
            readObjectClasses(objectList);
        }
        initiateAdd(objectList);
    }

    public void store(WizardDescriptor wd) {
        LdapConnection conn = (LdapConnection) wd.getProperty("LDAP_CONNECTION");
        String fileName = (String) wd.getProperty("FILE_NAME");
        try {
            Project project = (Project) wd.getProperty("project");
            File dir = new File(FileUtil.toFile(project.getProjectDirectory()).getAbsolutePath() + File.separator + "src" + File.separator + "ldapwsdls");
            dir.mkdirs();
            GenerateXSD genXsd = new GenerateXSD(dir, mSelectedObjectMap, mFunction, fileName, mSelectedDN, mMainAttrInAdd);
            GenerateWSDL genWsdl = new GenerateWSDL(dir, mSelectedObjectMap, mFunction, fileName, conn);
            genXsd.generate();
            genWsdl.generate();
            project.getProjectDirectory().refresh();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            this.mObjectClassesMap = null;
        }
    }

    public void readObjectClasses(List list) {
        if (null != list) {
            SortedComboboxModel searchComboboxModel = new SortedComboboxModel();
            SortedComboboxModel updateComboboxModel = new SortedComboboxModel();
            for (int i = 0; i < list.size(); i++) {
                String item = (String) list.get(i);
                searchComboboxModel.addElement(item);
                updateComboboxModel.addElement(item);
                try {
                    mObjectClassesMap.put(item, conn.getObjectClass(item));
                } catch (NamingException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            jComboBoxSearchFilter.setModel(searchComboboxModel);
            jComboBoxResultSet.setModel(searchComboboxModel);
            jComboBoxSearchFilter.setSelectedIndex(0);
            jComboBoxResultSet.setSelectedIndex(0);
            searchComboboxModel = null;

            jComboBoxUpdateFilter.setModel(updateComboboxModel);
            jComboBoxUpdateSet.setModel(updateComboboxModel);
            jComboBoxUpdateFilter.setSelectedIndex(0);
            jComboBoxUpdateSet.setSelectedIndex(0);
            updateComboboxModel = null;
        }
    }

    public List getUnselectedAttribute(String objName) {
        List ret = new ArrayList();
        LdifObjectClass obj = (LdifObjectClass) mObjectClassesMap.get(objName);
        List must = obj.getMust();
        List may = obj.getMay();
        List selected = DisplayFormatControl.filterToAttr(obj.getSelected());
        if (null != must & must.size() > 0) {
            ret.addAll(must);
        }
        if (null != may & may.size() > 0) {
            ret.addAll(may);
        }
        if (null != selected & ret.size() > 0 & ret.size() > 0) {
            ret.removeAll(selected);
        }
        must = null;
        may = null;
        selected = null;
        return ret;
    }

    public void filterAdd(String logicOp) {
        JComboBox jCombobox = null;
        JList jListAvailable = null;
        JList jListSelected = null;
        if (mFunction.equals("Search")) {
            jCombobox = jComboBoxSearchFilter;
            jListAvailable = jListSearchFilterAvailable;
            jListSelected = jListSearchFilterSeleted;
        } else {
            jCombobox = jComboBoxUpdateFilter;
            jListAvailable = jListUpdateFilterAvailable;
            jListSelected = jListUpdateFilterSeleted;
        }
        Object[] selected = jListAvailable.getSelectedValues();
        if (selected.length == 0) {
            return;
        }
        String objName = jCombobox.getSelectedItem().toString();
        LdifObjectClass obj;
        if (null == mSelectedObjectMap.get(objName)) {
            mSelectedObjectMap.put(objName, (LdifObjectClass) mObjectClassesMap.get(objName));
//            mObjectClassesMap.remove(objName);
        }
        obj = (LdifObjectClass) mSelectedObjectMap.get(objName);
        int existSelectedLength = jListSelected.getModel().getSize();
        for (int i = 0; i < selected.length; i++) {
            int posIndex = existSelectedLength + i;
            String attrMapName = objName + "." + (String) selected[i];
            SearchFilterAttribute sfa = DisplayFormatControl.attrToFilter((String) selected[i], logicOp, objName, posIndex);
            mSelectedAttrList.add(sfa);
            obj.addSelected(sfa);
            sfa = null;
            attrMapName = null;
        }
        jCombobox = null;
        jListAvailable = null;
        jListSelected = null;
        redisplaySearchFilter(objName);
    }

    public String treePathToDN(TreePath treePath) {
        String str = treePath.toString();
        String ret = "";
        String str1;
        int pathLenth = str.length();
        int baseLenth = base.length();
        if (pathLenth == baseLenth + 2) {
            return base;
        }
        str1 = str.substring(baseLenth + 2, pathLenth - 1);
        String[] pathArray = str1.split(",");
        int i = pathArray.length - 1;
        do {
            ret += pathArray[i] + ",";
            i--;
        } while (i >= 0);
        ret += base;
        ret = ret.replaceAll(" ", "");

        return ret;
    }

    private void unSelectFilter() {
        JList jListSelected = jListSearchFilterSeleted;
        if (mFunction.equals("Search")) {
            jListSelected = jListSearchFilterSeleted;
        } else if (mFunction.equals("Update")) {
            jListSelected = jListUpdateFilterSeleted;
        }
        Object[] selected = jListSelected.getSelectedValues();
        if (selected.length == 0) {
            return;
        }
        String finalObjName = "";
        for (int i = 0; i < selected.length; i++) {
            String str = DisplayFormatControl.toAttribute((String) selected[i]);
            int index = str.indexOf(".");
            String obj = str.substring(0, index);
            String att = str.substring(index + 1);
            finalObjName = obj;
            //remove from mSelectedAttrList
            for (int j = 0; j < mSelectedAttrList.size(); j++) {
                SearchFilterAttribute sfa = (SearchFilterAttribute) mSelectedAttrList.get(j);
                if (sfa.getObjName().equals(obj) & sfa.getAttributeName().equals(att)) {
                    mSelectedAttrList.remove(j);
                }
            }
            //remove from mSelectedObjectMap;
            LdifObjectClass loc = (LdifObjectClass) mSelectedObjectMap.get(obj);
            loc.removeSelected(att);
            if (!loc.isSelected()) {
                mSelectedObjectMap.remove(obj);
            }
            loc = null;
            obj = null;
            att = null;
        }

        //reset posIndex;
        for (int i = 0; i < mSelectedAttrList.size(); i++) {
            SearchFilterAttribute sfa2 = (SearchFilterAttribute) mSelectedAttrList.get(i);
            sfa2.setPositionIndex(i);
        }
        jListSelected = null;
        redisplaySearchFilter(finalObjName);
    }

    private void refreshDisplay() {
        Iterator it = mSelectedObjectMap.values().iterator();
        while (it.hasNext()) {
            LdifObjectClass loc = (LdifObjectClass) it.next();
            loc.clearSelect();
        }
        mSelectedAttrList.clear();
        mSelectedObjectMap.clear();
        mResultSetAttrList.clear();
        JComboBox jCombobox = null;
        JList jListSelected = null;
        JList jListSelectedResult = null;
        if (mFunction.equals("Search")) {
            jCombobox = jComboBoxSearchFilter;
            jListSelected = jListSearchFilterSeleted;
            jListSelectedResult = jListSearchResultSelected;
        } else if (mFunction.equals("Update")) {
            jCombobox = jComboBoxUpdateFilter;
            jListSelected = jListUpdateFilterSeleted;
            jListSelectedResult = jListUpdateSetSelected;
        }
        jListSelected.removeAll();
        jListSelected.setListData(new Vector(new ArrayList()));
        jListSelectedResult.removeAll();
        jListSelectedResult.setListData(new Vector(new ArrayList()));
        if (jCombobox.getItemCount() > 0) {
            jCombobox.setSelectedIndex(0);
        }
    }

    private void refreshUpdateSetSelectList() {
        mSelectedAttrList.clear();
        Iterator it = mSelectedObjectMap.values().iterator();
        while (it.hasNext()) {
            LdifObjectClass loc = (LdifObjectClass) it.next();
            List resultSet = loc.getResultSet();
            if (resultSet != null) {
                Iterator it2 = resultSet.iterator();
                while (it2.hasNext()) {
                    UpdateSetAttribute usa2 = (UpdateSetAttribute) it2.next();
                    mSelectedAttrList.add(new String(usa2.getOpType() + " " + usa2.getObjName() + "." + usa2.getAttrName()));
                }
            }
        }
        jListUpdateSetSelected.removeAll();
        jListUpdateSetSelected.setListData(new Vector(mSelectedAttrList));
    }

//add operation code begin
    private void initiateAdd(List list) {
        jListAddObjcecClassAvailable.removeAll();
        jListAddObjcecClassAvailable.setListData(new Vector(list));
        jListAddObjcecClassAvailable.setSelectedIndex(0);
        jListAddObjectClassSelect.removeAll();
        jListAddObjectClassSelect.setListData(new Vector());
    }

    private void refreshAddTabSelectedAttrsList() {
        mResultSetAttrList.clear();
        Iterator it = mSelectedObjectMap.values().iterator();
        while (it.hasNext()) {
            LdifObjectClass loc = (LdifObjectClass) it.next();
            mResultSetAttrList.addAll(DisplayFormatControl.attrToJListAddSeletectAttr(loc.getName(), loc.getResultSet()));
        }
        jListAddAttributeSelect.removeAll();
        jListAddAttributeSelect.setListData(new Vector(mResultSetAttrList));
    }

    private void refreshAddTabAttributeAvailableList(String objName) {
        LdifObjectClass obj = null;
        List ret = new ArrayList();
        List must;
        List may;
        List selectedAttrs;
        obj = (LdifObjectClass) mObjectClassesMap.get(objName);
        selectedAttrs = obj.getResultSet();
        must = obj.getMust();
        may = obj.getMay();
        if (null != must & must.size() > 0) {
            ret.addAll(must);
        }

        if (null != may & may.size() > 0) {
            ret.addAll(may);
        }
        if (null != selectedAttrs & selectedAttrs.size() > 0) {
            ret.removeAll(selectedAttrs);
        }
        jListAddAttributeAvailable.removeAll();
        jListAddAttributeAvailable.setListData(new Vector(ret));
    }

    private void setToMainAttr() {
        Object[] selected = jListAddAttributeSelect.getSelectedValues();
        if (selected.length != 1) {
            JOptionPane.showMessageDialog(null, "Please selecte an item!", "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String str = (String) selected[0];
        mMainAttrInAdd=str;
//        mSelectedObjectMap.put("MainAttribute", str);
    }

//add operation code end.
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu = new javax.swing.JPopupMenu();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabelSearchBaseDN = new javax.swing.JLabel();
        jTextFieldSearchBaseDN = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jComboBoxSearchFilter = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListSearchFilterAvailable = new javax.swing.JList();
        jButtonSearchFilterSelectAnd = new javax.swing.JButton();
        jButtonSearchFilterSelectOr = new javax.swing.JButton();
        jButtonSearchFilterUnselect = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListSearchFilterSeleted = new javax.swing.JList();
        jPanel7 = new javax.swing.JPanel();
        jComboBoxResultSet = new javax.swing.JComboBox();
        jScrollPane4 = new javax.swing.JScrollPane();
        jListSearchResultAvailable = new javax.swing.JList();
        jButtonResultSetSelect = new javax.swing.JButton();
        jButtonResultSetUnselect = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jListSearchResultSelected = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreeSearch = new javax.swing.JTree();
        jPanel2 = new javax.swing.JPanel();
        jLabelAddBaseDN = new javax.swing.JLabel();
        jTextFieldAddBaseDN = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTreeAdd = new javax.swing.JTree();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jListAddObjcecClassAvailable = new javax.swing.JList();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jListAddAttributeAvailable = new javax.swing.JList();
        jButtonAddAttributeUnselect = new javax.swing.JButton();
        jButtonAddAttributeSelect = new javax.swing.JButton();
        jButtonAddObjectUnselect = new javax.swing.JButton();
        jButtonAddObjectSelect = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jListAddObjectClassSelect = new javax.swing.JList();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jListAddAttributeSelect = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        jLabelUpdateBaseDN = new javax.swing.JLabel();
        jTextFieldUpdateBaseDN = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTreeUpdate = new javax.swing.JTree();
        jPanel11 = new javax.swing.JPanel();
        jComboBoxUpdateFilter = new javax.swing.JComboBox();
        jScrollPane12 = new javax.swing.JScrollPane();
        jListUpdateFilterAvailable = new javax.swing.JList();
        jButtonUpdateFilterSelectAnd = new javax.swing.JButton();
        jButtonUpdateFilterSelectOr = new javax.swing.JButton();
        jButtonUpdateFilterUnselect = new javax.swing.JButton();
        jScrollPane13 = new javax.swing.JScrollPane();
        jListUpdateFilterSeleted = new javax.swing.JList();
        jPanel12 = new javax.swing.JPanel();
        jComboBoxUpdateSet = new javax.swing.JComboBox();
        jScrollPane14 = new javax.swing.JScrollPane();
        jListUpdateSetAvailable = new javax.swing.JList();
        jButtonUpdateSetSelect = new javax.swing.JButton();
        jButtonUpdateSetUnselect = new javax.swing.JButton();
        jScrollPane15 = new javax.swing.JScrollPane();
        jListUpdateSetSelected = new javax.swing.JList();
        jPanel4 = new javax.swing.JPanel();
        jLabelRemoveBaseDN = new javax.swing.JLabel();
        jTextFieldRemoveBaseDN = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jComboBoxRemoveFilter = new javax.swing.JComboBox();
        jScrollPane17 = new javax.swing.JScrollPane();
        jListRemoveFilterAvailable = new javax.swing.JList();
        jButtonRemoveFilterSelectAnd = new javax.swing.JButton();
        jButtonRemoveFilterSelectOr = new javax.swing.JButton();
        jButtonRemoveFilterUnselect = new javax.swing.JButton();
        jScrollPane18 = new javax.swing.JScrollPane();
        jListRemoveFilterSeleted = new javax.swing.JList();
        jScrollPane16 = new javax.swing.JScrollPane();
        jTreeRemove = new javax.swing.JTree();

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                selectedTabIndexChange(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabelSearchBaseDN, "Base DN :"); // NOI18N

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("search filter"));

        jComboBoxSearchFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSearchFilterActionPerformed(evt);
            }
        });
        jComboBoxSearchFilter.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jComboBoxSearchFilterPropertyChange(evt);
            }
        });

        jListSearchFilterAvailable.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListSearchFilterAvailableValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jListSearchFilterAvailable);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonSearchFilterSelectAnd, "and >"); // NOI18N
        jButtonSearchFilterSelectAnd.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonSearchFilterSelectAnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchFilterSelectAndActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonSearchFilterSelectOr, "or  >"); // NOI18N
        jButtonSearchFilterSelectOr.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonSearchFilterSelectOr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchFilterSelectOrActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonSearchFilterUnselect, "  < "); // NOI18N
        jButtonSearchFilterUnselect.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonSearchFilterUnselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchFilterUnselectActionPerformed(evt);
            }
        });

        jListSearchFilterSeleted.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListItemOnSelected(evt);
            }
        });
        jScrollPane3.setViewportView(jListSearchFilterSeleted);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jComboBoxSearchFilter, 0, 107, Short.MAX_VALUE)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jButtonSearchFilterUnselect, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonSearchFilterSelectOr, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonSearchFilterSelectAnd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane3, 0, 0, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6Layout.createSequentialGroup()
                        .add(jComboBoxSearchFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel6Layout.createSequentialGroup()
                                .add(jButtonSearchFilterSelectAnd)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButtonSearchFilterSelectOr)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButtonSearchFilterUnselect))
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("result set"));

        jComboBoxResultSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxResultSetActionPerformed(evt);
            }
        });

        jListSearchResultAvailable.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListSearchResultAvailableValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jListSearchResultAvailable);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonResultSetSelect, " > "); // NOI18N
        jButtonResultSetSelect.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonResultSetSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResultSetSelectActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonResultSetUnselect, " < "); // NOI18N
        jButtonResultSetUnselect.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonResultSetUnselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResultSetUnselectActionPerformed(evt);
            }
        });

        jScrollPane5.setViewportView(jListSearchResultSelected);

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jComboBoxResultSet, 0, 109, Short.MAX_VALUE)
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jButtonResultSetUnselect, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonResultSetSelect, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane5, 0, 0, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel7Layout.createSequentialGroup()
                        .add(jComboBoxResultSet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel7Layout.createSequentialGroup()
                                .add(jButtonResultSetSelect)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButtonResultSetUnselect))
                            .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jTreeSearch.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                searchTreeOnSelected(evt);
            }
        });
        jScrollPane1.setViewportView(jTreeSearch);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabelSearchBaseDN))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jTextFieldSearchBaseDN)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelSearchBaseDN)
                    .add(jTextFieldSearchBaseDN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane1, 0, 0, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Search", jPanel1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabelAddBaseDN, "Base DN :"); // NOI18N

        jTreeAdd.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                addTreeOnSelected(evt);
            }
        });
        jScrollPane6.setViewportView(jTreeAdd);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("ObjectClass"));

        jListAddObjcecClassAvailable.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListAddObjcecClassAvailableValueChanged(evt);
            }
        });
        jScrollPane7.setViewportView(jListAddObjcecClassAvailable);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Attributes"));

        jScrollPane8.setViewportView(jListAddAttributeAvailable);

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 162, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jScrollPane8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jButtonAddAttributeUnselect, "∧"); // NOI18N
        jButtonAddAttributeUnselect.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonAddAttributeUnselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddAttributeUnselectActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonAddAttributeSelect, "∨"); // NOI18N
        jButtonAddAttributeSelect.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonAddAttributeSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddAttributeSelectActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonAddObjectUnselect, "∧"); // NOI18N
        jButtonAddObjectUnselect.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonAddObjectUnselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddObjectUnselectActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonAddObjectSelect, "∨"); // NOI18N
        jButtonAddObjectSelect.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonAddObjectSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddObjectSelectActionPerformed(evt);
            }
        });

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("ObjectClass"));

        jListAddObjectClassSelect.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListAddObjectClassSelectValueChanged(evt);
            }
        });
        jScrollPane9.setViewportView(jListAddObjectClassSelect);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jScrollPane9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Attributes"));

        jScrollPane10.setViewportView(jListAddAttributeSelect);

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 161, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(jScrollPane10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(64, 64, 64)
                        .add(jLabelAddBaseDN))
                    .add(jScrollPane6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jButtonAddObjectSelect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonAddObjectUnselect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(49, 49, 49)
                        .add(jButtonAddAttributeSelect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(38, 38, 38)
                        .add(jButtonAddAttributeUnselect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jTextFieldAddBaseDN, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(10, 10, 10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelAddBaseDN)
                    .add(jTextFieldAddBaseDN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel8, 0, 137, Short.MAX_VALUE)
                            .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jButtonAddObjectSelect)
                            .add(jButtonAddAttributeSelect)
                            .add(jButtonAddAttributeUnselect)
                            .add(jButtonAddObjectUnselect))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(40, 40, 40))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jScrollPane6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 295, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        jTabbedPane1.addTab(" Add ", jPanel2);

        org.openide.awt.Mnemonics.setLocalizedText(jLabelUpdateBaseDN, "Base DN :"); // NOI18N

        jTreeUpdate.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                updateTreeOnSelected(evt);
            }
        });
        jScrollPane11.setViewportView(jTreeUpdate);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Update filter"));

        jComboBoxUpdateFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxUpdateFilterActionPerformed(evt);
            }
        });

        jScrollPane12.setViewportView(jListUpdateFilterAvailable);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonUpdateFilterSelectAnd, "and >"); // NOI18N
        jButtonUpdateFilterSelectAnd.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonUpdateFilterSelectAnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateFilterSelectAndActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonUpdateFilterSelectOr, "or  >"); // NOI18N
        jButtonUpdateFilterSelectOr.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonUpdateFilterSelectOr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateFilterSelectOrActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonUpdateFilterUnselect, "  < "); // NOI18N
        jButtonUpdateFilterUnselect.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonUpdateFilterUnselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateFilterUnselectActionPerformed(evt);
            }
        });

        jListUpdateFilterSeleted.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListUpdateSelectedItemOnSelected(evt);
            }
        });
        jScrollPane13.setViewportView(jListUpdateFilterSeleted);

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jComboBoxUpdateFilter, 0, 111, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane12, 0, 0, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jButtonUpdateFilterUnselect, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonUpdateFilterSelectOr, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonUpdateFilterSelectAnd))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jScrollPane13, 0, 0, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel11Layout.createSequentialGroup()
                        .add(jComboBoxUpdateFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel11Layout.createSequentialGroup()
                                .add(jButtonUpdateFilterSelectAnd)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButtonUpdateFilterSelectOr)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButtonUpdateFilterUnselect))
                            .add(jScrollPane12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Update set"));

        jComboBoxUpdateSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxUpdateSetActionPerformed(evt);
            }
        });

        jScrollPane14.setViewportView(jListUpdateSetAvailable);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonUpdateSetSelect, " > "); // NOI18N
        jButtonUpdateSetSelect.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonUpdateSetSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateSetSelectActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonUpdateSetUnselect, " < "); // NOI18N
        jButtonUpdateSetUnselect.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonUpdateSetUnselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateSetUnselectActionPerformed(evt);
            }
        });

        jScrollPane15.setViewportView(jListUpdateSetSelected);

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane14, 0, 0, Short.MAX_VALUE)
                    .add(jComboBoxUpdateSet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jButtonUpdateSetUnselect, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonUpdateSetSelect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane15, 0, 0, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel12Layout.createSequentialGroup()
                        .add(jComboBoxUpdateSet, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel12Layout.createSequentialGroup()
                                .add(jButtonUpdateSetSelect)
                                .add(10, 10, 10)
                                .add(jButtonUpdateSetUnselect)
                                .add(37, 37, 37))
                            .add(jScrollPane14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabelUpdateBaseDN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(jTextFieldUpdateBaseDN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 312, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(52, 52, 52))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextFieldUpdateBaseDN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabelUpdateBaseDN))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane11, 0, 0, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Update", jPanel3);

        org.openide.awt.Mnemonics.setLocalizedText(jLabelRemoveBaseDN, "Base DN :"); // NOI18N

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("remove filter"));

        jScrollPane17.setViewportView(jListRemoveFilterAvailable);

        org.openide.awt.Mnemonics.setLocalizedText(jButtonRemoveFilterSelectAnd, "and >"); // NOI18N
        jButtonRemoveFilterSelectAnd.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.openide.awt.Mnemonics.setLocalizedText(jButtonRemoveFilterSelectOr, "or  >"); // NOI18N
        jButtonRemoveFilterSelectOr.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.openide.awt.Mnemonics.setLocalizedText(jButtonRemoveFilterUnselect, "  < "); // NOI18N
        jButtonRemoveFilterUnselect.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane18.setViewportView(jListRemoveFilterSeleted);

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel13Layout.createSequentialGroup()
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane17, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                    .add(jComboBoxRemoveFilter, 0, 91, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jButtonRemoveFilterUnselect, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonRemoveFilterSelectOr, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonRemoveFilterSelectAnd))
                .add(6, 6, 6)
                .add(jScrollPane18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane18, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                    .add(jPanel13Layout.createSequentialGroup()
                        .add(jComboBoxRemoveFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane17, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))
                    .add(jPanel13Layout.createSequentialGroup()
                        .add(83, 83, 83)
                        .add(jButtonRemoveFilterSelectAnd)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonRemoveFilterSelectOr)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonRemoveFilterUnselect)))
                .addContainerGap())
        );

        jTreeRemove.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                removeTreeOnSelected(evt);
            }
        });
        jScrollPane16.setViewportView(jTreeRemove);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabelRemoveBaseDN)
                    .add(jScrollPane16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextFieldRemoveBaseDN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 309, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelRemoveBaseDN)
                    .add(jTextFieldRemoveBaseDN, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jScrollPane16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Remove", jPanel4);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 457, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 368, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents
    private void searchTreeOnSelected(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_searchTreeOnSelected
        // TODO add your handling code here:
        TreePath path = evt.getPath();
        jTextFieldSearchBaseDN.setText(treePathToDN(path));
        mSelectedDN=treePathToDN(path);
        path = null;
}//GEN-LAST:event_searchTreeOnSelected

    private void addTreeOnSelected(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_addTreeOnSelected
        // TODO add your handling code here:
        TreePath path = evt.getPath();
        jTextFieldAddBaseDN.setText(treePathToDN(path));
        mSelectedDN=treePathToDN(path);
        path = null;
    }//GEN-LAST:event_addTreeOnSelected

    private void updateTreeOnSelected(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_updateTreeOnSelected
        // TODO add your handling code here:
        TreePath path = evt.getPath();
        jTextFieldUpdateBaseDN.setText(treePathToDN(path));
        mSelectedDN=treePathToDN(path);
        path = null;
    }//GEN-LAST:event_updateTreeOnSelected

    private void removeTreeOnSelected(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_removeTreeOnSelected
        // TODO add your handling code here:
        TreePath path = evt.getPath();
        jTextFieldRemoveBaseDN.setText(treePathToDN(path));
        mSelectedDN=treePathToDN(path);
        path = null;
    }//GEN-LAST:event_removeTreeOnSelected

    private void jButtonSearchFilterSelectAndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchFilterSelectAndActionPerformed
        // TODO add your handling code here:
        filterAdd("And");
    }//GEN-LAST:event_jButtonSearchFilterSelectAndActionPerformed

    private void jComboBoxSearchFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSearchFilterActionPerformed
        // TODO add your handling code here:

        String objName = jComboBoxSearchFilter.getSelectedItem().toString();
        List attributeList = getUnselectedAttribute(objName);
        jListSearchFilterAvailable.removeAll();
        jListSearchFilterAvailable.setListData(new Vector(attributeList));
//        this.jComboBoxResultSet.setSelectedIndex(jComboBoxSearchFilter.getSelectedIndex());
    }//GEN-LAST:event_jComboBoxSearchFilterActionPerformed

    private void jComboBoxResultSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxResultSetActionPerformed
        // TODO add your handling code here:
        String objName = (String) jComboBoxResultSet.getSelectedItem();
        List ret = new ArrayList();
        if (objName != null && objName.length() > 0) {
            LdifObjectClass obj = (LdifObjectClass) mObjectClassesMap.get(objName);
            List mays = obj.getMay();
            List musts = obj.getMust();
            List selected = DisplayFormatControl.setToAttr(obj.getResultSet());
            if (musts != null) {
//                for (int i = 0; i < musts.size(); i++) {
//                    ret.add("* " + (String) musts.get(i));
//                }
                ret.addAll(musts);
            }
            if (mays != null) {
                ret.addAll(mays);
            }
            if (selected != null) {
                ret.removeAll(selected);
            }
            this.jListSearchResultAvailable.setListData(new Vector(ret));
        }
    }//GEN-LAST:event_jComboBoxResultSetActionPerformed

    private void jButtonResultSetSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResultSetSelectActionPerformed
        // TODO add your handling code here:
        Object[] selected = this.jListSearchResultAvailable.getSelectedValues();
        if (selected.length == 0) {
            return;
        }
        List listCurrentSelected = new ArrayList();
        for (int i = 0; i < selected.length; i++) {
            listCurrentSelected.add(selected[i]);
        }
        String objName = (String) this.jComboBoxResultSet.getSelectedItem();
        if (mSelectedObjectMap.get(objName) == null) {
            mSelectedObjectMap.put(objName, (LdifObjectClass) mObjectClassesMap.get(objName));
        }
        LdifObjectClass obj = (LdifObjectClass) mSelectedObjectMap.get(objName);
        List resultSetAttributes = DisplayFormatControl.attrToSet(objName, listCurrentSelected);
        Iterator it = resultSetAttributes.iterator();
        while (it.hasNext()) {
            ResultSetAttribute attr = (ResultSetAttribute) it.next();
            obj.addResultSet(attr);
        }
        List ret = DisplayFormatControl.setToJlist(resultSetAttributes);
        List listAvailable = new ArrayList();
        for (int i = 0; i < jListSearchResultAvailable.getModel().getSize(); i++) {
            listAvailable.add(jListSearchResultAvailable.getModel().getElementAt(i));
        }
        listAvailable.removeAll(listCurrentSelected);
        mResultSetAttrList.addAll(ret);

        jListSearchResultAvailable.removeAll();
        jListSearchResultAvailable.setListData(new Vector(listAvailable));

        jListSearchResultSelected.removeAll();
        jListSearchResultSelected.setListData(new Vector(mResultSetAttrList)); 
    }//GEN-LAST:event_jButtonResultSetSelectActionPerformed

    private void jButtonSearchFilterUnselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchFilterUnselectActionPerformed
        // TODO add your handling code here:
        unSelectFilter();               
    }//GEN-LAST:event_jButtonSearchFilterUnselectActionPerformed

    private void jButtonResultSetUnselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResultSetUnselectActionPerformed
        // TODO add your handling code here:
        Object[] selected = this.jListSearchResultSelected.getSelectedValues();
        if (selected.length == 0) {
            return;
        }
        List listCurrentSelected = new ArrayList();
        for (int i = 0; i < selected.length; i++) {
            listCurrentSelected.add(selected[i]);
        }
        // List attrs = new ArrayList();
        String finalObjName = "";
        List resultSetAttributes = DisplayFormatControl.jListToAttr(listCurrentSelected);
        Iterator it = resultSetAttributes.iterator();
        while (it.hasNext()) {
            ResultSetAttribute attr = (ResultSetAttribute) it.next();
            finalObjName = attr.getObjName();
            //String attribute = attr.getAttributeName();
            //attrs.add(attribute);
            LdifObjectClass obj = (LdifObjectClass) mObjectClassesMap.get(finalObjName);
            List resultSet = obj.getResultSet();
            for (int i = 0; i < resultSet.size(); i++) {
                ResultSetAttribute rsa = (ResultSetAttribute) resultSet.get(i);
                if (attr.equals(rsa)) {
                    obj.removeResultSet(rsa);
                }
            }
            if (!obj.isSelected()) {
                mSelectedObjectMap.remove(finalObjName);
            }
        }

        jComboBoxResultSet.getModel().setSelectedItem(finalObjName);
        List ret = new ArrayList();
        if (finalObjName != null && finalObjName.length() > 0) {
            LdifObjectClass fianlObj = (LdifObjectClass) mObjectClassesMap.get(finalObjName);
            List mays = fianlObj.getMay();
            List musts = fianlObj.getMust();
            List selectedattr = DisplayFormatControl.setToAttr(fianlObj.getResultSet());
            if (musts != null) {
//                for (int i = 0; i < musts.size(); i++) {
//                    ret.add("* " + (String) musts.get(i));
//                }
                ret.addAll(musts);
            }
            if (mays != null) {
                ret.addAll(mays);
            }
            if (selectedattr != null) {
                ret.removeAll(selectedattr);
            }
        }
        mResultSetAttrList.removeAll(listCurrentSelected);

        jListSearchResultAvailable.removeAll();
        jListSearchResultAvailable.setListData(new Vector(ret));

        jListSearchResultSelected.removeAll();
        jListSearchResultSelected.setListData(new Vector(mResultSetAttrList));  
    }//GEN-LAST:event_jButtonResultSetUnselectActionPerformed

    private void jButtonSearchFilterSelectOrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchFilterSelectOrActionPerformed
        // TODO add your handling code here:
        filterAdd("Or");
    }//GEN-LAST:event_jButtonSearchFilterSelectOrActionPerformed

    private void jListItemOnSelected(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListItemOnSelected
        // TODO add your handling code here:
        Object[] selected = jListSearchFilterSeleted.getSelectedValues();
        if (selected.length == 0) {
            return;
        }
        int[] lastSelectedIndexes = jListSearchFilterSeleted.getSelectedIndices();
        if (evt.getClickCount() == 1) {
            int currentSelectedIndex = jListSearchFilterSeleted.locationToIndex(evt.getPoint());
            int currentBracketDepth = 0;
            int beginIndex = currentSelectedIndex;
            int endIndex = currentSelectedIndex;

            String str = DisplayFormatControl.toAttribute((String) jListSearchFilterSeleted.getSelectedValue());
            int index = str.indexOf(".");
            String obj = str.substring(0, index);
            String att = str.substring(index + 1);

            int endFlag = 0;
            int beginFlag = 0;

            Iterator it1 = mSelectedAttrList.iterator();
            SearchFilterAttribute currentSelectedAttribute = null;
            while (it1.hasNext()) {
                SearchFilterAttribute sfa = (SearchFilterAttribute) it1.next();
                if (sfa.getObjName().equals(obj) & sfa.getAttributeName().equals(att)) {
                    currentSelectedAttribute = sfa;
                    currentBracketDepth = sfa.getBracketDepth();
//                    if (sfa.getBracketBeginDepth() > 0) {
//                        endFlag = sfa.getBracketBeginDepth();
//                    }
//                    if (sfa.getBracketEndDepth() > 0) {
//                        beginFlag = sfa.getBracketEndDepth();
//                    }
                    break;
                }
                sfa = null;
            }
            selected = null;
            if (currentSelectedAttribute == null) {
                return;
            }

            if (currentBracketDepth > 0) {
                //get the beginIndex and endIndex;      
                if (!(currentSelectedAttribute.getBracketEndDepth() > 0)) {
                    for (int i = currentSelectedIndex + 1; i < mSelectedAttrList.size(); i++) {
                        boolean stopFlag = false;
                        Iterator it = mSelectedAttrList.iterator();
                        while (it.hasNext()) {
                            SearchFilterAttribute sfa = (SearchFilterAttribute) it.next();
                            if (sfa.getPositionIndex() != i) {
                                continue;
                            }
                            if (sfa.getBracketDepth() >= currentBracketDepth) {
                                if (sfa.getBracketEndDepth() <= 0) {

                                    endFlag += sfa.getBracketBeginDepth();
                                } else {
                                    if (endFlag - sfa.getBracketEndDepth() < 0) {
                                        stopFlag = true;
                                    } else {
                                        endFlag -= sfa.getBracketEndDepth();
                                    }
                                }
                                endIndex = i;
                            } else {
                                stopFlag = true;
                            }
                            sfa = null;
                            break;
                        }
                        if (stopFlag) {
                            break;
                        }
                    }
                }
                if (!(currentSelectedAttribute.getBracketBeginDepth() > 0)) {
                    for (int j = currentSelectedIndex - 1; j >= 0; j--) {
                        boolean stopFlag2 = false;
                        Iterator it = mSelectedAttrList.iterator();
                        while (it.hasNext()) {
                            SearchFilterAttribute sfa2 = (SearchFilterAttribute) it.next();
                            if (sfa2.getPositionIndex() != j) {
                                continue;
                            }
                            if (sfa2.getBracketDepth() >= currentBracketDepth) {
                                if (sfa2.getBracketBeginDepth() <= 0) {
//                                beginIndex = j;
                                    beginFlag += sfa2.getBracketEndDepth();
                                } else {
                                    if (beginFlag - sfa2.getBracketBeginDepth() < 0) {
                                        stopFlag2 = true;
                                    } else {

                                        beginFlag -= sfa2.getBracketBeginDepth();
                                    }
                                }
                                beginIndex = j;
                                break;
                            } else {
                                stopFlag2 = true;
                            }
                            sfa2 = null;
                            break;
                        }
                        if (stopFlag2) {
                            break;
                        }
                    }
                }
                int lastLength = lastSelectedIndexes.length;
                int currentLength = endIndex - beginIndex + 1;
                int[] selectedIndexs = new int[lastLength + currentLength];
                int j;
                for (j = 0; j < currentLength; j++) {
                    selectedIndexs[j] = beginIndex + j;
                }
                if (lastLength > 0) {
                    for (j = currentLength; j < lastLength + currentLength; j++) {
                        selectedIndexs[j] = lastSelectedIndexes[j - currentLength];
                    }
                }
                jListSearchFilterSeleted.setSelectedIndices(selectedIndexs);
            }
        }
    }//GEN-LAST:event_jListItemOnSelected

    private void jComboBoxUpdateFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxUpdateFilterActionPerformed
        // TODO add your handling code here:
        String objName = jComboBoxUpdateFilter.getSelectedItem().toString();
        List attributeList = getUnselectedAttribute(objName);
        jListUpdateFilterAvailable.removeAll();
        jListUpdateFilterAvailable.setListData(new Vector(attributeList));
    }//GEN-LAST:event_jComboBoxUpdateFilterActionPerformed

    private void jListSearchFilterAvailableValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListSearchFilterAvailableValueChanged
    // TODO add your handling code here:
    }//GEN-LAST:event_jListSearchFilterAvailableValueChanged

    private void jListSearchResultAvailableValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListSearchResultAvailableValueChanged
    // TODO add your handling code here:
    }//GEN-LAST:event_jListSearchResultAvailableValueChanged

    private void jComboBoxSearchFilterPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jComboBoxSearchFilterPropertyChange
        // TODO add your handling code here:
        this.jListSearchFilterAvailable.requestFocus();
    }//GEN-LAST:event_jComboBoxSearchFilterPropertyChange

    private void selectedTabIndexChange(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_selectedTabIndexChange
        // TODO add your handling code here:
        int index = jTabbedPane1.getSelectedIndex();
        if (index == 0) {
            mFunction = "Search";
            refreshDisplay();
        }
        if (index == 1) {
            mFunction = "Add";
            Iterator it = mSelectedObjectMap.values().iterator();
            while (it.hasNext()) {
                LdifObjectClass loc = (LdifObjectClass) it.next();
                loc.clearSelect();
            }
            mSelectedAttrList.clear();
            mSelectedObjectMap.clear();
            mResultSetAttrList.clear();
//            try {
//                initiateAdd(conn.getObjectNames());
//            } catch (NamingException ex) {
//                Exceptions.printStackTrace(ex);
//            }
        }
        if (index == 2) {
            mFunction = "Update";
            refreshDisplay();
        }
        
    }//GEN-LAST:event_selectedTabIndexChange

    private void jComboBoxUpdateSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxUpdateSetActionPerformed
        // TODO add your handling code here:        
        String objName = (String) jComboBoxUpdateSet.getSelectedItem();
        List ret = new ArrayList();
        if (objName != null && objName.length() > 0) {
            LdifObjectClass obj = (LdifObjectClass) mObjectClassesMap.get(objName);
            List mays = obj.getMay();
            List musts = obj.getMust();
            List selected = DisplayFormatControl.updateSetAttrToAttr(obj.getResultSet());
            if (musts != null) {
//                for (int i = 0; i < musts.size(); i++) {
//                    ret.add("* " + (String) musts.get(i));
//                }
                ret.addAll(musts);
            }
            if (mays != null) {
                ret.addAll(mays);
            }
            if (selected != null) {
                ret.removeAll(selected);
            }
            this.jListUpdateSetAvailable.setListData(new Vector(ret));
        }
    }//GEN-LAST:event_jComboBoxUpdateSetActionPerformed

    private void jButtonUpdateFilterSelectAndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateFilterSelectAndActionPerformed
        // TODO add your handling code here:
        filterAdd("And");
    }//GEN-LAST:event_jButtonUpdateFilterSelectAndActionPerformed

    private void jButtonUpdateFilterSelectOrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateFilterSelectOrActionPerformed
        // TODO add your handling code here:
        filterAdd("Or");
    }//GEN-LAST:event_jButtonUpdateFilterSelectOrActionPerformed

    private void jListUpdateSelectedItemOnSelected(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListUpdateSelectedItemOnSelected
        // TODO add your handling code here:
        Object[] selected = jListUpdateFilterSeleted.getSelectedValues();
        if (selected.length == 0) {
            return;
        }
        int[] lastSelectedIndexes = jListUpdateFilterSeleted.getSelectedIndices();
        if (evt.getClickCount() == 1) {
            int currentSelectedIndex = jListUpdateFilterSeleted.locationToIndex(evt.getPoint());
            int currentBracketDepth = 0;
            int beginIndex = currentSelectedIndex;
            int endIndex = currentSelectedIndex;

            String str = DisplayFormatControl.toAttribute((String) jListUpdateFilterSeleted.getSelectedValue());
            int index = str.indexOf(".");
            String obj = str.substring(0, index);
            String att = str.substring(index + 1);

            int endFlag = 0;
            int beginFlag = 0;

            Iterator it1 = mSelectedAttrList.iterator();
            SearchFilterAttribute currentSelectedAttribute = null;
            while (it1.hasNext()) {
                SearchFilterAttribute sfa = (SearchFilterAttribute) it1.next();
                if (sfa.getObjName().equals(obj) & sfa.getAttributeName().equals(att)) {
                    currentSelectedAttribute = sfa;
                    currentBracketDepth = sfa.getBracketDepth();
                    break;
                }
                sfa = null;
            }
            selected = null;
            if (currentSelectedAttribute == null) {
                return;
            }

            if (currentBracketDepth > 0) {
                //get the beginIndex and endIndex;      
                if (!(currentSelectedAttribute.getBracketEndDepth() > 0)) {
                    for (int i = currentSelectedIndex + 1; i < mSelectedAttrList.size(); i++) {
                        boolean stopFlag = false;
                        Iterator it = mSelectedAttrList.iterator();
                        while (it.hasNext()) {
                            SearchFilterAttribute sfa = (SearchFilterAttribute) it.next();
                            if (sfa.getPositionIndex() != i) {
                                continue;
                            }
                            if (sfa.getBracketDepth() >= currentBracketDepth) {
                                if (sfa.getBracketEndDepth() <= 0) {

                                    endFlag += sfa.getBracketBeginDepth();
                                } else {
                                    if (endFlag - sfa.getBracketEndDepth() < 0) {
                                        stopFlag = true;
                                    } else {
                                        endFlag -= sfa.getBracketEndDepth();
                                    }
                                }
                                endIndex = i;
                            } else {
                                stopFlag = true;
                            }
                            sfa = null;
                            break;
                        }
                        if (stopFlag) {
                            break;
                        }
                    }
                }
                if (!(currentSelectedAttribute.getBracketBeginDepth() > 0)) {
                    for (int j = currentSelectedIndex - 1; j >= 0; j--) {
                        boolean stopFlag2 = false;
                        Iterator it = mSelectedAttrList.iterator();
                        while (it.hasNext()) {
                            SearchFilterAttribute sfa2 = (SearchFilterAttribute) it.next();
                            if (sfa2.getPositionIndex() != j) {
                                continue;
                            }
                            if (sfa2.getBracketDepth() >= currentBracketDepth) {
                                if (sfa2.getBracketBeginDepth() <= 0) {
//                                beginIndex = j;
                                    beginFlag += sfa2.getBracketEndDepth();
                                } else {
                                    if (beginFlag - sfa2.getBracketBeginDepth() < 0) {
                                        stopFlag2 = true;
                                    } else {

                                        beginFlag -= sfa2.getBracketBeginDepth();
                                    }
                                }
                                beginIndex = j;
                                break;
                            } else {
                                stopFlag2 = true;
                            }
                            sfa2 = null;
                            break;
                        }
                        if (stopFlag2) {
                            break;
                        }
                    }
                }
                int lastLength = lastSelectedIndexes.length;
                int currentLength = endIndex - beginIndex + 1;
                int[] selectedIndexs = new int[lastLength + currentLength];
                int j;
                for (j = 0; j < currentLength; j++) {
                    selectedIndexs[j] = beginIndex + j;
                }
                if (lastLength > 0) {
                    for (j = currentLength; j < lastLength + currentLength; j++) {
                        selectedIndexs[j] = lastSelectedIndexes[j - currentLength];
                    }
                }
                jListUpdateFilterSeleted.setSelectedIndices(selectedIndexs);
            }
        }
    }//GEN-LAST:event_jListUpdateSelectedItemOnSelected

    private void jButtonUpdateSetSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateSetSelectActionPerformed
        // TODO add your handling code here:
        Object[] selected = this.jListUpdateSetAvailable.getSelectedValues();
        if (selected.length == 0) {
            return;
        }
        List listCurrentSelected = new ArrayList();
        for (int i = 0; i < selected.length; i++) {
            listCurrentSelected.add(selected[i]);
        }
        String objName = (String) this.jComboBoxUpdateSet.getSelectedItem();
        if (mSelectedObjectMap.get(objName) == null) {
            mSelectedObjectMap.put(objName, (LdifObjectClass) mObjectClassesMap.get(objName));
        }
        LdifObjectClass obj = (LdifObjectClass) mSelectedObjectMap.get(objName);
        List updateSetAttributes = DisplayFormatControl.attrToUpdateSetAttr(objName, listCurrentSelected);
        Iterator it = updateSetAttributes.iterator();
        while (it.hasNext()) {
            UpdateSetAttribute attr = (UpdateSetAttribute) it.next();
            obj.addResultSet(attr);
        }
        List ret = DisplayFormatControl.updateSetToJList(updateSetAttributes);
        List listAvailable = new ArrayList();
        for (int i = 0; i < jListUpdateSetAvailable.getModel().getSize(); i++) {
            listAvailable.add(jListUpdateSetAvailable.getModel().getElementAt(i));
        }
        listAvailable.removeAll(listCurrentSelected);
//        mResultSetAttrList.addAll(ret);

        jListUpdateSetAvailable.removeAll();
        jListUpdateSetAvailable.setListData(new Vector(listAvailable));

        refreshUpdateSetSelectList();
//        jListUpdateSetSelected.removeAll();
//        jListUpdateSetSelected.setListData(new Vector(mResultSetAttrList)); 
    }//GEN-LAST:event_jButtonUpdateSetSelectActionPerformed

    private void jButtonUpdateSetUnselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateSetUnselectActionPerformed
        // TODO add your handling code here:
        Object[] selected = this.jListUpdateSetSelected.getSelectedValues();
        if (selected.length == 0) {
            return;
        }
        List listCurrentSelected = new ArrayList();
        for (int i = 0; i < selected.length; i++) {
            listCurrentSelected.add(selected[i]);
        }
        String finalObjName = "";
        List updateSetAttributes = DisplayFormatControl.jListToUpdateSetAttr(listCurrentSelected);
        Iterator it = updateSetAttributes.iterator();
        while (it.hasNext()) {
            UpdateSetAttribute attr = (UpdateSetAttribute) it.next();
            finalObjName = attr.getObjName();
            LdifObjectClass obj = (LdifObjectClass) mObjectClassesMap.get(finalObjName);
            List resultSet = obj.getResultSet();
            for (int i = 0; i < resultSet.size(); i++) {
                UpdateSetAttribute usa = (UpdateSetAttribute) resultSet.get(i);
                if (attr.equals(usa)) {
                    obj.removeResultSet(usa);
                }
            }
            if (!obj.isSelected()) {
                mSelectedObjectMap.remove(finalObjName);
            }
        }

        jComboBoxUpdateSet.getModel().setSelectedItem(finalObjName);
        List ret = new ArrayList();
        if (finalObjName != null && finalObjName.length() > 0) {
            LdifObjectClass fianlObj = (LdifObjectClass) mObjectClassesMap.get(finalObjName);
            List mays = fianlObj.getMay();
            List musts = fianlObj.getMust();
            List selectedattr = DisplayFormatControl.updateSetAttrToAttr(fianlObj.getResultSet());
            if (musts != null) {
//                for (int i = 0; i < musts.size(); i++) {
//                    ret.add("* " + (String) musts.get(i));
//                }
                ret.addAll(musts);
            }
            if (mays != null) {
                ret.addAll(mays);
            }
            if (selectedattr != null) {
                ret.removeAll(selectedattr);
            }
        }
//        mResultSetAttrList.removeAll(listCurrentSelected);

        jListUpdateSetAvailable.removeAll();
        jListUpdateSetAvailable.setListData(new Vector(ret));

//        jListUpdateSetSelected.removeAll();
//        jListUpdateSetSelected.setListData(new Vector(mResultSetAttrList)); 
        refreshUpdateSetSelectList();
        
    }//GEN-LAST:event_jButtonUpdateSetUnselectActionPerformed

    private void jButtonUpdateFilterUnselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateFilterUnselectActionPerformed
        // TODO add your handling code here:
        unSelectFilter();
    }//GEN-LAST:event_jButtonUpdateFilterUnselectActionPerformed

    private void jButtonAddObjectSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddObjectSelectActionPerformed
        // TODO add your handling code here:
        Object[] selected = jListAddObjcecClassAvailable.getSelectedValues();
        if (selected.length != 1) {
            return;
        }
        String objName = (String) selected[0];
        LdifObjectClass loc = (LdifObjectClass) mObjectClassesMap.get(objName);
        mSelectedObjectMap.put(objName, loc);

        //refresh jListAddObjectClassSelect
        List objAvaiList = new ArrayList();
        List objSelecteList = new ArrayList();
//        List attrAvaiList  = new ArrayList();
        List must = loc.getMust();
        for (int i = 0; i < must.size(); i++) {
            loc.addResultSet((String) must.get(i));
        }

        Iterator it = mSelectedObjectMap.values().iterator();
        while (it.hasNext()) {
            objSelecteList.add(((LdifObjectClass) it.next()).getName());
        }

        try {
            objAvaiList = conn.getObjectNames();
        } catch (NamingException ex) {
            Exceptions.printStackTrace(ex);
        }
        objAvaiList.removeAll(objSelecteList);

        jListAddObjectClassSelect.removeAll();
        jListAddObjectClassSelect.setListData(new Vector(objSelecteList));
        jListAddObjectClassSelect.setSelectedValue(objName, true);

        jListAddObjcecClassAvailable.removeAll();
        jListAddObjcecClassAvailable.setListData(new Vector(objAvaiList));
        
    }//GEN-LAST:event_jButtonAddObjectSelectActionPerformed

    private void jListAddObjcecClassAvailableValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListAddObjcecClassAvailableValueChanged
        // TODO add your handling code here:
        Object[] selected = jListAddObjcecClassAvailable.getSelectedValues();
        if (selected.length != 1) {
            return;
        }
        String objName = (String) selected[0];
        refreshAddTabAttributeAvailableList(objName);
        refreshAddTabSelectedAttrsList();

        jButtonAddAttributeSelect.setEnabled(false);
        jButtonAddAttributeUnselect.setEnabled(false);
    }//GEN-LAST:event_jListAddObjcecClassAvailableValueChanged

    private void jListAddObjectClassSelectValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListAddObjectClassSelectValueChanged
        // TODO add your handling code here:
        Object[] selected = jListAddObjectClassSelect.getSelectedValues();
        if (selected.length != 1) {
            return;
        }
        String objName = (String) selected[0];

        refreshAddTabAttributeAvailableList(objName);
        refreshAddTabSelectedAttrsList();

        jButtonAddAttributeSelect.setEnabled(true);
        jButtonAddAttributeUnselect.setEnabled(true);
    }//GEN-LAST:event_jListAddObjectClassSelectValueChanged

    private void jButtonAddObjectUnselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddObjectUnselectActionPerformed
        // TODO add your handling code here:
        Object[] selected = jListAddObjectClassSelect.getSelectedValues();
        if (selected.length != 1) {
            return;
        }
        String objName = (String) selected[0];
        LdifObjectClass loc = (LdifObjectClass) mSelectedObjectMap.get(objName);
        loc.setResultSet(new ArrayList());
        mSelectedObjectMap.remove(objName);

        List objAvaiList = new ArrayList();
        List objSelecteList = new ArrayList();

        Iterator it = mSelectedObjectMap.values().iterator();
        while (it.hasNext()) {
            objSelecteList.add(((LdifObjectClass) it.next()).getName());
        }

        try {
            objAvaiList = conn.getObjectNames();
        } catch (NamingException ex) {
            Exceptions.printStackTrace(ex);
        }
        objAvaiList.removeAll(objSelecteList);

        jListAddObjectClassSelect.removeAll();
        jListAddObjectClassSelect.setListData(new Vector(objSelecteList));


        jListAddObjcecClassAvailable.removeAll();
        jListAddObjcecClassAvailable.setListData(new Vector(objAvaiList));

        jListAddObjcecClassAvailable.setSelectedValue(objName, true);
        
    }//GEN-LAST:event_jButtonAddObjectUnselectActionPerformed

    private void jButtonAddAttributeSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddAttributeSelectActionPerformed
        // TODO add your handling code here:
        Object[] selected = jListAddAttributeAvailable.getSelectedValues();
        int lenth = selected.length;
        if (lenth < 1) {
            return;
        }
        if (jListAddObjectClassSelect.getSelectedValues().length > 1) {
            return;
        }
        String objName = (String) jListAddObjectClassSelect.getSelectedValue();
        for (int i = 0; i < lenth; i++) {
            String attrName = (String) selected[i];
            LdifObjectClass loc = (LdifObjectClass) mSelectedObjectMap.get(objName);
            loc.addResultSet(attrName);
        }
        refreshAddTabAttributeAvailableList(objName);
        refreshAddTabSelectedAttrsList();
    }//GEN-LAST:event_jButtonAddAttributeSelectActionPerformed

    private void jButtonAddAttributeUnselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddAttributeUnselectActionPerformed
        // TODO add your handling code here:
        Object[] selected = jListAddAttributeSelect.getSelectedValues();
        int lenth = selected.length;
        if (lenth < 1) {
            return;
        }
        if (jListAddObjectClassSelect.getSelectedValues().length > 1) {
            return;
        }
        String objName = "";
        for (int i = 0; i < lenth; i++) {
            String str = (String) selected[i];
            int index = str.indexOf(".");
            objName = str.substring(0, index);
            String attrName = str.substring(index + 1);
            LdifObjectClass loc = (LdifObjectClass) mSelectedObjectMap.get(objName);
            List must = loc.getMust();
            boolean flag = false;
            if (null != must & must.size() > 0) {
                for (int j = 0; j < must.size(); j++) {
                    String attr = (String) must.get(j);
                    if (attrName.equals(attr)) {
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) {
                JOptionPane.showMessageDialog(null, "the attribute " + attrName + " is must in ObjectClass " + objName, "Message", JOptionPane.INFORMATION_MESSAGE);
                continue;
            }
            loc.removeResultSet(attrName);
        }
        refreshAddTabAttributeAvailableList(objName);
        refreshAddTabSelectedAttrsList();
    }//GEN-LAST:event_jButtonAddAttributeUnselectActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddAttributeSelect;
    private javax.swing.JButton jButtonAddAttributeUnselect;
    private javax.swing.JButton jButtonAddObjectSelect;
    private javax.swing.JButton jButtonAddObjectUnselect;
    private javax.swing.JButton jButtonRemoveFilterSelectAnd;
    private javax.swing.JButton jButtonRemoveFilterSelectOr;
    private javax.swing.JButton jButtonRemoveFilterUnselect;
    private javax.swing.JButton jButtonResultSetSelect;
    private javax.swing.JButton jButtonResultSetUnselect;
    private javax.swing.JButton jButtonSearchFilterSelectAnd;
    private javax.swing.JButton jButtonSearchFilterSelectOr;
    private javax.swing.JButton jButtonSearchFilterUnselect;
    private javax.swing.JButton jButtonUpdateFilterSelectAnd;
    private javax.swing.JButton jButtonUpdateFilterSelectOr;
    private javax.swing.JButton jButtonUpdateFilterUnselect;
    private javax.swing.JButton jButtonUpdateSetSelect;
    private javax.swing.JButton jButtonUpdateSetUnselect;
    private javax.swing.JComboBox jComboBoxRemoveFilter;
    private javax.swing.JComboBox jComboBoxResultSet;
    private javax.swing.JComboBox jComboBoxSearchFilter;
    private javax.swing.JComboBox jComboBoxUpdateFilter;
    private javax.swing.JComboBox jComboBoxUpdateSet;
    private javax.swing.JLabel jLabelAddBaseDN;
    private javax.swing.JLabel jLabelRemoveBaseDN;
    private javax.swing.JLabel jLabelSearchBaseDN;
    private javax.swing.JLabel jLabelUpdateBaseDN;
    private javax.swing.JList jListAddAttributeAvailable;
    private javax.swing.JList jListAddAttributeSelect;
    private javax.swing.JList jListAddObjcecClassAvailable;
    private javax.swing.JList jListAddObjectClassSelect;
    private javax.swing.JList jListRemoveFilterAvailable;
    private javax.swing.JList jListRemoveFilterSeleted;
    private javax.swing.JList jListSearchFilterAvailable;
    private javax.swing.JList jListSearchFilterSeleted;
    private javax.swing.JList jListSearchResultAvailable;
    private javax.swing.JList jListSearchResultSelected;
    private javax.swing.JList jListUpdateFilterAvailable;
    private javax.swing.JList jListUpdateFilterSeleted;
    private javax.swing.JList jListUpdateSetAvailable;
    private javax.swing.JList jListUpdateSetSelected;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextFieldAddBaseDN;
    private javax.swing.JTextField jTextFieldRemoveBaseDN;
    private javax.swing.JTextField jTextFieldSearchBaseDN;
    private javax.swing.JTextField jTextFieldUpdateBaseDN;
    private javax.swing.JTree jTreeAdd;
    private javax.swing.JTree jTreeRemove;
    private javax.swing.JTree jTreeSearch;
    private javax.swing.JTree jTreeUpdate;
    // End of variables declaration//GEN-END:variables
}

