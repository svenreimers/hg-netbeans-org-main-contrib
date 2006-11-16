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
 * The Original Software is Ramon Ramos. The Initial Developer of the Original
 * Software is Ramon Ramos. All rights reserved.
 *
 * Copyright (c) 2006 Ramon Ramos
 */
package ramos.localhistory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.zip.GZIPInputStream;
import org.openide.actions.DeleteAction;

import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.PropertySupport.ReadWrite;

import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;

import java.awt.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import javax.swing.Action;


public class VersionNode
   extends FilterNode implements Comparable{
  private static final String ICON_PATH =
     "ramos/localhistory/resources/clock.png";
  private static final String ANNOTATION = "Annotation";
  private FileObject fileCopy;
  private String htmlDisplayName;
  private String displayName;
  
  public VersionNode(final FileObject fo)
     throws DataObjectNotFoundException {
    super(DataObject.find(fo).getNodeDelegate());
    fileCopy = fo;
    htmlDisplayName = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.MEDIUM).format(fileCopy.lastModified());
    displayName = String.valueOf(fileCopy.lastModified().getTime());
  }
  public String getHtmlDisplayName() {
    return htmlDisplayName;
    //return fileCopy.lastModified().toString();
    
  }
  @Override
  public String getDisplayName() {
    return displayName;
    //return getName();
    // return fileCopy.lastModified().toString();
  }
  
  @Override
  public String getName() {
    //return String.valueOf(fileCopy.lastModified().getTime());
    //return fileCopy.lastModified().toString();
    return htmlDisplayName;
  }
  
  @Override
  public boolean canRename() {
    return false;
  }
  
  @Override
  public Action[] getActions(final boolean context) {
    return new Action[] { SystemAction.get(DeleteAction.class) };
  }
  
  @Override
  public Action getPreferredAction() {
    return null;
  }
  
  
  @Override
  public Node.PropertySet[] getPropertySets() {
    //System.out.println("getPropertySets");
    PropertySet[] retValue = new PropertySet[1];
    final ReadWrite prop =
       new AnnotationProperty(ANNOTATION, String.class, ANNOTATION, null);
    
    retValue[0] =
       new PropertySet() {
      public Node.Property[] getProperties() {
        return new Property[] { prop };
      }
    };
    
    return retValue;
  }
  
  public String getShortDescription() {
    //return "an older version";
    return null;
  }
  
  public Image getIcon(final int type) {
    return Utilities.loadImage(ICON_PATH);
  }
  
  public void delete()
     throws IOException {
    fileCopy.delete();
  }
  
  public Reader getReader()
     throws IOException {
    Reader retValue;
    if (fileCopy.getExt().equals("gz")){
      //System.out.println("is gz");
      BufferedReader br =
         new BufferedReader(
         new InputStreamReader(
         new GZIPInputStream(fileCopy.getInputStream())));
      retValue = br;
    }else{
      //System.out.println("normal old");
      File file = FileUtil.toFile(fileCopy);
      retValue = new BufferedReader(new FileReader(file));
    }
    return retValue;
  }
  
  public void revert(final FileObject current) {
    
    InputStream is = null;
    OutputStream os = null;
    FileLock lock = null;
    try {
      FileObject source = fileCopy;
      
      FileObject target = current;
      lock = target.lock();
      if (source.getExt().equals("gz")){
        is = new GZIPInputStream(source.getInputStream());
      } else {
        is = source.getInputStream();
      }
      os = target.getOutputStream(lock);
      FileUtil.copy(is, os);
      
      String annotation = (String) source.getAttribute(ANNOTATION);
      String newAnnotation = "reverted to " + getName();
      if (annotation != null) newAnnotation += " <" + annotation + ">";
      LocalHistoryRepository.getInstance()
         .makeLocalHistoryCopy(DataObject.find(target),newAnnotation);
    } catch (final FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }finally{
      try {
        if (is != null) is.close();
        if (os != null) os.close();
      } catch (IOException ex) {
      }
      if (lock != null) lock.releaseLock();
    }
  }
  
  public int compareTo(Object o) {
    //System.out.println("compareTo");
    VersionNode vn = (VersionNode)o;
    return fileCopy.lastModified().compareTo(vn.fileCopy.lastModified());
  }
  
  public Object getValue(String attributeName) {
    //System.out.println("getValue "+attributeName);
    Object retValue;
    
    retValue = super.getValue(attributeName);
    return retValue;
  }
  
  /**
   * Delegates to original, if no special lookup provided in constructor,
   * Otherwise it delegates to the lookup. Never override this method
   * if the lookup is provided in constructor.
   *
   * @param type the class to look for
   * @return instance of that class or null if this class of cookie
   *    is not supported
   * @see Node#getCookie
   */
  //  public Node.Cookie getCookie(Class type) {
  //    System.out.println("getCookie "+type);
  //    Cookie retValue = null;
  //    if (type.equals(DataObject.class)){
  //      try {
  //        retValue = DataObject.find(FileUtil.toFileObject(new File((String) fileCopy.getAttribute("path"))));
  //      } catch (DataObjectNotFoundException ex) {
  //        ex.printStackTrace();
  //      }
  //    }else{
  //      retValue = super.getCookie(type);
  //      //System.out.println("super.getCookie "+retValue);
  //    }
  //    return retValue;
  //  }
  
  
  
  
  private class AnnotationProperty extends PropertySupport.ReadWrite{
    AnnotationProperty(String name,Class type,String displayName,
       String shortDescription){
      super(name, type, displayName, shortDescription);
    }
    public Object getValue() throws IllegalAccessException,
       InvocationTargetException {
      //System.out.println("getValue");
      String retValue = "";
      String attr = (String) fileCopy.getAttribute(ANNOTATION);
      
      if (attr != null) {
        retValue = attr;
      }
      
      return retValue;
    }
    
    public void setValue(Object value) throws IllegalAccessException,
       IllegalArgumentException, InvocationTargetException {
      //System.out.println("setValue");
      try {
        fileCopy.setAttribute(ANNOTATION, value);
      } catch (final IOException ex) {
        ex.printStackTrace();
      }
      
    }
    @Override public Object getValue(String attr){
      Object retValue = super.getValue(attr);
      if (attr.equals("htmlDisplayValue"))
        try {
          String converted = (String)getValue();
          converted = converted.replaceAll("<","&lt;");
          converted = converted.replaceAll(">","&gt;");
          retValue =  "<html>"+converted+"</html>";
        } catch (InvocationTargetException ex) {
          ex.printStackTrace();
        } catch (IllegalAccessException ex) {
          ex.printStackTrace();
        }
      return retValue;
    }
    
    
  }
  
}
