/**
 *	This generated bean class DependenciesType matches the schema element 'dependenciesType'.
 *  The root bean class is EjbJar
 *
 *	===============================================================
 *	
 *	                        "dependencies" holds all classloader and dependency
 *	                        information for the module
 *	                    
 *	===============================================================
 * @Generated
 */

package org.netbeans.modules.j2ee.geronimo2.config.gen;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

// BEGIN_NOI18N

public class DependenciesType extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String DEPENDENCY = "Dependency";	// NOI18N
	static public final String DEPENDENCY2 = "Dependency2";	// NOI18N
	static public final String DEPENDENCY3 = "Dependency3";	// NOI18N

	public DependenciesType() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public DependenciesType(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(3);
		this.createProperty("dependency", 	// NOI18N
			DEPENDENCY, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			DependencyType.class);
		this.createProperty("dependency", 	// NOI18N
			DEPENDENCY2, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			DependencyType.class);
		this.createProperty("dependency", 	// NOI18N
			DEPENDENCY3, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			DependencyType.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is an array, possibly empty
	public void setDependency(int index, DependencyType value) {
		this.setValue(DEPENDENCY, index, value);
	}

	//
	public DependencyType getDependency(int index) {
		return (DependencyType)this.getValue(DEPENDENCY, index);
	}

	// Return the number of properties
	public int sizeDependency() {
		return this.size(DEPENDENCY);
	}

	// This attribute is an array, possibly empty
	public void setDependency(DependencyType[] value) {
		this.setValue(DEPENDENCY, value);
	}

	//
	public DependencyType[] getDependency() {
		return (DependencyType[])this.getValues(DEPENDENCY);
	}

	// Add a new element returning its index in the list
	public int addDependency(org.netbeans.modules.j2ee.geronimo2.config.gen.DependencyType value) {
		int positionOfNewItem = this.addValue(DEPENDENCY, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeDependency(org.netbeans.modules.j2ee.geronimo2.config.gen.DependencyType value) {
		return this.removeValue(DEPENDENCY, value);
	}

	// This attribute is an array, possibly empty
	public void setDependency2(int index, DependencyType value) {
		this.setValue(DEPENDENCY2, index, value);
	}

	//
	public DependencyType getDependency2(int index) {
		return (DependencyType)this.getValue(DEPENDENCY2, index);
	}

	// Return the number of properties
	public int sizeDependency2() {
		return this.size(DEPENDENCY2);
	}

	// This attribute is an array, possibly empty
	public void setDependency2(DependencyType[] value) {
		this.setValue(DEPENDENCY2, value);
	}

	//
	public DependencyType[] getDependency2() {
		return (DependencyType[])this.getValues(DEPENDENCY2);
	}

	// Add a new element returning its index in the list
	public int addDependency2(org.netbeans.modules.j2ee.geronimo2.config.gen.DependencyType value) {
		int positionOfNewItem = this.addValue(DEPENDENCY2, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeDependency2(org.netbeans.modules.j2ee.geronimo2.config.gen.DependencyType value) {
		return this.removeValue(DEPENDENCY2, value);
	}

	// This attribute is an array, possibly empty
	public void setDependency3(int index, DependencyType value) {
		this.setValue(DEPENDENCY3, index, value);
	}

	//
	public DependencyType getDependency3(int index) {
		return (DependencyType)this.getValue(DEPENDENCY3, index);
	}

	// Return the number of properties
	public int sizeDependency3() {
		return this.size(DEPENDENCY3);
	}

	// This attribute is an array, possibly empty
	public void setDependency3(DependencyType[] value) {
		this.setValue(DEPENDENCY3, value);
	}

	//
	public DependencyType[] getDependency3() {
		return (DependencyType[])this.getValues(DEPENDENCY3);
	}

	// Add a new element returning its index in the list
	public int addDependency3(org.netbeans.modules.j2ee.geronimo2.config.gen.DependencyType value) {
		int positionOfNewItem = this.addValue(DEPENDENCY3, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeDependency3(org.netbeans.modules.j2ee.geronimo2.config.gen.DependencyType value) {
		return this.removeValue(DEPENDENCY3, value);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public DependencyType newDependencyType() {
		return new DependencyType();
	}

	//
	public static void addComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.add(c);
	}

	//
	public static void removeComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.remove(c);
	}
	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
	}

	// Dump the content of this bean returning it as a String
	public void dump(StringBuffer str, String indent){
		String s;
		Object o;
		org.netbeans.modules.schema2beans.BaseBean n;
		str.append(indent);
		str.append("Dependency["+this.sizeDependency()+"]");	// NOI18N
		for(int i=0; i<this.sizeDependency(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getDependency(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(DEPENDENCY, i, str, indent);
		}

		str.append(indent);
		str.append("Dependency2["+this.sizeDependency2()+"]");	// NOI18N
		for(int i=0; i<this.sizeDependency2(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getDependency2(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(DEPENDENCY2, i, str, indent);
		}

		str.append(indent);
		str.append("Dependency3["+this.sizeDependency3()+"]");	// NOI18N
		for(int i=0; i<this.sizeDependency3(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getDependency3(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(DEPENDENCY3, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("DependenciesType\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N


/*
		The following schema file has been used for generation:

<?xml version="1.0" encoding="UTF-8"?>
<!--

  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.

-->

<xs:schema
    xmlns:openejb="http://geronimo.apache.org/xml/ns/j2ee/ejb/openejb-2.0"
    targetNamespace="http://geronimo.apache.org/xml/ns/j2ee/ejb/openejb-2.0"
    xmlns:naming="http://geronimo.apache.org/xml/ns/naming-1.2"
    xmlns:app="http://geronimo.apache.org/xml/ns/j2ee/application-2.0"
    xmlns:sys="http://geronimo.apache.org/xml/ns/deployment-1.2"
    xmlns:ee="http://java.sun.com/xml/ns/persistence"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    elementFormDefault="qualified"
    attributeFormDefault="unqualified"
    version="1.0">

    <xs:import namespace="http://geronimo.apache.org/xml/ns/naming-1.2" schemaLocation="geronimo-naming-1.2.xsd"/>
    <xs:import namespace="http://geronimo.apache.org/xml/ns/j2ee/application-2.0" schemaLocation="geronimo-application-2.0.xsd"/>
    <xs:import namespace="http://geronimo.apache.org/xml/ns/deployment-1.2" schemaLocation="geronimo-module-1.2.xsd"/>
    <xs:import namespace="http://java.sun.com/xml/ns/persistence" schemaLocation="http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd"/>

    <xs:element name="ejb-jar" type="openejb:geronimo-ejb-jarType"/>

    <xs:complexType name="emptyType"/>

    <xs:complexType name="geronimo-ejb-jarType">
        <xs:sequence>
            <xs:element ref="sys:environment" minOccurs="0"/>

            <xs:element name="openejb-jar" type="openejb:openejb-jarType" minOccurs="0"/>

            <!-- Naming -->
            <xs:group ref="naming:jndiEnvironmentRefsGroup" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element ref="naming:message-destination" minOccurs="0" maxOccurs="unbounded"/>

            <xs:element name="tss-link" type="openejb:tss-linkType" minOccurs="0" maxOccurs="unbounded"/>

            <xs:element name="web-service-binding" type="openejb:web-service-bindingType" minOccurs="0" maxOccurs="unbounded"/>

            <!-- Security -->
            <xs:element ref="app:security" minOccurs="0"/>

            <!-- GBeans -->
            <xs:choice minOccurs="0" maxOccurs="unbounded">
                <xs:element ref="sys:service"/>
                <xs:element ref="ee:persistence"/>
            </xs:choice>
        </xs:sequence>
    </xs:complexType>

    <!-- TODO there is no need for the extra wrapper other then xmlbean is overly enforcing the unique particle attribution rule -->
    <xs:complexType name="openejb-jarType">
        <xs:sequence>
            <xs:any namespace="##other" processContents="lax"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="tss-linkType">
        <xs:sequence>
            <xs:element name="ejb-name" type="xs:string" minOccurs="0"/>
            <xs:element name="tss-name" type="xs:string" minOccurs="0"/>
            <xs:element name="jndi-name" type="xs:string" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="web-service-bindingType">
        <xs:sequence>
            <xs:element name="ejb-name" type="xs:string"/>
            <xs:element name="web-service-address" type="xs:string" minOccurs="0"/>
            <xs:element name="web-service-virtual-host" type="xs:string" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element name="web-service-security" type="openejb:web-service-securityType" minOccurs="0"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="web-service-securityType">
        <xs:sequence>
            <xs:element name="security-realm-name" type="xs:string"/>
            <xs:element name="realm-name" type="xs:string" minOccurs="0"/>
            <xs:element name="transport-guarantee" type="openejb:transport-guaranteeType"/>
            <xs:element name="auth-method" type="openejb:auth-methodType"/>
        </xs:sequence>
    </xs:complexType>

    <xs:simpleType name="transport-guaranteeType">
        <xs:restriction base="xs:string">
            <xs:enumeration value="NONE"/>
            <xs:enumeration value="INTEGRAL"/>
            <xs:enumeration value="CONFIDENTIAL"/>
        </xs:restriction>
    </xs:simpleType>

    <xs:simpleType name="auth-methodType">
        <xs:restriction base="xs:string">
            <xs:enumeration value="BASIC"/>
            <xs:enumeration value="DIGEST"/>
            <xs:enumeration value="CLIENT-CERT"/>
            <xs:enumeration value="NONE"/>
        </xs:restriction>
    </xs:simpleType>

</xs:schema>

*/
