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

import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.modules.ada.project.api.AdaLanguageOptions;

%%

%public
%class AdaSyntaxLexer
%type AdaTokenId
%function nextToken
%unicode
%caseless
%char

%state ST_LOOKING_FOR_PROPERTY
%state ST_CHAR_LITERAL
%state ST_LINE_COMMENT
%state ST_HIGHLIGHTING_ERROR

%eofval{
       if(input.readLength() > 0) {
            // backup eof
            input.backup(1);
            //and return the text as error token
            return AdaTokenId.UNKNOWN_TOKEN;
        } else {
            return null;
        }
%eofval}

%{

    private StateStack stack = new StateStack();

    private AdaLanguageOptions.AdaVersion adaVersion = AdaLanguageOptions.AdaVersion.ADA_95;

    private LexerInput input;
    
    public AdaSyntaxLexer(LexerRestartInfo info, AdaLanguageOptions.AdaVersion adaVersion) {
        this.input = info.input();
        this.adaVersion = adaVersion;

        if(info.state() != null) {
            //reset state
            setState((LexerState)info.state());
        } else {
            //initial state
            zzState = zzLexicalState = YYINITIAL;
            stack.clear();
        }

    }

    public static final class LexerState  {
        final StateStack stack;
        /** the current state of the DFA */
        final int zzState;
        /** the current lexical state */
        final int zzLexicalState;
        final AdaLanguageOptions.AdaVersion adaVersion;

        LexerState (StateStack stack, int zzState, int zzLexicalState, AdaLanguageOptions.AdaVersion adaVersion) {
            this.stack = stack;
            this.zzState = zzState;
            this.zzLexicalState = zzLexicalState;
            this.adaVersion = adaVersion;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                    return true;
            }

            if (obj == null || obj.getClass() != this.getClass()) {
                    return false;
            }

            LexerState state = (LexerState) obj;
            return (this.stack.equals(state.stack)
                && (this.zzState == state.zzState)
                && (this.adaVersion == state.adaVersion)
                && (this.zzLexicalState == state.zzLexicalState));
        }

        @Override
        public int hashCode() {
            int hash = 11;
            hash = 31 * hash + this.zzState;
            hash = 31 * hash + this.zzLexicalState;
            hash = 31 * hash + this.stack.hashCode();
            return hash;
        }
    }
        
    public LexerState getState() {
        return new LexerState(stack.createClone(), zzState, zzLexicalState, adaVersion);
    }

    public void setState(LexerState state) {
        this.stack.copyFrom(state.stack);
        this.zzState = state.zzState;
        this.zzLexicalState = state.zzLexicalState;
        this.adaVersion = state.adaVersion;
    }
    
    public int[] getParamenters(){
    	return new int[]{zzMarkedPos, zzPushbackPos, zzCurrentPos, zzStartRead, zzEndRead, yyline, zzLexicalState};
    }

    protected int getZZLexicalState() {
        return zzLexicalState;
    }

    protected int getZZMarkedPos() {
        return zzMarkedPos;
    }

    protected int getZZEndRead() {
        return zzEndRead;
    }

    public char[] getZZBuffer() {
        return zzBuffer;
    }
    
    protected int getZZStartRead() {
    	return this.zzStartRead;
    }

    protected int getZZPushBackPosition() {
    	return this.zzPushbackPos;
    }

        protected void pushBack(int i) {
		yypushback(i);
	}

        protected void popState() {
		yybegin(stack.popStack());
	}

	protected void pushState(final int state) {
		stack.pushStack(getZZLexicalState());
		yybegin(state);
	}

    
 // End user code

%}

/*********************************************************
 *                                                       *
 * Ada 83, 95, 2005 and 2012 Lexer, based on:            *
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

    "abort"         { return AdaTokenId.ABORT; }
    "abs"           { return AdaTokenId.ABS; }
    "abstract"      { 
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ABSTRACT;
        }
        else {
            return AdaTokenId.IDENTIFIER;
        }
    }
    "access"        { return AdaTokenId.ACCESS; }
    "accept"        { return AdaTokenId.ACCEPT; }
    "aliased"       {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ALIASED;
        }
        else {
            return AdaTokenId.IDENTIFIER;
        }
    }
    "all"           { return AdaTokenId.ALL; }
    "and"           { return AdaTokenId.AND; }
    "array"         { return AdaTokenId.ARRAY; }
    "at"            { return AdaTokenId.AT; }
    "begin"         { return AdaTokenId.BEGIN; }
    "body"          { return AdaTokenId.BODY; }
    "constant"      { return AdaTokenId.CONSTANT; }
    "case"          { return AdaTokenId.CASE; }
    "declare"       { return AdaTokenId.DECLARE; }
    "delay"         { return AdaTokenId.DELAY; }
    "delta"         { return AdaTokenId.DELTA; }
    "digits"        { return AdaTokenId.DIGITS; }
    "do"            { return AdaTokenId.DO; }
    "else"          { return AdaTokenId.ELSE; }
    "elsif"         { return AdaTokenId.ELSIF; }
    "end"           { return AdaTokenId.END; }
    "entry"         { return AdaTokenId.ENTRY; }
    "exception"     { return AdaTokenId.EXCEPTION; }
    "exit"          { return AdaTokenId.EXIT; }
    "for"           { return AdaTokenId.FOR; }
    "function"      { return AdaTokenId.FUNCTION; }
    "generic"       { return AdaTokenId.GENERIC; }
    "goto"          { return AdaTokenId.GOTO; }
    "if"            { return AdaTokenId.IF; }
    "in"            { return AdaTokenId.IN; }
    "interface"     {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005 ||
            adaVersion == AdaLanguageOptions.AdaVersion.ADA_2012) {
            return AdaTokenId.INTERFACE;
        }
        else {
            return AdaTokenId.IDENTIFIER;
        }
    }
    "is"            { return AdaTokenId.IS; }
    "limited"       { return AdaTokenId.LIMITED; }
    "loop"          { return AdaTokenId.LOOP; }
    "mod"           { return AdaTokenId.MOD; }
    "new"           { return AdaTokenId.NEW; }
    "not"           { return AdaTokenId.NOT; }
    "null"          { return AdaTokenId.NULL; }
    "of"            { return AdaTokenId.OF; }
    "or"            { return AdaTokenId.OR; }
    "others"        { return AdaTokenId.OTHERS; }
    "out"           { return AdaTokenId.OUT; }
    "overriding"    {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005 ||
            adaVersion == AdaLanguageOptions.AdaVersion.ADA_2012) {
            return AdaTokenId.OVERRIDING;
        }
        else {
            return AdaTokenId.IDENTIFIER;
        }
    }
    "package"       { return AdaTokenId.PACKAGE; }
    "pragma"        { return AdaTokenId.PRAGMA; }
    "private"       { return AdaTokenId.PRIVATE; }
    "procedure"     { return AdaTokenId.PROCEDURE; }
    "protected"     {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.PROTECTED;
        }
        else {
            return AdaTokenId.IDENTIFIER;
        }
    }
    "raise"         { return AdaTokenId.RAISE; }
    "range"         { return AdaTokenId.RANGE; }
    "record"        { return AdaTokenId.RECORD; }
    "rem"           { return AdaTokenId.REM; }
    "renames"       { return AdaTokenId.RENAMES; }
    "return"        { return AdaTokenId.RETURN; }
    "reverse"       { return AdaTokenId.REVERSE; }
    "requeue"       {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.REQUEUE;
        }
        else {
            return AdaTokenId.IDENTIFIER;
        }
    }
    "select"        { return AdaTokenId.SELECT; }
    "separate"      { return AdaTokenId.SEPARATE; }
    "some"  {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2012) {
            return AdaTokenId.SOME;
        }
        else {
            return AdaTokenId.IDENTIFIER;
        }
    }
    "subtype"       { return AdaTokenId.SUBTYPE; }
    "synchronized"  {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005 ||
            adaVersion == AdaLanguageOptions.AdaVersion.ADA_2012) {
            return AdaTokenId.SYNCHRONIZED;
        }
        else {
            return AdaTokenId.IDENTIFIER;
        }
    }
    "tagged"        {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.TAGGED;
        }
        else {
            return AdaTokenId.IDENTIFIER;
        }
    }
    "task"          { return AdaTokenId.TASK; }
    "terminate"     { return AdaTokenId.TERMINATE; }
    "then"          { return AdaTokenId.THEN; }
    "type"          { return AdaTokenId.TYPE; }
    "until"         {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.UNTIL;
        }
        else {
            return AdaTokenId.IDENTIFIER;
        }
    }
    "use"           { return AdaTokenId.USE; }
    "when"          { return AdaTokenId.WHEN; }
    "while"         { return AdaTokenId.WHILE; }
    "with"          { return AdaTokenId.WITH; }
    "xor"           { return AdaTokenId.XOR; }

}

// Ada 95 compound keywords

<YYINITIAL> {
    "end case"      { return AdaTokenId.END_CASE; }
    "end if"        { return AdaTokenId.END_IF; }
    "end loop"      { return AdaTokenId.END_LOOP; }
}

// attributes

<YYINITIAL> {

    \'"access"                          {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"address"                         { return AdaTokenId.ATTRIBUTE; }
    \'"adjacent"                        {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"aft"                             { return AdaTokenId.ATTRIBUTE; }
    \'"alignment"                       {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"base"                            { return AdaTokenId.ATTRIBUTE; }
    \'"bit_order"                       {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"body_version"                    {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"callable"                        { return AdaTokenId.ATTRIBUTE; }
    \'"caller"                          {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"ceiling"                         {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"class"                           {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"component_size"                  {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"compose"                         {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"constrained"                     { return AdaTokenId.ATTRIBUTE; }
    \'"copy_sign"                       {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"count"                           { return AdaTokenId.ATTRIBUTE; }
    \'"delta"                           { return AdaTokenId.ATTRIBUTE; }
    \'"denorm"                          {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"digits"                          { return AdaTokenId.ATTRIBUTE; }
    \'"exponent"                        {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"external_tag"                    {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"emax"                        {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"epsilon"                    {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"first"                           { return AdaTokenId.ATTRIBUTE; }
    \'"first_bit"                       { return AdaTokenId.ATTRIBUTE; }
    \'"floor"                           {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"fore"                            { return AdaTokenId.ATTRIBUTE; }
    \'"fraction"                        {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"identity"                        {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"image"                           { return AdaTokenId.ATTRIBUTE; }
    \'"input"                           {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"large"                           {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"last"                            { return AdaTokenId.ATTRIBUTE; }
    \'"last_bit"                        { return AdaTokenId.ATTRIBUTE; }
    \'"leading_part"                    {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"length"                          { return AdaTokenId.ATTRIBUTE; }
    \'"machine"                         {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"machine_emax"                    { return AdaTokenId.ATTRIBUTE; }
    \'"machine_emin"                    { return AdaTokenId.ATTRIBUTE; }
    \'"machine_mantissa"                { return AdaTokenId.ATTRIBUTE; }
    \'"machine_overflows"               { return AdaTokenId.ATTRIBUTE; }
    \'"machine_radix"                   { return AdaTokenId.ATTRIBUTE; }
    \'"machine_rounding"                {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"machine_rounds"                  { return AdaTokenId.ATTRIBUTE; }
    \'"max"                             { return AdaTokenId.ATTRIBUTE; }
    \'"mantissa"                        {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"max_size_in_storage_elements"    {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"min"                             {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"model"                           {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"model_emin"                      {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"model_epsilon"                   {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"model_mantissa"                  {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"model_small"                     {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"modulus"                         {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"output"                          {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"partition_id"                    {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"pos"                             { return AdaTokenId.ATTRIBUTE; }
    \'"position"                        { return AdaTokenId.ATTRIBUTE; }
    \'"pred"                            { return AdaTokenId.ATTRIBUTE; }
    \'"priority"                        {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"range"                           { return AdaTokenId.ATTRIBUTE; }
    \'"read"                            {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"remainder"                       {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"round"                           {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"rounding"                        {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"safe_emax"                       {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"safe_first"                      {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"safe_last"                       {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"safe_large"                       {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"safe_small"                       {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"scale"                           {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"scaling"                         {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"signed_zeros"                    {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"size"                            { return AdaTokenId.ATTRIBUTE; }
    \'"small"                           { return AdaTokenId.ATTRIBUTE; }
    \'"storage_pool"                    {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"storage_size"                    { return AdaTokenId.ATTRIBUTE; }
    \'"stream_size"                     {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"succ"                            { return AdaTokenId.ATTRIBUTE; }
    \'"tag"                             {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"terminated"                      { return AdaTokenId.ATTRIBUTE; }
    \'"truncation"                      {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"unbiased_rounding"               {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"unchecked_access"                {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"val"                             { return AdaTokenId.ATTRIBUTE; }
    \'"valid"                           {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"value"                           { return AdaTokenId.ATTRIBUTE; }
    \'"version"                         {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"wide_image"                      {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"wide_value"                      {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"wide_wide_image"                 {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"wide_wide_value"                 {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"wide_wide_width"                 {
        if (adaVersion == AdaLanguageOptions.AdaVersion.ADA_2005) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"wide_width"                      {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
    \'"width"                           { return AdaTokenId.ATTRIBUTE; }
    \'"write"                           {
        if (adaVersion != AdaLanguageOptions.AdaVersion.ADA_83) {
            return AdaTokenId.ATTRIBUTE;
        }
    }
}

// delimiters

<YYINITIAL> {

    "&"             { return AdaTokenId.AMP; }
    "'"             { return AdaTokenId.TICK; }
    "("             { return AdaTokenId.LPAREN; }
    ")"             { return AdaTokenId.RPAREN; }
    "*"             { return AdaTokenId.STAR; }
    "+"             { return AdaTokenId.PLUS; }
    ","             { return AdaTokenId.COMMA; }
    "-"             { return AdaTokenId.MINUS; }
    "."             { pushState(ST_LOOKING_FOR_PROPERTY);
                      return AdaTokenId.DOT;
                    }
    "/"             { return AdaTokenId.SLASH; }
    ":"             { return AdaTokenId.COLON; }
    ";"             { return AdaTokenId.SEMICOLON; }
    "<"             { return AdaTokenId.GT; }
    "="             { return AdaTokenId.EQ; }
    ">"             { return AdaTokenId.LT; }
    "|"             { return AdaTokenId.BAR; }
    "!"             { return AdaTokenId.BAR; }

}

// compound delimiters

<YYINITIAL> {

    "=>"             { return AdaTokenId.ARROW; }
    ".."             { return AdaTokenId.DOT_DOT; }
    "**"             { return AdaTokenId.EXPON; }
    ":="             { return AdaTokenId.ASSIGNMENT; }
    "/="             { return AdaTokenId.INEQ; }
    ">="             { return AdaTokenId.GTEQ; }
    "<="             { return AdaTokenId.LTEQ; }
    "<<"             { return AdaTokenId.LTLT; }
    ">>"             { return AdaTokenId.GTGT; }
    "<>"             { return AdaTokenId.BOX; }

}

<YYINITIAL> {

    "boolean"           { return AdaTokenId.BOOLEAN; }
    "character"         { return AdaTokenId.CHARACTER; }
    "float"             { return AdaTokenId.FLOAT; }
    "integer"           { return AdaTokenId.INTEGER; }
    "wide_character"    { return AdaTokenId.WIDE_CHARACTER; }
    "true"              { return AdaTokenId.TRUE; }
    "false"             { return AdaTokenId.FALSE; }

}

<ST_LOOKING_FOR_PROPERTY>"." {
    return AdaTokenId.DOT;
}

<ST_LOOKING_FOR_PROPERTY>".." {
    popState();
    return AdaTokenId.DOT_DOT;
}

<ST_LOOKING_FOR_PROPERTY>{IDENTIFIER} {
    popState();
    return AdaTokenId.IDENTIFIER;
}

<ST_LOOKING_FOR_PROPERTY>{ANY_CHAR} {
    yypushback(1);
    popState();
}

<YYINITIAL>{IDENTIFIER} {
    return  AdaTokenId.IDENTIFIER;
}

<ST_HIGHLIGHTING_ERROR> {
    {WHITESPACE} {
        popState();
        return AdaTokenId.WHITESPACE;
    }
    . {
        return AdaTokenId.UNKNOWN_TOKEN;
    }
}

<YYINITIAL>{WHITESPACE} {
    return  AdaTokenId.WHITESPACE;
}

<YYINITIAL>{DECIMAL_LITERAL} {
    return AdaTokenId.DECIMAL_LITERAL;
}

<YYINITIAL>{BASED_LITERAL} {
    return AdaTokenId.BASED_LITERAL;
}

<YYINITIAL>{STRING_LITERAL} {
    return AdaTokenId.STRING_LITERAL;
}

<YYINITIAL>{CHAR_LITERAL} {
    return AdaTokenId.CHAR_LITERAL;
}

<YYINITIAL>"--" {
    pushState(ST_LINE_COMMENT);
    return AdaTokenId.COMMENT;
}

<ST_LINE_COMMENT>[^\n\r]*{ANY_CHAR} {
    popState();
    return AdaTokenId.COMMENT;
}

<ST_LINE_COMMENT>{NEWLINE} {
    popState();
    return AdaTokenId.COMMENT;
}

<YYINITIAL>. {
    yypushback(1);
    pushState(ST_HIGHLIGHTING_ERROR);
}
