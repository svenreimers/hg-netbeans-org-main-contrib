<?xml version="1.0" encoding="UTF-8"?>
<!--
                Sun Public License Notice

The contents of this file are subject to the Sun Public License
Version 1.0 (the "License"). You may not use this file except in
compliance with the License. A copy of the License is available at
http://www.sun.com/

The Original Code is NetBeans. The Initial Developer of the Original
Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
Microsystems, Inc. All Rights Reserved.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                xmlns:packagerproject="http://www.netbeans.org/ns/packager-project/1"
                exclude-result-prefixes="xalan project packagerproject">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    <xsl:template match="/">
    
        <xsl:variable name="name" select="/project:project/project:configuration/packagerproject:data/packagerproject:name"/>
        <project name="{$name}">
            <xsl:attribute name="default">default</xsl:attribute>
            <xsl:attribute name="basedir">.</xsl:attribute>
            <description>Builds, tests, and runs the project <xsl:value-of select="$name"/>.</description>
            <import file="nbproject/build-impl.xml"/>
        </project>

    </xsl:template>
    
</xsl:stylesheet> 
