/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.scala.editing;

import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;


/**
 *
 * @author Tor Norbye
 */
public class SignatureHtmlFormatter extends HtmlFormatter {
    protected boolean isDeprecated;
    protected boolean isParameter;
    protected boolean isType;
    protected boolean isName;
    protected boolean isEmphasis;

    protected StringBuilder sb = new StringBuilder();

    public SignatureHtmlFormatter() {
    }

    public void reset() {
        textLength = 0;
        sb.setLength(0);
    }

    public void appendHtml(String html) {
        sb.append(html);
        // Not sure what to do about maxLength here... but presumably
    }

    @Override
    public void appendText(String text, int fromInclusive, int toExclusive) {
        for (int i = fromInclusive; i < toExclusive; i++) {
            if (textLength >= maxLength) {
                if (textLength == maxLength) {
                    sb.append("...");
                    textLength += 3;
                }
                break;
            }
            char c = text.charAt(i);

            switch (c) {
            case '<':
                sb.append("&lt;"); // NOI18N

                break;

            case '>': // Only ]]> is dangerous
                if ((i > 1) && (text.charAt(i - 2) == ']') && (text.charAt(i - 1) == ']')) {
                    sb.append("&gt;"); // NOI18N
                } else {
                    sb.append(c);
                }
                break;

            case '&':
                sb.append("&amp;"); // NOI18N

                break;

            default:
                sb.append(c);
            }
            
            textLength++;
        }
    }

    @Override
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

    @Override
    public void active(boolean start) {
        emphasis(start);
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

    @Override
    public String getText() {
        assert !isParameter && !isDeprecated && !isName && !isType;

        return sb.toString();
    }

    public void emphasis(boolean start) {
        assert start != isEmphasis;
        isEmphasis = start;

        if (isEmphasis) {
            sb.append("<b>");
        } else {
            sb.append("</b>");
        }
    }

    @Override
    public String toString() {
        return getText();
    }    
}
