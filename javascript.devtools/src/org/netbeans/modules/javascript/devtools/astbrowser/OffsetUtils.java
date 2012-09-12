/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development and
 * Distribution License("CDDL") (collectively, the "License"). You may not use
 * this file except in compliance with the License. You can obtain a copy of
 * the License at http://www.netbeans.org/cddl-gplv2.html or
 * nbbuild/licenses/CDDL-GPL-2-CP. See the License for the specific language
 * governing permissions and limitations under the License. When distributing
 * the software, include this License Header Notice in each file and include
 * the License file at nbbuild/licenses/CDDL-GPL-2-CP. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided by
 * Oracle in the GPL Version 2 section of the License file that accompanied
 * this code. If applicable, add the following below the License Header, with
 * the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license." If you do not indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to its
 * licensees as provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the option applies only
 * if the new code is made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.javascript.devtools.astbrowser;

import com.oracle.nashorn.ir.AccessNode;
import com.oracle.nashorn.ir.BinaryNode;
import com.oracle.nashorn.ir.Block;
import com.oracle.nashorn.ir.BreakNode;
import com.oracle.nashorn.ir.CallNode;
import com.oracle.nashorn.ir.CaseNode;
import com.oracle.nashorn.ir.CatchNode;
import com.oracle.nashorn.ir.ContinueNode;
import com.oracle.nashorn.ir.ExecuteNode;
import com.oracle.nashorn.ir.ForNode;
import com.oracle.nashorn.ir.FunctionNode;
import com.oracle.nashorn.ir.IdentNode;
import com.oracle.nashorn.ir.IfNode;
import com.oracle.nashorn.ir.IndexNode;
import com.oracle.nashorn.ir.LabelNode;
import com.oracle.nashorn.ir.LineNumberNode;
import com.oracle.nashorn.ir.LiteralNode;
import com.oracle.nashorn.ir.Node;
import com.oracle.nashorn.ir.ObjectNode;
import com.oracle.nashorn.ir.PropertyNode;
import com.oracle.nashorn.ir.ReferenceNode;
import com.oracle.nashorn.ir.ReturnNode;
import com.oracle.nashorn.ir.RuntimeNode;
import com.oracle.nashorn.ir.SwitchNode;
import com.oracle.nashorn.ir.TernaryNode;
import com.oracle.nashorn.ir.ThrowNode;
import com.oracle.nashorn.ir.TryNode;
import com.oracle.nashorn.ir.UnaryNode;
import com.oracle.nashorn.ir.VarNode;
import com.oracle.nashorn.ir.WhileNode;
import com.oracle.nashorn.ir.WithNode;
import com.oracle.nashorn.parser.Token;

/**
 *
 * @author petr
 */
public class OffsetUtils {
    
    public static int[] getOffsets (Node node) {
       long start = System.currentTimeMillis();
        OffsetFinder visitor = new OffsetFinder();
        node.accept(visitor);
        long end = System.currentTimeMillis();
        if ((end - start) > 0)
            System.out.println("findOffsets for : " + node.getClass().getSimpleName() + " : " + (end - start));
        return new int[]{visitor.minOffset, visitor.maxOffset};
    }
    
    private static class OffsetFinder extends com.oracle.nashorn.ir.visitor.NodeVisitor {
        protected int minOffset = Integer.MAX_VALUE;
        protected int maxOffset = -1;
        
        private Node findOffsets(Node node) {
            int min = node.position();
            int max = min + node.length();
            if (minOffset > min) {
                minOffset = min;
            }
            if (maxOffset < max) {
                maxOffset = max;
            }
            return node;
            
        }
        
        @Override
        public Node enter(AccessNode accessNode) {
            return findOffsets(accessNode);
        }

        @Override
        public Node enter(BinaryNode binaryNode) {
            return findOffsets(binaryNode);
        }

        @Override
        public Node enter(Block block) {
            return findOffsets(block);
        }

        @Override
        public Node enter(BreakNode breakNode) {
            return findOffsets(breakNode);
        }

        @Override
        public Node enter(CallNode callNode) {
            return findOffsets(callNode);
        }

        @Override
        public Node enter(CaseNode caseNode) {
            return findOffsets(caseNode);
        }

        @Override
        public Node enter(CatchNode catchNode) {
            return findOffsets(catchNode);
        }

        @Override
        public Node enter(ContinueNode continueNode) {
            return findOffsets(continueNode);
        }

        @Override
        public Node enter(ExecuteNode executeNode) {
            return findOffsets(executeNode);
        }

        @Override
        public Node enter(ForNode forNode) {
            return findOffsets(forNode);
        }

        @Override
        public Node enter(FunctionNode functionNode) {
            int min = Token.descPosition(functionNode.getFirstToken());
            int max = Token.descPosition(functionNode.getLastToken()) + Token.descLength(functionNode.getLastToken());
            if (minOffset > min) {
                minOffset = min;
            }
            if (maxOffset < max) {
                maxOffset = max;
            } 
            return findOffsets(functionNode);
        }

        @Override
        public Node enter(IdentNode identNode) {
            return findOffsets(identNode);
        }

        @Override
        public Node enter(IfNode ifNode) {
            return findOffsets(ifNode);
        }

        @Override
        public Node enter(IndexNode indexNode) {
            return findOffsets(indexNode);
        }

        @Override
        public Node enter(LabelNode labeledNode) {
            return findOffsets(labeledNode);
        }

        @Override
        public Node enter(LineNumberNode lineNumberNode) {
            return findOffsets(lineNumberNode);
        }

        @Override
        public Node enter(LiteralNode literalNode) {
            return findOffsets(literalNode);
        }

        @Override
        public Node enter(ObjectNode objectNode) {
            return findOffsets(objectNode);
        }

        @Override
        public Node enter(PropertyNode propertyNode) {
            return findOffsets(propertyNode);
        }

        @Override
        public Node enter(ReferenceNode referenceNode) {
            return findOffsets(referenceNode);
        }

        @Override
        public Node enter(ReturnNode returnNode) {
            return findOffsets(returnNode);
        }

        @Override
        public Node enter(RuntimeNode runtimeNode) {
            return findOffsets(runtimeNode);
        }

        @Override
        public Node enter(SwitchNode switchNode) {
            return findOffsets(switchNode);
        }

        @Override
        public Node enter(TernaryNode ternaryNode) {
            return findOffsets(ternaryNode);
        }

        @Override
        public Node enter(ThrowNode throwNode) {
            return findOffsets(throwNode);
        }

        @Override
        public Node enter(TryNode tryNode) {
            return findOffsets(tryNode);
        }

        @Override
        public Node enter(UnaryNode unaryNode) {
            return findOffsets(unaryNode);
        }

        @Override
        public Node enter(VarNode varNode) {
            return findOffsets(varNode);
        }

        @Override
        public Node enter(WhileNode whileNode) {
            return findOffsets(whileNode);
        }

        @Override
        public Node enter(WithNode withNode) {
            return findOffsets(withNode);
        }
        
        
        
    }
}
