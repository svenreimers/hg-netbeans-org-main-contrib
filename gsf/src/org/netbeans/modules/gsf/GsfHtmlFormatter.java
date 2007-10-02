/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.gsf;

import org.netbeans.api.gsf.ElementKind;
import org.netbeans.api.gsf.HtmlFormatter;


/**
 *
 * @author Tor Norbye
 */
public class GsfHtmlFormatter extends HtmlFormatter {
    protected boolean isDeprecated;
    protected boolean isParameter;
    protected boolean isType;
    protected boolean isName;

    protected StringBuilder sb = new StringBuilder();

    public GsfHtmlFormatter() {
    }

    public void reset() {
        sb.setLength(0);
    }

    public void appendHtml(String html) {
        sb.append(html);
    }

    public void appendText(String text) {
        for (int i = 0, n = text.length(); i < n; i++) {
            char c = text.charAt(i);

            switch (c) {
            case '<':
                sb.append("&lt;"); // NOI18N

                break;

            case '>':
                sb.append("&gt;"); // NOI18N

                break;

            case '&':
                sb.append("&amp;"); // NOI18N

                break;

            case '"':
                sb.append("&quot;"); // NOI18N

                break;

            case '\'':
                sb.append("&apos;"); // NOI18N

                break;

            default:
                sb.append(c);
            }
        }
    }

    public void name(ElementKind kind, boolean start) {
        assert start != isName;
        isName = start;

        if (isName) {
            sb.append("<b>");
        } else {
            sb.append("</b>");
        }
    }

    public void parameters(boolean start) {
        assert start != isParameter;
        isParameter = start;

        if (isParameter) {
            sb.append("<font color=\"#808080\">");
        } else {
            sb.append("</font>");
        }
    }

    public void type(boolean start) {
        assert start != isType;
        isType = start;

        if (isType) {
            sb.append("<font color=\"#808080\">");
        } else {
            sb.append("</font>");
        }
    }

    public void deprecated(boolean start) {
        assert start != isDeprecated;
        isDeprecated = start;

        if (isDeprecated) {
            sb.append("<s>");
        } else {
            sb.append("</s>");
        }
    }

    public String getText() {
        assert !isParameter && !isDeprecated && !isName && !isType;

        return sb.toString();
    }
}
