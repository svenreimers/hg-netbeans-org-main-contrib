<?xml version="1.0" encoding="UTF-8"?>
<!--
                Sun Public License Notice

The contents of this file are subject to the Sun Public License
Version 1.0 (the "License"). You may not use this file except in
compliance with the License. A copy of the License is available at
http://www.sun.com/

The Original Code is NetBeans. The Initial Developer of the Original
Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
Microsystems, Inc. All Rights Reserved.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:p="http://www.netbeans.org/ns/project/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                xmlns:projdeps="http://www.netbeans.org/ns/ant-project-references/1"
                xmlns:packagerproject="http://www.netbeans.org/ns/packager-project/1"
                exclude-result-prefixes="xalan p projdeps packagerproject">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    <xsl:template match="/">

        <xsl:variable name="name" select="/p:project/p:configuration/packagerproject:data/packagerproject:name"/>
        <project name="{$name}-impl">
            <xsl:attribute name="default">build</xsl:attribute>
            <xsl:attribute name="basedir">..</xsl:attribute>

            <target name="pre-init">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="init-private">
                <xsl:attribute name="depends">pre-init</xsl:attribute>
                <property file="nbproject/private/private.properties"/>
            </target>

            <target name="init-user">
                <xsl:attribute name="depends">pre-init,init-private</xsl:attribute>
                <property file="${{user.properties.file}}"/>
            </target>

            <target name="init-project">
                <xsl:attribute name="depends">pre-init,init-private,init-user</xsl:attribute>
                <property file="nbproject/project.properties"/>
            </target>

            <target name="do-init"/>

            <target name="post-init">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="init-check">
                <xsl:attribute name="depends">pre-init,init-private,init-user,init-project,do-init</xsl:attribute>
            </target>


            <target name="init">
                <xsl:attribute name="depends">pre-init,init-private,init-user,init-project,do-init,post-init,init-check</xsl:attribute>
            </target>
            
            <xsl:call-template name="deps.target">
                <xsl:with-param name="targetname" select="'deps-build'"/>
                <xsl:with-param name="type" select="'jar'"/>
                <xsl:with-param name="copyfiles" select="'true'"/>
            </xsl:call-template>            

            <target name="build-mac" if="platform.mac">
                <mkdir dir="dist/Macintosh/{$name}.app/Contents/MacOS"/>
                <copy file="Configurations/Macintosh/start.sh" todir="dist/Macintosh/{$name}.app/Contents/MacOS">
                    <filterset>
                        <filter token="MAIN-CLASS" value="${{main.class}}"/>
                        <filter token="APPNAME" value="{$name}"/>
                    </filterset>
                </copy>
                <chmod file="dist/Macintosh/{$name}.app/Contents/MacOS/start.sh" perm="ugo+rx"/>
                <copy file="Configurations/Macintosh/Info.plist" todir="dist/Macintosh/{$name}.app/Contents">
                    <filterset>
                        <filter token="APPNAME" value="{$name}"/>
                        <filter token="VERSION" value="${{mac.appversion}}"/>
                        <filter token="APPVERSION" value="${{mac.versionstring}}"/>
                        <filter token="ICONFILENAME" value="{$name}.icns"/>
                    </filterset>
                </copy>
                <copy file="Configurations/Macintosh/{$name}.icns" tofile="dist/Macintosh/{$name}.app/Contents/Resources/{$name}.icns" failonerror="false"/>
            </target>
            
            <target name="build-windows" if="platform.windows">
                <echo message="Windows build not implemented yet"/>
                <!-- XXX maybe try http://launch4j.sourceforge.net/ -->
            </target>
            
            <target name="build-jnlp" if="platform.webstart">
                <!--XXX MAKE A LIST OF JARS IN THE RESOURCES SECTION -->
                <!--XXX SIGN JARS IF SECURITY ACCESS REQUESTED -->
                <copy file="Configurations/WebStart/app.jnlp" tofile="dist/WebStart/{$name}.jnlp">
                    <filterset>
                        <filter token="NAME" value="{$name}"/>
                        <filter token="VENDOR" value="${{jnlp.vendor}}"/>
                        <!--<filter token="CODEBASE" value="${{jnlp.codebase}}"/> -->
                        <filter token="CODEBASE" value="file://${{basedir}}/dist/WebStart/"/> <!--XXX for testing-->
                        <filter token="DESCRIPTION" value="${{jnlp.description}}"/>
                        <filter token="LONG-DESCRIPTION" value="${{jnlp.longdescription}}"/>
                        <filter token="HOMEPAGE" value="${{jnlp.homepage}}"/>
                        <filter token="JAR" value="${{main.class.jar}}"/> <!--XXX-->
                        <filter token="MAIN-CLASS" value="${{main.class}}"/>
                    </filterset>
                </copy>
            </target>
            
            <target name="build-unix" if="platform.unix">
                <echo message="Unix build not implemented yet"/>
            </target>
            
            <target name="build" depends="init,deps-build,build-mac,build-jnlp,build-windows,build-unix"/>
            
            <target name="run" depends="build">
                <exec executable="open">
                    <arg file="dist/Macintosh"/>
                </exec>
                <exec executable="open">
                    <arg file="dist/Macintosh/{$name}.app"/>
                </exec>
            </target>

            <xsl:comment>
    ===============
    CLEANUP SECTION
    ===============
    </xsl:comment>

            <xsl:call-template name="deps.target">
                <xsl:with-param name="targetname" select="'deps-clean'"/>
                <xsl:with-param name="copyfiles" select="'false'"/>
            </xsl:call-template>
            
            <target name="do-clean">
                <xsl:attribute name="depends">init</xsl:attribute>
                <delete dir="dist"/>
            </target>

            <target name="post-clean">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="clean">
                <xsl:attribute name="depends">init,deps-clean,do-clean,post-clean</xsl:attribute>
                <xsl:attribute name="description">Clean build products.</xsl:attribute>
            </target>

        </project>

    </xsl:template>

    <!---
    Generic template to build subdependencies of a certain type.
    Feel free to copy into other modules.
    @param targetname required name of target to generate
    @param type artifact-type from project.xml to filter on; optional, if not specified, uses
                all references, and looks for clean targets rather than build targets
    @return an Ant target which builds (or cleans) all known subprojects
    -->
    <xsl:template name="deps.target">
        <xsl:param name="targetname"/>
        <xsl:param name="type"/>
        <xsl:param name="copyfiles"/>
        
        <xsl:variable name="name" select="/p:project/p:configuration/packagerproject:data/packagerproject:name"/>
        <target name="{$targetname}">
            <xsl:attribute name="depends">init</xsl:attribute>
            <xsl:attribute name="unless">${no.dependencies}</xsl:attribute>
            <xsl:variable name="references" select="/p:project/p:configuration/projdeps:references"/>
            <xsl:for-each select="$references/projdeps:reference[not($type) or projdeps:artifact-type = $type]">
                <xsl:variable name="subproj" select="projdeps:foreign-project"/>
                <xsl:variable name="subtarget">
                    <xsl:choose>
                        <xsl:when test="$type">
                            <xsl:value-of select="projdeps:target"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="projdeps:clean-target"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="script" select="projdeps:script"/>
                <ant target="{$subtarget}" inheritall="false" antfile="${{project.{$subproj}}}/{$script}"/>
                <xsl:if test="$copyfiles='true'">
                    <!--XXX test for is.mac here & also copy to jnlp dir -->
                    <mkdir dir="dist/Macintosh/{$projname}.app/Contents/Resources"/>
                    <copy todir="dist/Macintosh/{$projname}.app/Contents/Resources" file="${{reference.{$subproj}.jar}}"/>

                    <!--XXX don't always do this, and get it to match the URL in the jnlp file -->
                    <mkdir dir="dist/WebStart/dist"/>
                    <copy todir="dist/WebStart/dist" file="${{reference.{$subproj}.jar}}"/>
                </xsl:if>
            </xsl:for-each>
        </target>
    </xsl:template>

</xsl:stylesheet>
