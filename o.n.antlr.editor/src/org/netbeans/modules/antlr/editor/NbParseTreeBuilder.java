/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.antlr.editor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.debug.BlankDebugEventListener;

import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import org.antlr.runtime.NoViableAltException;
import org.netbeans.modules.antlr.editor.gen.ANTLRv3Lexer;
import org.openide.util.NbBundle;

/**
 * A patched version of ANLR's ParseTreeBuilder 
 * 
 * @author mfukala@netbeans.org
 */
public class NbParseTreeBuilder extends BlankDebugEventListener {

    List<CommonToken> hiddenTokens = new ArrayList<CommonToken>();
    private int backtracking = 0;
    private CommonToken lastConsumedToken;
    private CharSequence source;
    static boolean debug_tokens = false; //testing 
    private boolean resync;
    private CommonToken unexpectedToken;
    Stack<RuleNode> callStack = new Stack<RuleNode>();
        
    public NbParseTreeBuilder(CharSequence source) {
        this.source = source;
        callStack.push(new RootNode(source));
    }

    public AbstractParseTreeNode getTree() {
        return callStack.elementAt(0);
    }

    /** Backtracking or cyclic DFA, don't want to add nodes to tree */
    @Override
    public void enterDecision(int d, boolean couldBacktrack) {
        backtracking++;
    }

    @Override
    public void exitDecision(int i) {
        backtracking--;
    }

    private boolean isIgnoredRule(String ruleName) {
//        return Arrays.binarySearch(IGNORED_RULES, ruleName) >= 0;
        return false;
    }

    @Override
    public void enterRule(String filename, String ruleName) {
        if (backtracking > 0) {
            return;
        }
        System.out.println("entering " + ruleName);
        
        if (isIgnoredRule(ruleName)) {
            return;
        }

        AbstractParseTreeNode parentRuleNode = callStack.peek();
        RuleNode ruleNode = new RuleNode(NodeType.valueOf(ruleName), source);
        addNodeChild(parentRuleNode, ruleNode);
        callStack.push(ruleNode);
    }

    @Override
    public void exitRule(String filename, String ruleName) {
        if (backtracking > 0) {
            return;
        }
        System.out.println("exiting " + ruleName);
        
        if (isIgnoredRule(ruleName)) {
            return;
        }

        RuleNode ruleNode = callStack.pop();
        if (ruleNode.getChildCount() > 0) {
            //set the rule end offset
            if (lastConsumedToken != null) {
                ruleNode.setLastToken(lastConsumedToken);
            }
        }
        
        
    }

    @Override
    public void beginResync() {
        super.beginResync();
        resync = true;
    }

    @Override
    public void endResync() {
        super.endResync();
        resync = false;
    }
    
    @Override
    public void consumeToken(Token token) {
        if (backtracking > 0 || resync) {
            return;
        }

        if (debug_tokens) {
            CommonToken ct = (CommonToken) token;
            int[] ctr = CommonTokenUtil.getCommonTokenOffsetRange(ct);
            System.out.println(token + "(" + ctr[0] + "-" + ctr[1] + ")");
        }

        //ignore the closing EOF token, we do not want it
        //it the parse tree
        if (token.getType() == ANTLRv3Lexer.EOF) {
            return;
        }

        //also ignore error tokens - they are added as children of ErrorNode-s in the recognitionException(...) method
        if (token.getType() == Token.INVALID_TOKEN_TYPE) {
            return;
        }

        lastConsumedToken = (CommonToken) token;

        RuleNode ruleNode = callStack.peek();
        TokenNode elementNode = new TokenNode(source, (CommonToken) token);
        elementNode.hiddenTokens = this.hiddenTokens;
        hiddenTokens.clear();
        ruleNode.addChild(elementNode);

        updateFirstTokens(ruleNode, lastConsumedToken);
    }

    //set first token for all RuleNode-s in the stack without the first token set
    private void updateFirstTokens(RuleNode ruleNode, CommonToken token) {
        while (true) {

            if (ruleNode.from() != -1) {
                break;
            }
            ruleNode.setFirstToken(token);
            ruleNode = (RuleNode) ruleNode.getParent();
            if (ruleNode == null) {
                break;
            }
        }
    }

    @Override
    public void consumeHiddenToken(Token token) {
        if (backtracking > 0 || resync) {
            return;
        }

        if (debug_tokens) {
            CommonToken ct = (CommonToken) token;
            int[] ctr = CommonTokenUtil.getCommonTokenOffsetRange(ct);
            System.out.println(token + "(" + ctr[0] + "-" + ctr[1] + ")");
        }

        hiddenTokens.add((CommonToken) token);
    }

//    @Override
//    public void recognitionException(RecognitionException e) {
//        if (backtracking > 0) {
//            return;
//        }
//        
//        RuleNode ruleNode = callStack.peek();
//
//        String message;
//        int from, to;
//
//        assert e.token != null;
//
//        //invalid token found int the stream
//        unexpectedToken = (CommonToken) e.token;
//        int unexpectedTokenCode = e.getUnexpectedType();
//        CssTokenId unexpectedTokenId = CssTokenId.forTokenTypeCode(unexpectedTokenCode);
//
//        assert unexpectedTokenId != null : "No CssTokenId for " + unexpectedToken;
//
//        //special handling for EOF token - it has lenght == 1 !
//        if(unexpectedTokenId == CssTokenId.EOF) {
//            from = to = CommonTokenUtil.getCommonTokenOffsetRange(unexpectedToken)[0]; 
//        } else {
//            //normal tokens
//            from = CommonTokenUtil.getCommonTokenOffsetRange(unexpectedToken)[0]; 
//            to = CommonTokenUtil.getCommonTokenOffsetRange(unexpectedToken)[1];
//        }
//      
//        if (unexpectedTokenId == CssTokenId.EOF) {
//            message = NbBundle.getMessage(NbParseTreeBuilder.class, "MSG_Error_Premature_EOF");
//        } else {
//            message = NbBundle.getMessage(NbParseTreeBuilder.class, "MSG_Error_Unexpected_Token", unexpectedTokenId.name());
//        }
//        
//        //create a ParsingProblem
//        ProblemDescription problemDescription = new ProblemDescription(
//                from,
//                to,
//                message,
//                ProblemDescription.Keys.PARSING.name(),
//                ProblemDescription.Type.ERROR);
//
//        problems.add(problemDescription);
//        
//        //create an error node and add it to the parse tree
//        ErrorNode errorNode = new ErrorNode(from, to, problemDescription, source);
//
//        //add the unexpected token as a child of the error node
//        TokenNode tokenNode = new TokenNode(source, unexpectedToken);
//        addNodeChild(errorNode, tokenNode);
//        
//        if(e instanceof NoViableAltException) {
//            //error during predicate - the unexpected token may or may not be
//            //reported later as an error. To handle this,
//            //store the error node and the ruleNode where the error node should be added
//            noViableAltNodes.put(unexpectedToken, new Pair<Node>(ruleNode, errorNode));
//            errorNodes.push(errorNode);
//        } else {
//            //possibly remove the unexpectedToken from the noViableAltNodes map
//            
//            //NOTICE:
//            //Uncomment the following line if you want the parse tree not to produce
//            //multiple error nodes for the same token. If the line is active, there 
//            //wont be error nodes for semantic predicates if the unexpected token
//            //is matched by another error rule later.
////            noViableAltNodes.remove(unexpectedToken);
//            
//            addNodeChild(ruleNode, errorNode);
//            errorNodes.push(errorNode);
//
//            //create and artificial error token so the rules on stack can properly set their ranges
//            lastConsumedToken = new CommonToken(Token.INVALID_TOKEN_TYPE);
//            lastConsumedToken.setStartIndex(from);
//            lastConsumedToken.setStopIndex(to - 1); // ... ( -1 => last *char* index )
//        }
//
//
//    }

//    @Override
//    public void terminate() {
//        super.terminate();
//
//        //process unreported errors from NoViableAltException
//        for(Pair<Node> pair : noViableAltNodes.values()) {
//            RuleNode ruleNode = (RuleNode)pair.n1;
//            ErrorNode errorNode = (ErrorNode)pair.n2;
//            
//            ruleNode.addChild(errorNode);
//            errorNode.setParent(ruleNode);
//        }
//        
//        //Finally after the parsing is done fix the error nodes and their predecessors.
//        //This fixes the problem with rules where RecognitionException happened
//        //but the errorneous or missing token has been matched in somewhere further
//        for (ErrorNode en : errorNodes) {
//            synchronizeAncestorsBoundaries(en);
//        }
//        
//        //clean parse tree from empty rule nodes 
//        //empty rule node == rule node without a single _token node_ child
//        for(RuleNode node : leafRuleNodes) {
//            removeLeafRuleNodes(node);
//        }
//
//    }
//    
//    //removes all empty rule nodes in the tree path from the given node to the parse tree root
//    private void removeLeafRuleNodes(RuleNode node) {
//        for(;;) {
//            if(node.children().isEmpty()) {
//                RuleNode parent = (RuleNode)node.parent();
//                if(parent == null) {
//                    return ;
//                }
//                parent.deleteChild(node);
//                node = parent;
//            } else {
//                break;
//            }
//        }
//    }
//    
//    private void synchronizeAncestorsBoundaries(RuleNode en) {
//        RuleNode n = en;
//            for (;;) {
//                if (n == null) {
//                    break;
//                }
//                
//                //adjust the parent nodes ranges to the errorNode
//                if (n.from() == -1 || n.from() > en.from()) {
//                    n.from = en.from();
//                }
//                if(n.to() == -1 || n.to() < en.to()) {                    
//                    n.to = en.to();
//                }
//                
//                n = (RuleNode) n.parent();
//            }
//    }
//
//    public Collection<ProblemDescription> getProblems() {
//        return problems;
//    }
//
//    //note: it would be possible to handle this all in consumeToken since it is called from the
//    //BaseRecognizer.consumeUntil(...) {   input.consume();   } but for the better usability
//    //it is done this way. So the beginResyn/endResync doesn't have to be used.
//    //the NbParseTreeBuilder.consumeToken() method ignores tokens with ERROR type so they
//    //won't be duplicated in the parse tree
//    
//    //creates a "recovery" node with all the skipped tokens as children
//    void consumeSkippedTokens(List<Token> tokens) {
//        if(tokens.isEmpty()) {
//            return ;
//        }
//
//        CommonToken first = (CommonToken)tokens.get(0);
//        CommonToken last = (CommonToken)tokens.get(tokens.size() - 1);
//        
//        
//
//        //if there's just one recovered token and the token is the same as the unexpectedToken just skip the 
//        //recovery node creation, the parse tree for the errorneous piece of code is already complete
//        boolean ignoreFirstToken = unexpectedToken  == first;
//        if(ignoreFirstToken && tokens.size() == 1) {
//            return ;
//        }
//        
//        //do not add the first token as children of the recovery node if it has been already
//        //added as a child of the error node created for the RecognitionException
//        if(ignoreFirstToken) {
//            first = (CommonToken)tokens.get(1); //use second
//        }
//        
//        //find last error which triggered this recovery and add the skipped tokens to it
////        ErrorNode errorNode = errorNodes.peek();
////        RuleNode peek = callStack.peek();
////        if(!(peek instanceof ErrorNode)) {
//        
//        RuleNode peek = errorNodes.peek();
//        
//            RuleNode node = new RuleNode(NodeType.recovery, source);
//            peek.addChild(node);
//            node.setParent(peek);
//            peek = node;
//            
////        }
//            
//        
//        //set first and last token
//        peek.setFirstToken(first);
//        peek.setLastToken(last);
//        
//        synchronizeAncestorsBoundaries(peek);
//        
//        //set range
//        peek.from = CommonTokenUtil.getCommonTokenOffsetRange(first)[0]; 
//        peek.to = CommonTokenUtil.getCommonTokenOffsetRange(last)[1]; 
//        
//        //set the error tokens as children of the error node
//        for(int i = (ignoreFirstToken ? 1 : 0); i < tokens.size(); i++) {
//            CommonToken token = (CommonToken)tokens.get(i);
//            TokenNode tokenNode = new TokenNode(source, token);
//            addNodeChild(peek, tokenNode);
//        }
//        
//        //create and artificial error token so the rules on stack can properly set their ranges
//        lastConsumedToken = new CommonToken(Token.INVALID_TOKEN_TYPE);
//        lastConsumedToken.setStartIndex(first.getStartIndex());
//        lastConsumedToken.setStopIndex(last.getStopIndex()); 
//                
//    }
//    
//    
    private void addNodeChild(AbstractParseTreeNode parent, AbstractParseTreeNode child) {
        parent.addChild(child);
        child.setParent(parent);
    }
//    
//    private static class Pair<T> {
//        T n1, n2;
//        public Pair(T n1, T n2) {
//            this.n1 = n1;
//            this.n2 = n2;
//        }
//    }
//    
}
