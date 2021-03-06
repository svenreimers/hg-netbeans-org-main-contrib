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
package org.netbeans.modules.ada.editor.parser;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.nodes.Block;
import org.netbeans.modules.ada.editor.ast.nodes.BodyDeclaration.Modifier;
import org.netbeans.modules.ada.editor.ast.nodes.FieldsDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.DefaultVisitor;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.api.SemanticAnalyzer;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;

/**
 * Based on org.netbeans.modules.php.editor.parser.SemanticAnalysis
 *
 * @author Andrea Lucarelli
 */
public class AdaSemanticAnalyzer extends SemanticAnalyzer {

    public static final EnumSet<ColoringAttributes> UNUSED_FIELD_SET = EnumSet.of(ColoringAttributes.UNUSED, ColoringAttributes.FIELD);
    public static final EnumSet<ColoringAttributes> UNUSED_METHOD_SET = EnumSet.of(ColoringAttributes.UNUSED, ColoringAttributes.METHOD);
    private boolean cancelled;
    private Map<OffsetRange, Set<ColoringAttributes>> semanticHighlights;

    public AdaSemanticAnalyzer() {
        semanticHighlights = null;
    }

    public Map<OffsetRange, Set<ColoringAttributes>> getHighlights() {
        return semanticHighlights;
    }

    public void cancel() {
        cancelled = true;
    }

    public void run(ParserResult compilationInfo) throws Exception {
    }

    protected final synchronized boolean isCancelled() {
        return cancelled;
    }

    protected final synchronized void resume() {
        cancelled = false;
    }

    @Override
    public void run(Result r, SchedulerEvent event) {
        resume();

        if (isCancelled()) {
            return;
        }

        AdaParseResult result = (AdaParseResult) r;
        Map<OffsetRange, Set<ColoringAttributes>> highlights =
                new HashMap<OffsetRange, Set<ColoringAttributes>>(100);

        if (result.getProgram() != null) {
            result.getProgram().accept(new SemanticHighlightVisitor(highlights, result.getSnapshot()));

            if (highlights.size() > 0) {
                semanticHighlights = highlights;
            } else {
                semanticHighlights = null;
            }
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    private class SemanticHighlightVisitor extends DefaultVisitor {

        private class IdentifierColoring {

            public Identifier identifier;
            public Set<ColoringAttributes> coloring;

            public IdentifierColoring(Identifier identifier, Set<ColoringAttributes> coloring) {
                this.identifier = identifier;
                this.coloring = coloring;
            }
        }
        Map<OffsetRange, Set<ColoringAttributes>> highlights;
        // for unused private fields: name, varible
        // if isused, then it's deleted from the list and marked as the field
        private final Map<String, IdentifierColoring> privateFieldsUsed;
        // for unsed private method: name, identifier
        private final Map<String, IdentifierColoring> privateMethod;
        // this is holder of blocks, which has to be scanned for usages in the class.
        private List<Block> needToScan = new ArrayList<Block>();
        private final Snapshot snapshot;

        public SemanticHighlightVisitor(Map<OffsetRange, Set<ColoringAttributes>> highlights, Snapshot snapshot) {
            this.highlights = highlights;
            privateFieldsUsed = new HashMap<String, IdentifierColoring>();
            privateMethod = new HashMap<String, IdentifierColoring>();
            this.snapshot = snapshot;
        }

        private void addOffsetRange(ASTNode node, Set<ColoringAttributes> coloring) {
            int start = snapshot.getOriginalOffset(node.getStartOffset());
            if (start > -1) {
                int end = start + node.getEndOffset() - node.getStartOffset();
                highlights.put(new OffsetRange(start, end), coloring);
            }
        }

        @Override
        public void visit(PackageSpecification node) {
            if (isCancelled()) {
                return;
            }
            Identifier name = node.getName();
            addOffsetRange(name, ColoringAttributes.CLASS_SET);
            // Check if package name end is present
            if (node.getNameEnd().getName() != null) {
                Identifier nameEnd = node.getNameEnd();
                addOffsetRange(nameEnd, ColoringAttributes.CLASS_SET);
            }
            needToScan = new ArrayList<Block>();
            if (node.getBody() != null) {
                node.getBody().accept(this);

                // find all usages in the method bodies
                for (Block block : needToScan) {
                    block.accept(this);
                }
                // are there unused private fields?
                for (IdentifierColoring item : privateFieldsUsed.values()) {
                    addOffsetRange(item.identifier, UNUSED_FIELD_SET);
                }

                // are there unused private methods?
                for (IdentifierColoring item : privateMethod.values()) {
                    addOffsetRange(item.identifier, UNUSED_METHOD_SET);
                }
            }
        }

        @Override
        public void visit(MethodDeclaration method) {
            boolean isPrivate = Modifier.isPrivate(method.getModifier());
            EnumSet<ColoringAttributes> coloring = ColoringAttributes.METHOD_SET;

            Identifier identifier = method.getSubrogramName();
            addOffsetRange(identifier, coloring);
            if (!method.isSpefication()) {
                Identifier nameEnd = method.getSubrogramNameEnd();
                addOffsetRange(nameEnd, coloring);
            }

            if (method.getSubprogramBody() != null) {
                // don't scan the body now. It should be scanned after all declarations
                // are known
                Block declarations = method.getSubprogramBody().getDeclarations();
                if (declarations != null) {
                    needToScan.add(declarations);
                }
                Block body = method.getSubprogramBody().getBody();
                if (body != null) {
                    needToScan.add(body);
                }
            }
        }

        @Override
        public void visit(PackageBody node) {
            if (isCancelled()) {
                return;
            }
            Identifier name = node.getName();
            addOffsetRange(name, ColoringAttributes.CLASS_SET);
            // Check if package name end is present
            if (node.getNameEnd().getName() != null) {
                Identifier nameEnd = node.getNameEnd();
                addOffsetRange(nameEnd, ColoringAttributes.CLASS_SET);
            }
            needToScan = new ArrayList<Block>();
            if (node.getBody() != null) {
                node.getBody().accept(this);

                // find all usages in the method bodies
                for (Block block : needToScan) {
                    block.accept(this);
                }
                // are there unused private fields?
                for (IdentifierColoring item : privateFieldsUsed.values()) {
                    addOffsetRange(item.identifier, UNUSED_FIELD_SET);
                }

                // are there unused private methods?
                for (IdentifierColoring item : privateMethod.values()) {
                    addOffsetRange(item.identifier, UNUSED_METHOD_SET);
                }
            }
        }

        @Override
        public void visit(FieldsDeclaration node) {
            if (isCancelled()) {
                return;
            }

            boolean isPrivate = Modifier.isPrivate(node.getModifier());
            EnumSet<ColoringAttributes> coloring = ColoringAttributes.FIELD_SET;

            Variable[] variables = node.getVariableNames();
            for (int i = 0; i < variables.length; i++) {
                Variable variable = variables[i];
                if (!isPrivate) {
                    addOffsetRange(variable.getName(), ColoringAttributes.FIELD_SET);
                } else {
                    if (variable.getName() instanceof Identifier) {
                        Identifier identifier = (Identifier) variable.getName();
                        privateFieldsUsed.put(identifier.getName(), new IdentifierColoring(identifier, coloring));
                    }
                }
            }
        }

        @Override
        public void visit(TypeDeclaration node) {
            if (isCancelled()) {
                return;
            }

            boolean isPrivate = Modifier.isPrivate(node.getModifier());
            EnumSet<ColoringAttributes> coloring = ColoringAttributes.FIELD_SET;

            Identifier id = node.getTypeName();
            if (!isPrivate) {
                addOffsetRange(id, ColoringAttributes.FIELD_SET);
            } else {
                privateFieldsUsed.put(id.getName(), new IdentifierColoring(id, coloring));
            }
        }
    }
}
