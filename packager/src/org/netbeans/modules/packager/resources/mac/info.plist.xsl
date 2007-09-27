<?xml version="1.0" encoding="UTF-8"?>
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
Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                exclude-result-prefixes="xalan project">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" doctype-system="file://localhost/System/Library/DTDs/PropertyList.dtd" xalan:indent-amount="4"/>
    <xsl:template match="/">
<plist version="0.9">
<xsl:text disable-output-escaping="yes">&lt;!--
  This file tells Mac OS what to call your application and where to find icon
  files, the executable file, etc.  You may edit this file freely.  If edited,
  it will not be regenerated by the NetBeans Packager module; otherwise it may
  be replaced if you install a newer version of the Packager module which 
  contains changes for this file.
  
  Documentation can be found at http://developer.apple.com/technotes/tn/tn2013.html
--&gt;</xsl:text>
<dict>
    <key>CFBundleName</key>
	<string>@APPNAME@</string>
    <key>CFBundleVersion</key>
	<string>@APPVERSION@</string> 
    <key>CFBundleExecutable</key>
	<string>start.sh</string>
    <key>CFBundlePackageType</key>
	<string>APPL</string>
    <key>CFBundleShortVersionString</key>
	<string>@VERSION@</string> 
    <key>CFBundleSignature</key>
	<string>????</string>
    <key>CFBundleInfoDictionaryVersion</key>
	<string>6.0</string>
    <key>CFBundleIconFile</key>
	<string>@ICONFILENAME@</string>
</dict>
</plist>
    </xsl:template>
    
</xsl:stylesheet> 
