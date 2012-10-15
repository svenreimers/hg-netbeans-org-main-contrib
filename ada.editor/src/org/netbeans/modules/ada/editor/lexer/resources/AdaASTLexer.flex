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
package org.netbeans.modules.ada.editor.lexer;

import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.ada.editor.parser.AdaASTSymbols;
import org.netbeans.modules.ada.editor.ast.nodes.*;
import java_cup.runtime.*;
import org.netbeans.modules.ada.project.api.AdaLanguageOptions;

%%
// Options adn declarations section

%class AdaASTLexer
%implements Scanner
%type Symbol
%function next_token
%public

%eofval{
    return createSymbol(AdaASTSymbols.EOF);
%eofval}
%eofclose

%unicode
%caseless

//Turns character counting on
%char
//Turns line counting on
%line
//Turns column counting on
%column


%state ST_LOOKING_FOR_PROPERTY
%state ST_LINE_COMMENT

%{
    private final List commentList = new LinkedList();
    private String comment = null;
    private StateStack stack = new StateStack();
    private char yy_old_buffer[] = new char[ZZ_BUFFERSIZE];
    private int yy_old_pushbackPos;
    protected int commentStartPosition;
    private AdaLanguageOptions.AdaVersion adaVersion = AdaLanguageOptions.AdaVersion.ADA_95;

    public AdaASTLexer(java.io.Reader in, AdaLanguageOptions.AdaVersion adaVersion) {
        this(in);
        this.adaVersion = adaVersion;
    }

    public AdaLanguageOptions.AdaVersion getAdaVersion() {
            return this.adaVersion;
    }

    public void reset(java.io.Reader reader) {
        yyreset(reader);
    }

    public void setState(int state) {
        yybegin(state);
    }

    public int getState() {
        return yystate();
    }

    public void resetCommentList() {
            commentList.clear();
    }

    public List getCommentList() {
            return commentList;
    }
	
    protected void addComment(Comment.Type type) {
            int leftPosition = getTokenStartPosition();
            Comment comm;
            comm = new Comment(commentStartPosition, leftPosition + getTokenLength());
            commentList.add(comm);
    }
	
    private void pushState(int state) {
        stack.pushStack(zzLexicalState);
        yybegin(state);
    }

    private void popState() {
        yybegin(stack.popStack());
    }

    public int getCurrentLine() {
        return yyline;
    }

    protected int getTokenStartPosition() {
        return zzStartRead - zzPushbackPos;
    }

    protected int getTokenLength() {
        return zzMarkedPos - zzStartRead;
    }

    public int getLength() {
        return zzEndRead - zzPushbackPos;
    }
    
    private void handleCommentStart() {
        commentStartPosition = getTokenStartPosition();
    }
	
    private void handleLineCommentEnd() {
         addComment(Comment.Type.TYPE_SINGLE_LINE);
    }
        
    private Symbol createFullSymbol(int symbolNumber) {
        Symbol symbol = createSymbol(symbolNumber);
        symbol.value = yytext();
        return symbol;
    }

    private Symbol createSymbol(int symbolNumber) {
        int leftPosition = getTokenStartPosition();
        Symbol symbol = new Symbol(symbolNumber, leftPosition, leftPosition + getTokenLength());
        return symbol;
    }

    public int[] getParamenters(){
    	return new int[]{zzMarkedPos, zzPushbackPos, zzCurrentPos, zzStartRead, zzEndRead, yyline};
    }
    
    public void reset(java.io.Reader  reader, char[] buffer, int[] parameters){
            this.zzReader = reader;
            this.zzBuffer = buffer;
            this.zzMarkedPos = parameters[0];
            this.zzPushbackPos = parameters[1];
            this.zzCurrentPos = parameters[2];
            this.zzStartRead = parameters[3];
            this.zzEndRead = parameters[4];
            this.yyline = parameters[5];
            this.yychar = this.zzStartRead - this.zzPushbackPos;
    }

%}

/*********************************************************
 *                                                       *
 * Ada 83, 95, 2005 and 2012 AST Lexer, based on:        *
 *                                                       *
 * 1. Ada 83 Reference Manual                            *
 * 2. Ada Reference Manual                               *
 *    ISO/IEC 8652:1995(E)                               *
 *    with Technical Corrigendum 1                       *
 *    Language and Standard Libraries                    *
 *    Copyright © 1992,1993,1994,1995 Intermetrics, Inc. *
 *    Copyright © 2000 The MITRE Corporation, Inc.       *
 * 3. http://www.adaic.com/standards/95lrm/lexer9x.l     *
 * 4. Ada Reference Manual                               *
 *    ISO/IEC 8652:201z Ed. 3                            *
 *    with Technical Corrigendum 1                       *
 *    and Amendment 1                                    *
 *    and Amendment 2 (Draft 10)                         *
 *    Language and Standard Libraries                    *
 *    Copyright © 1992,1993,1994,1995 Intermetrics, Inc. *
 *    Copyright © 2000 The MITRE Corporation, Inc.       *
 *    Copyright © 2004, 2005, 2006 AXE Consultants       *
 *    Copyright © 2004, 2005, 2006 Ada-Europe            *
 *    Copyright © 2008, 2009, 2010 AXE Consultants       *
 *                                                       *
 * Author: Andrea Lucarelli                              *
 * Lexer Generator: JFlex                                *
 *                                                       *
 *********************************************************/

DIGIT=[0-9]
EXTENDED_DIGIT=[0-9a-fA-F]
INTEGER=({DIGIT}(_?{DIGIT})*)
EXPONENT=([eE](\+?|-){INTEGER})
DECIMAL_LITERAL={INTEGER}(\.?{INTEGER})?{EXPONENT}?
BASE={INTEGER}
BASED_INTEGER={EXTENDED_DIGIT}(_?{EXTENDED_DIGIT})*
BASED_LITERAL={BASE}(#|:){BASED_INTEGER}(\.{BASED_INTEGER})?(#|:){EXPONENT}?
IDENTIFIER=[a-zA-Z]("_"?[a-zA-Z0-9])*
WHITESPACE=[ \n\r\t]+
STRING_LITERAL=\"(\"\"|[^\n\"])*\"
CHAR_LITERAL=\'[^\n]\'
WHITESPACE=[ \n\r\t]+
NEWLINE=("\r"|"\n"|"\r\n")
ANY_CHAR=(.|[\n])

%%

// Ada 83, 95 and 2005 keywords

<YYINITIAL> {

    "abort"         { return createSymbol(AdaASTSymbols.ABORT); }
    "abs"           { return createSymbol(AdaASTSymbols.ABS); }
    "abstract"      {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return createSymbol(AdaASTSymbols.ABSTRACT);
        }
        else {
            return createSymbol(AdaASTSymbols.IDENTIFIER);
        }
    }
    "access"        { return createSymbol(AdaASTSymbols.ACCESS); }
    "accept"        { return createSymbol(AdaASTSymbols.ACCEPT); }
    "aliased"       {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return createSymbol(AdaASTSymbols.ALIASED);
        }
        else {
            return createSymbol(AdaASTSymbols.IDENTIFIER);
        }
    }
    "all"           { return createSymbol(AdaASTSymbols.ALL); }
    "and"           { return createSymbol(AdaASTSymbols.AND); }
    "array"         { return createSymbol(AdaASTSymbols.ARRAY); }
    "at"            { return createSymbol(AdaASTSymbols.AT); }
    "begin"         { return createSymbol(AdaASTSymbols.BEGIN); }
    "body"          { return createSymbol(AdaASTSymbols.BODY); }
    "constant"      { return createSymbol(AdaASTSymbols.CONSTANT); }
    "case"          { return createSymbol(AdaASTSymbols.CASE); }
    "declare"       { return createSymbol(AdaASTSymbols.DECLARE); }
    "delay"         { return createSymbol(AdaASTSymbols.DELAY); }
    "do"            { return createSymbol(AdaASTSymbols.DO); }
    "delta"         { return createSymbol(AdaASTSymbols.DELTA); }
    "else"          { return createSymbol(AdaASTSymbols.ELSE); }
    "elsif"         { return createSymbol(AdaASTSymbols.ELSIF); }
    "end"           { return createSymbol(AdaASTSymbols.END); }
    "entry"         { return createSymbol(AdaASTSymbols.ENTRY); }
    "exception"     { return createSymbol(AdaASTSymbols.EXCEPTION); }
    "exit"          { return createSymbol(AdaASTSymbols.EXIT); }
    "for"           { return createSymbol(AdaASTSymbols.FOR); }
    "function"      { return createSymbol(AdaASTSymbols.FUNCTION); }
    "generic"       { return createSymbol(AdaASTSymbols.GENERIC); }
    "goto"          { return createSymbol(AdaASTSymbols.GOTO); }
    "if"            { return createSymbol(AdaASTSymbols.IF); }
    "in"            { return createSymbol(AdaASTSymbols.IN); }
    "interface"     {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005 ||
            adaVersion == AdaLanguageOptions.AdaVersion.ADA_2012) {
            return createSymbol(AdaASTSymbols.INTERFACE);
        }
        else {
            return createFullSymbol(AdaASTSymbols.IDENTIFIER);
        }
    }
    "is"            { return createSymbol(AdaASTSymbols.IS); }
    "limited"       { return createSymbol(AdaASTSymbols.LIMITED); }
    "loop"          { return createSymbol(AdaASTSymbols.LOOP); }
    "mod"           { return createSymbol(AdaASTSymbols.MOD); }
    "new"           { return createSymbol(AdaASTSymbols.NEW); }
    "not"           { return createSymbol(AdaASTSymbols.NOT); }
    "null"          { return createSymbol(AdaASTSymbols.NULL); }
    "of"            { return createSymbol(AdaASTSymbols.OF); }
    "or"            { return createSymbol(AdaASTSymbols.OR); }
    "others"        { return createSymbol(AdaASTSymbols.OTHERS); }
    "out"           { return createSymbol(AdaASTSymbols.OUT); }
    "overriding"    {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005 ||
            adaVersion == AdaLanguageOptions.AdaVersion.ADA_2012) {
            return createSymbol(AdaASTSymbols.OVERRIDING);
        }
        else {
            return createFullSymbol(AdaASTSymbols.IDENTIFIER);
        }
    }
    "package"       { return createSymbol(AdaASTSymbols.PACKAGE); }
    "pragma"        { return createSymbol(AdaASTSymbols.PRAGMA); }
    "private"       { return createSymbol(AdaASTSymbols.PRIVATE); }
    "procedure"     { return createSymbol(AdaASTSymbols.PROCEDURE); }
    "protected"     {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return createSymbol(AdaASTSymbols.PROTECTED);
        }
        else {
            return createSymbol(AdaASTSymbols.IDENTIFIER);
        }
    }
    "return"        { return createSymbol(AdaASTSymbols.RETURN); }
    "reverse"       { return createSymbol(AdaASTSymbols.REVERSE); }
    "raise"         { return createSymbol(AdaASTSymbols.RAISE); }
    "range"         { return createSymbol(AdaASTSymbols.RANGE); }
    "record"        { return createSymbol(AdaASTSymbols.RECORD); }
    "rem"           { return createSymbol(AdaASTSymbols.REM); }
    "renames"       { return createSymbol(AdaASTSymbols.RENAMES); }
    "requeue"       {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return createSymbol(AdaASTSymbols.REQUEUE);
        }
        else {
            return createSymbol(AdaASTSymbols.IDENTIFIER);
        }
    }
    "select"        { return createSymbol(AdaASTSymbols.SELECT); }
    "separate"      { return createSymbol(AdaASTSymbols.SEPARATE); }
    "some"  {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2012) {
            return createSymbol(AdaASTSymbols.SOME);
        }
        else {
            return createFullSymbol(AdaASTSymbols.IDENTIFIER);
        }
    }
    "subtype"       { return createSymbol(AdaASTSymbols.SUBTYPE); }
    "synchronized"  {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005 ||
            adaVersion == AdaLanguageOptions.AdaVersion.ADA_2012) {
            return createSymbol(AdaASTSymbols.SYNCHRONIZED);
        }
        else {
            return createFullSymbol(AdaASTSymbols.IDENTIFIER);
        }
    }
    "tagged"        {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return createSymbol(AdaASTSymbols.TAGGED);
        }
        else {
            return createSymbol(AdaASTSymbols.IDENTIFIER);
        }
    }
    "task"          { return createSymbol(AdaASTSymbols.TASK); }
    "terminate"     { return createSymbol(AdaASTSymbols.TERMINATE); }
    "then"          { return createSymbol(AdaASTSymbols.THEN); }
    "type"          { return createSymbol(AdaASTSymbols.TYPE); }
    "until"         {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return createSymbol(AdaASTSymbols.UNTIL);
        }
        else {
            return createSymbol(AdaASTSymbols.IDENTIFIER);
        }
    }
    "use"           { return createSymbol(AdaASTSymbols.USE); }
    "when"          { return createSymbol(AdaASTSymbols.WHEN); }
    "while"         { return createSymbol(AdaASTSymbols.WHILE); }
    "with"          { return createSymbol(AdaASTSymbols.WITH); }
    "xor"           { return createSymbol(AdaASTSymbols.XOR); }

}

// delimiters

<YYINITIAL> {

    "&"             { return createSymbol(AdaASTSymbols.AMP); }
    "'"             { return createSymbol(AdaASTSymbols.TICK); }
    "("             { return createSymbol(AdaASTSymbols.LPAREN); }
    ")"             { return createSymbol(AdaASTSymbols.RPAREN); }
    "*"             { return createSymbol(AdaASTSymbols.STAR); }
    "+"             { return createSymbol(AdaASTSymbols.PLUS); }
    ","             { return createSymbol(AdaASTSymbols.COMMA); }
    "-"             { return createSymbol(AdaASTSymbols.MINUS); }
    "."             { pushState(ST_LOOKING_FOR_PROPERTY);
                      return createSymbol(AdaASTSymbols.DOT);
                    }
    "/"             { return createSymbol(AdaASTSymbols.SLASH); }
    ":"             { return createSymbol(AdaASTSymbols.COLON); }
    ";"             { return createSymbol(AdaASTSymbols.SEMICOLON); }
    "<"             { return createSymbol(AdaASTSymbols.GT); }
    "="             { return createSymbol(AdaASTSymbols.EQ); }
    ">"             { return createSymbol(AdaASTSymbols.LT); }
    "|"             { return createSymbol(AdaASTSymbols.BAR); }
    "!"             { return createSymbol(AdaASTSymbols.BAR); }

}

// compound delimiters

<YYINITIAL> {

    "=>"             { return createSymbol(AdaASTSymbols.ARROW); }
    ".."             { return createSymbol(AdaASTSymbols.DOT_DOT); }
    "**"             { return createSymbol(AdaASTSymbols.EXPON); }
    ":="             { return createSymbol(AdaASTSymbols.ASSIGNMENT); }
    "/="             { return createSymbol(AdaASTSymbols.INEQ); }
    ">="             { return createSymbol(AdaASTSymbols.GTEQ); }
    "<="             { return createSymbol(AdaASTSymbols.LTEQ); }
    "<<"             { return createSymbol(AdaASTSymbols.LTLT); }
    ">>"             { return createSymbol(AdaASTSymbols.GTGT); }
    "<>"             { return createSymbol(AdaASTSymbols.BOX); }

}

<ST_LOOKING_FOR_PROPERTY>"." {
    return createSymbol(AdaASTSymbols.DOT);
}

<ST_LOOKING_FOR_PROPERTY>".." {
    popState();
    return createSymbol(AdaASTSymbols.DOT_DOT);
}

<ST_LOOKING_FOR_PROPERTY>{IDENTIFIER} {
    popState();
    return createFullSymbol(AdaASTSymbols.IDENTIFIER);
}

<ST_LOOKING_FOR_PROPERTY>{ANY_CHAR} {
    yypushback(yylength());
    popState();
}

<YYINITIAL>{CHAR_LITERAL} {
    return createFullSymbol(AdaASTSymbols.CHAR_LITERAL);
}

<YYINITIAL>{STRING_LITERAL} {
    return createFullSymbol(AdaASTSymbols.STRING_LITERAL);
}

<YYINITIAL>{BASED_LITERAL} {
    return createFullSymbol(AdaASTSymbols.BASED_LITERAL);
}

<YYINITIAL>{DECIMAL_LITERAL} {
    return createFullSymbol(AdaASTSymbols.DECIMAL_LITERAL);
}

<YYINITIAL>{IDENTIFIER} {
    return createFullSymbol(AdaASTSymbols.IDENTIFIER);
}

<YYINITIAL>{WHITESPACE} {
}

<YYINITIAL>"--" {
	handleCommentStart();
	yybegin(ST_LINE_COMMENT);
}

<ST_LINE_COMMENT>[^\n\r]*(ANY_CHAR|{NEWLINE}) {
    handleLineCommentEnd();
    yybegin(YYINITIAL);
}

<YYINITIAL> <<EOF>> {
              if (yytext().length() > 0) {
                yypushback(1);  // backup eof
                comment = yytext();
              }
              else {
                return createSymbol(AdaASTSymbols.EOF);
              }
              
}

<YYINITIAL>{ANY_CHAR} {
	// do nothing
}