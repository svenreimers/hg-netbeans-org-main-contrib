/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.idl.src;

public interface IDLParserTreeConstants
{
  public int JJTIDLELEMENT = 0;
  public int JJTVOID = 1;
  public int JJTMODULEELEMENT = 2;
  public int JJTINTERFACEELEMENT = 3;
  public int JJTINTERFACEFORWARDELEMENT = 4;
  public int JJTINTERFACEHEADERELEMENT = 5;
  public int JJTVALUEFORWARDELEMENT = 6;
  public int JJTVALUEBOXELEMENT = 7;
  public int JJTVALUEABSELEMENT = 8;
  public int JJTVALUEELEMENT = 9;
  public int JJTVALUEHEADERELEMENT = 10;
  public int JJTVALUEINHERITANCESPECELEMENT = 11;
  public int JJTSTATEMEMBERELEMENT = 12;
  public int JJTINITDCLELEMENT = 13;
  public int JJTINITPARAMDECLELEMENT = 14;
  public int JJTCONSTELEMENT = 15;
  public int JJTTYPEELEMENT = 16;
  public int JJTSIMPLEDECLARATOR = 17;
  public int JJTSTRUCTTYPEELEMENT = 18;
  public int JJTMEMBERELEMENT = 19;
  public int JJTUNIONTYPEELEMENT = 20;
  public int JJTUNIONMEMBERELEMENT = 21;
  public int JJTENUMTYPEELEMENT = 22;
  public int JJTARRAYDECLARATOR = 23;
  public int JJTATTRIBUTEELEMENT = 24;
  public int JJTEXCEPTIONELEMENT = 25;
  public int JJTOPERATIONELEMENT = 26;
  public int JJTPARAMETERELEMENT = 27;
  public int JJTIDENTIFIER = 28;


  public String[] jjtNodeName = {
    "IDLElement",
    "void",
    "ModuleElement",
    "InterfaceElement",
    "InterfaceForwardElement",
    "InterfaceHeaderElement",
    "ValueForwardElement",
    "ValueBoxElement",
    "ValueAbsElement",
    "ValueElement",
    "ValueHeaderElement",
    "ValueInheritanceSpecElement",
    "StateMemberElement",
    "InitDclElement",
    "InitParamDeclElement",
    "ConstElement",
    "TypeElement",
    "SimpleDeclarator",
    "StructTypeElement",
    "MemberElement",
    "UnionTypeElement",
    "UnionMemberElement",
    "EnumTypeElement",
    "ArrayDeclarator",
    "AttributeElement",
    "ExceptionElement",
    "OperationElement",
    "ParameterElement",
    "Identifier",
  };
}
