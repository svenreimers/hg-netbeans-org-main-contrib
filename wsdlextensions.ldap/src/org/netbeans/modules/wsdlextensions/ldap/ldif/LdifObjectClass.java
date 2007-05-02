/*
 * LdifSchema.java
 * 
 * Created on Apr 30, 2007, 2:35:57 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.wsdlextensions.ldap.ldif;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gary
 */
public class LdifObjectClass {
    private String mName;
    private String mDescription;
    private String mSuper;
    private List mMust;
    private List mMay;
    
    public LdifObjectClass() {
    }
    
    public void setName(String n) {
        mName = n;
    }
    
    public void setDescription(String d) {
        mDescription = d;
    }
    
    public void setSuper(String s) {
        mSuper = s;
    }
    
    public void addMust(String m) {
        if (mMust == null) {
            mMust = new ArrayList();
        }
        
        mMust.add(m);
    }
    
    public void addMay(String m) {
        if (mMay == null) {
            mMay = new ArrayList();
        }
        
        mMay.add(m);
    }

    public String getName() {
        return mName;
    }
    
    public String getDescription() {
        return mDescription;
    }
    
    public String getSuper() {
        return mSuper;
    }
    
    public List getMust() {
        return mMust;
    }
    
    public List getMay() {
        return mMay;
    }
}