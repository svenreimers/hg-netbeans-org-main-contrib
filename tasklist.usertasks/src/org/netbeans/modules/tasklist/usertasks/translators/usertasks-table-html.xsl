<?xml version="1.0" encoding="UTF-8" ?>

<!--
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.


The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s):

The Original Software is NetBeans. The Initial Developer of the Original
Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
-->

<!--
    Description: converts user tasks xml into html
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <title>Task List</title>
                <style type="text/css">
                    th { text-align : center; background-color : #222288; color : white }
                    tr.data { background-color: #eeeeee }
                    td.priority { text-align : center }
                    td.category { text-align : center }
                    td.due { text-align : right }
                    td.created { text-align : center }
                    td.modified { text-align : center }
                </style>
            </head>
            <body style="font-family : sans-serif">
                <h1>Task List</h1>
                <table width="100%" border="0" cellpadding="0" cellspacing="1">
                    <col width="80%"/>
                    <col width="10%"/>
                    <col width="10%"/>
                    <thead>
                        <tr>
                            <th>Summary</th>
                            <th>Priority</th>
                            <th>Progress</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:apply-templates select="tasks"/>
                    </tbody>
                </table>
                <p/>
                <hr/>
                <p><a href="http://validator.w3.org/check/referer">Valid XHTML 1.0!</a> This page was created by the <a href="http://www.netbeans.org">NetBeans</a> 
                        &#160;<a href="http://tasklist.netbeans.org">User Tasks Module</a></p>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="tasks">    
        <xsl:apply-templates select="task"/>
        <xsl:apply-templates select="task" mode="subtasks"/>
    </xsl:template>
    
    <xsl:template match="task" mode="subtasks">
        <xsl:if test="count(task) != 0">
            <tr>
                <td colspan="3" style="text-align : center; background-color : #ccccff">
                    <a name="subtasks_{generate-id()}">
                        <span>
                            Sub-Tasks of: 
                            <a href="#{generate-id()}">
                                <xsl:value-of select="summary"/>
                            </a>
                        </span>
                    </a>
                </td>
            </tr>
            <xsl:apply-templates select="task"/>
        </xsl:if>
        <xsl:apply-templates select="task" mode="subtasks"/>
    </xsl:template>
    
    <xsl:template match="task">
        <tr class="data">
            <td>
                <a name="{generate-id()}">
                    <span title="{details}">
                        <xsl:if test="@progress = 100">
                            <xsl:attribute name="style">
                                text-decoration : line-through
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:value-of select="summary"/>
                        <xsl:text> </xsl:text>
                    </span>
                </a>
                <xsl:if test="details != ''">
                    <pre style="font-family : sans-serif">
                        <xsl:value-of select="details"/>
                    </pre>
                </xsl:if>
                <xsl:if test="count(task) != 0">
                    <div style="text-align : right">
                        <a href="#subtasks_{generate-id()}">
                            Sub-Tasks
                        </a>
                    </div>
                </xsl:if>
            </td>
            <td class="priority">
                <xsl:choose>
                    <xsl:when test="@priority = 'high'">
                        <div style="color : #DD0000">high</div>
                    </xsl:when>
                    <xsl:when test="@priority = 'medium-high'">
                        <div style="color : #FF8000">medium-high</div>
                    </xsl:when>
                    <xsl:when test="@priority = 'medium'">
                        <div style="color : black">medium</div>
                    </xsl:when>
                    <xsl:when test="@priority = 'medium-low'">
                        <div style="color : #00BB00">medium-low</div>
                    </xsl:when>
                    <xsl:when test="@priority = 'low'">
                        <div style="color : #008000">low</div>
                    </xsl:when>
                </xsl:choose>
            </td>
            <td>
                <xsl:call-template name="progress-bar">
                    <xsl:with-param name="progress" select="@progress"/>
                </xsl:call-template>
            </td>
        </tr>
    </xsl:template>
    
    <xsl:template name="format-date">
        <xsl:param name="date"/>
        <span>
            <xsl:attribute name="title">
                <xsl:value-of select="substring-before($date, 'T')"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="substring-after($date, 'T')"/>
            </xsl:attribute>
            <xsl:value-of select="substring-before($date, 'T')"/>
        </span>
    </xsl:template>
    
    <xsl:template name="progress-bar">
        <xsl:param name="progress"/>
        <table cellspacing="0" cellpadding="0" 
            style="width : 100%; height : 10">
            <tbody>
                <tr>
                    <td style="background-color : #222288; color: white; text-align : right; width : {$progress}%">
                        <xsl:if test="$progress &gt; 50">
                            <xsl:value-of select="$progress"/>%
                        </xsl:if>
                    </td>
                    <td style="background-color : #cccccc; color: black; text-align : left; width : {100 - $progress}%">
                        <xsl:if test="$progress &lt;= 50">
                            <xsl:value-of select="$progress"/>%
                        </xsl:if>
                    </td>
                </tr>
            </tbody>
        </table>
    </xsl:template>
</xsl:stylesheet> 
