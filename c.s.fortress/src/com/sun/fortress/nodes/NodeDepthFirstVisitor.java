package com.sun.fortress.nodes;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.sun.fortress.nodes_util.*;
import com.sun.fortress.parser_util.*;
import com.sun.fortress.parser_util.precedence_opexpr.*;
import com.sun.fortress.useful.*;
import edu.rice.cs.plt.tuple.Option;

/** A parametric abstract implementation of a visitor over Node that returns a value.
 ** This visitor implements the visitor interface with methods that 
 ** first visit children, and then call forCASEOnly(), passing in 
 ** the values of the visits of the children. (CASE is replaced by the case name.)
 ** By default, each of forCASEOnly delegates to a more general case; at the
 ** top of this delegation tree is defaultCase(), which (unless overridden)
 ** throws an exception.
 **/
public abstract class NodeDepthFirstVisitor<RetType> extends NodeVisitorLambda<RetType> {
    /**
     * This method is run for all cases that are not handled elsewhere.
     * By default, an exception is thrown; subclasses may override this behavior.
     * @throws IllegalArgumentException
    **/
    public RetType defaultCase(Node that) {
        throw new IllegalArgumentException("Visitor " + getClass().getName() + " does not support visiting values of type " + that.getClass().getName());
    }

    /* Methods to handle a node after recursion. */
    public RetType forAbstractNodeOnly(AbstractNode that) {
        return defaultCase(that);
    }

    public RetType forCompilationUnitOnly(CompilationUnit that, RetType name_result, List<RetType> imports_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forComponentOnly(Component that, RetType name_result, List<RetType> imports_result, List<RetType> exports_result, List<RetType> decls_result) {
        return forCompilationUnitOnly(that, name_result, imports_result);
    }

    public RetType forApiOnly(Api that, RetType name_result, List<RetType> imports_result, List<RetType> decls_result) {
        return forCompilationUnitOnly(that, name_result, imports_result);
    }

    public RetType forImportOnly(Import that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forImportedNamesOnly(ImportedNames that, RetType api_result) {
        return forImportOnly(that);
    }

    public RetType forImportStarOnly(ImportStar that, RetType api_result, List<RetType> except_result) {
        return forImportedNamesOnly(that, api_result);
    }

    public RetType forImportNamesOnly(ImportNames that, RetType api_result, List<RetType> aliasedNames_result) {
        return forImportedNamesOnly(that, api_result);
    }

    public RetType forImportApiOnly(ImportApi that, List<RetType> apis_result) {
        return forImportOnly(that);
    }

    public RetType forAliasedSimpleNameOnly(AliasedSimpleName that, RetType name_result, Option<RetType> alias_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forAliasedAPINameOnly(AliasedAPIName that, RetType api_result, Option<RetType> alias_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forExportOnly(Export that, List<RetType> apis_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forTraitObjectAbsDeclOrDeclOnly(TraitObjectAbsDeclOrDecl that, List<RetType> mods_result, RetType name_result, List<RetType> staticParams_result, List<RetType> extendsClause_result, RetType where_result, List<RetType> decls_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forTraitAbsDeclOrDeclOnly(TraitAbsDeclOrDecl that, List<RetType> mods_result, RetType name_result, List<RetType> staticParams_result, List<RetType> extendsClause_result, RetType where_result, List<RetType> excludes_result, Option<List<RetType>> comprises_result, List<RetType> decls_result) {
        return forTraitObjectAbsDeclOrDeclOnly(that, mods_result, name_result, staticParams_result, extendsClause_result, where_result, decls_result);
    }

    public RetType forAbsTraitDeclOnly(AbsTraitDecl that, List<RetType> mods_result, RetType name_result, List<RetType> staticParams_result, List<RetType> extendsClause_result, RetType where_result, List<RetType> excludes_result, Option<List<RetType>> comprises_result, List<RetType> decls_result) {
        return forTraitAbsDeclOrDeclOnly(that, mods_result, name_result, staticParams_result, extendsClause_result, where_result, excludes_result, comprises_result, decls_result);
    }

    public RetType forTraitDeclOnly(TraitDecl that, List<RetType> mods_result, RetType name_result, List<RetType> staticParams_result, List<RetType> extendsClause_result, RetType where_result, List<RetType> excludes_result, Option<List<RetType>> comprises_result, List<RetType> decls_result) {
        return forTraitAbsDeclOrDeclOnly(that, mods_result, name_result, staticParams_result, extendsClause_result, where_result, excludes_result, comprises_result, decls_result);
    }

    public RetType forObjectAbsDeclOrDeclOnly(ObjectAbsDeclOrDecl that, List<RetType> mods_result, RetType name_result, List<RetType> staticParams_result, List<RetType> extendsClause_result, RetType where_result, Option<List<RetType>> params_result, Option<List<RetType>> throwsClause_result, RetType contract_result, List<RetType> decls_result) {
        return forTraitObjectAbsDeclOrDeclOnly(that, mods_result, name_result, staticParams_result, extendsClause_result, where_result, decls_result);
    }

    public RetType forAbsObjectDeclOnly(AbsObjectDecl that, List<RetType> mods_result, RetType name_result, List<RetType> staticParams_result, List<RetType> extendsClause_result, RetType where_result, Option<List<RetType>> params_result, Option<List<RetType>> throwsClause_result, RetType contract_result, List<RetType> decls_result) {
        return forObjectAbsDeclOrDeclOnly(that, mods_result, name_result, staticParams_result, extendsClause_result, where_result, params_result, throwsClause_result, contract_result, decls_result);
    }

    public RetType forObjectDeclOnly(ObjectDecl that, List<RetType> mods_result, RetType name_result, List<RetType> staticParams_result, List<RetType> extendsClause_result, RetType where_result, Option<List<RetType>> params_result, Option<List<RetType>> throwsClause_result, RetType contract_result, List<RetType> decls_result) {
        return forObjectAbsDeclOrDeclOnly(that, mods_result, name_result, staticParams_result, extendsClause_result, where_result, params_result, throwsClause_result, contract_result, decls_result);
    }

    public RetType forVarAbsDeclOrDeclOnly(VarAbsDeclOrDecl that, List<RetType> lhs_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forAbsVarDeclOnly(AbsVarDecl that, List<RetType> lhs_result) {
        return forVarAbsDeclOrDeclOnly(that, lhs_result);
    }

    public RetType forVarDeclOnly(VarDecl that, List<RetType> lhs_result, RetType init_result) {
        return forVarAbsDeclOrDeclOnly(that, lhs_result);
    }

    public RetType forLValueOnly(LValue that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forLValueBindOnly(LValueBind that, RetType name_result, Option<RetType> type_result, List<RetType> mods_result) {
        return forLValueOnly(that);
    }

    public RetType forUnpastingOnly(Unpasting that) {
        return forLValueOnly(that);
    }

    public RetType forUnpastingBindOnly(UnpastingBind that, RetType name_result, List<RetType> dim_result) {
        return forUnpastingOnly(that);
    }

    public RetType forUnpastingSplitOnly(UnpastingSplit that, List<RetType> elems_result) {
        return forUnpastingOnly(that);
    }

    public RetType forFnAbsDeclOrDeclOnly(FnAbsDeclOrDecl that, List<RetType> mods_result, RetType name_result, List<RetType> staticParams_result, List<RetType> params_result, Option<RetType> returnType_result, Option<List<RetType>> throwsClause_result, RetType where_result, RetType contract_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forAbsFnDeclOnly(AbsFnDecl that, List<RetType> mods_result, RetType name_result, List<RetType> staticParams_result, List<RetType> params_result, Option<RetType> returnType_result, Option<List<RetType>> throwsClause_result, RetType where_result, RetType contract_result) {
        return forFnAbsDeclOrDeclOnly(that, mods_result, name_result, staticParams_result, params_result, returnType_result, throwsClause_result, where_result, contract_result);
    }

    public RetType forFnDeclOnly(FnDecl that, List<RetType> mods_result, RetType name_result, List<RetType> staticParams_result, List<RetType> params_result, Option<RetType> returnType_result, Option<List<RetType>> throwsClause_result, RetType where_result, RetType contract_result) {
        return forFnAbsDeclOrDeclOnly(that, mods_result, name_result, staticParams_result, params_result, returnType_result, throwsClause_result, where_result, contract_result);
    }

    public RetType forFnDefOnly(FnDef that, List<RetType> mods_result, RetType name_result, List<RetType> staticParams_result, List<RetType> params_result, Option<RetType> returnType_result, Option<List<RetType>> throwsClause_result, RetType where_result, RetType contract_result, RetType body_result) {
        return forFnDeclOnly(that, mods_result, name_result, staticParams_result, params_result, returnType_result, throwsClause_result, where_result, contract_result);
    }

    public RetType forParamOnly(Param that, List<RetType> mods_result, RetType name_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forNormalParamOnly(NormalParam that, List<RetType> mods_result, RetType name_result, Option<RetType> type_result, Option<RetType> defaultExpr_result) {
        return forParamOnly(that, mods_result, name_result);
    }

    public RetType forVarargsParamOnly(VarargsParam that, List<RetType> mods_result, RetType name_result, RetType varargsType_result) {
        return forParamOnly(that, mods_result, name_result);
    }

    public RetType forDimUnitDeclOnly(DimUnitDecl that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forDimDeclOnly(DimDecl that, RetType dim_result, Option<RetType> derived_result, Option<RetType> default_result) {
        return forDimUnitDeclOnly(that);
    }

    public RetType forUnitDeclOnly(UnitDecl that, List<RetType> units_result, Option<RetType> dim_result, Option<RetType> def_result) {
        return forDimUnitDeclOnly(that);
    }

    public RetType forTestDeclOnly(TestDecl that, RetType name_result, List<RetType> gens_result, RetType expr_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forPropertyDeclOnly(PropertyDecl that, Option<RetType> name_result, List<RetType> params_result, RetType expr_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forExternalSyntaxAbsDeclOrDeclOnly(ExternalSyntaxAbsDeclOrDecl that, RetType openExpander_result, RetType name_result, RetType closeExpander_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forAbsExternalSyntaxOnly(AbsExternalSyntax that, RetType openExpander_result, RetType name_result, RetType closeExpander_result) {
        return forExternalSyntaxAbsDeclOrDeclOnly(that, openExpander_result, name_result, closeExpander_result);
    }

    public RetType forExternalSyntaxOnly(ExternalSyntax that, RetType openExpander_result, RetType name_result, RetType closeExpander_result, RetType expr_result) {
        return forExternalSyntaxAbsDeclOrDeclOnly(that, openExpander_result, name_result, closeExpander_result);
    }

    public RetType forGrammarDeclOnly(GrammarDecl that, RetType name_result, List<RetType> extends_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forGrammarDefOnly(GrammarDef that, RetType name_result, List<RetType> extends_result, List<RetType> members_result) {
        return forGrammarDeclOnly(that, name_result, extends_result);
    }

    public RetType forGrammarMemberDeclOnly(GrammarMemberDecl that, RetType name_result, Option<RetType> type_result, Option<RetType> modifier_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forNonterminalDeclOnly(NonterminalDecl that, RetType name_result, Option<RetType> type_result, Option<RetType> modifier_result, List<RetType> syntaxDefs_result) {
        return forGrammarMemberDeclOnly(that, name_result, type_result, modifier_result);
    }

    public RetType forNonterminalDefOnly(NonterminalDef that, RetType name_result, Option<RetType> type_result, Option<RetType> modifier_result, List<RetType> syntaxDefs_result) {
        return forNonterminalDeclOnly(that, name_result, type_result, modifier_result, syntaxDefs_result);
    }

    public RetType forNonterminalExtensionDefOnly(NonterminalExtensionDef that, RetType name_result, Option<RetType> type_result, Option<RetType> modifier_result, List<RetType> syntaxDefs_result) {
        return forNonterminalDeclOnly(that, name_result, type_result, modifier_result, syntaxDefs_result);
    }

    public RetType forTerminalDeclOnly(TerminalDecl that, RetType name_result, Option<RetType> type_result, Option<RetType> modifier_result, RetType syntaxDef_result) {
        return forGrammarMemberDeclOnly(that, name_result, type_result, modifier_result);
    }

    public RetType for_TerminalDefOnly(_TerminalDef that, RetType name_result, Option<RetType> type_result, Option<RetType> modifier_result, RetType syntaxDef_result) {
        return forTerminalDeclOnly(that, name_result, type_result, modifier_result, syntaxDef_result);
    }

    public RetType forSyntaxDeclOnly(SyntaxDecl that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forSyntaxDefOnly(SyntaxDef that, List<RetType> syntaxSymbols_result, RetType transformationExpression_result) {
        return forSyntaxDeclOnly(that);
    }

    public RetType forSyntaxSymbolOnly(SyntaxSymbol that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forPrefixedSymbolOnly(PrefixedSymbol that, Option<RetType> id_result, RetType symbol_result) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forOptionalSymbolOnly(OptionalSymbol that, RetType symbol_result) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forRepeatSymbolOnly(RepeatSymbol that, RetType symbol_result) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forRepeatOneOrMoreSymbolOnly(RepeatOneOrMoreSymbol that, RetType symbol_result) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forNoWhitespaceSymbolOnly(NoWhitespaceSymbol that, RetType symbol_result) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forSpecialSymbolOnly(SpecialSymbol that) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forWhitespaceSymbolOnly(WhitespaceSymbol that) {
        return forSpecialSymbolOnly(that);
    }

    public RetType forTabSymbolOnly(TabSymbol that) {
        return forSpecialSymbolOnly(that);
    }

    public RetType forFormfeedSymbolOnly(FormfeedSymbol that) {
        return forSpecialSymbolOnly(that);
    }

    public RetType forCarriageReturnSymbolOnly(CarriageReturnSymbol that) {
        return forSpecialSymbolOnly(that);
    }

    public RetType forBackspaceSymbolOnly(BackspaceSymbol that) {
        return forSpecialSymbolOnly(that);
    }

    public RetType forNewlineSymbolOnly(NewlineSymbol that) {
        return forSpecialSymbolOnly(that);
    }

    public RetType forBreaklineSymbolOnly(BreaklineSymbol that) {
        return forSpecialSymbolOnly(that);
    }

    public RetType forItemSymbolOnly(ItemSymbol that) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forNonterminalSymbolOnly(NonterminalSymbol that, RetType nonterminal_result) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forKeywordSymbolOnly(KeywordSymbol that) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forTokenSymbolOnly(TokenSymbol that) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forNotPredicateSymbolOnly(NotPredicateSymbol that, RetType symbol_result) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forAndPredicateSymbolOnly(AndPredicateSymbol that, RetType symbol_result) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forCharacterClassSymbolOnly(CharacterClassSymbol that, List<RetType> characters_result) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forCharacterSymbolOnly(CharacterSymbol that) {
        return forSyntaxSymbolOnly(that);
    }

    public RetType forCharSymbolOnly(CharSymbol that) {
        return forCharacterSymbolOnly(that);
    }

    public RetType forCharacterIntervalOnly(CharacterInterval that) {
        return forCharacterSymbolOnly(that);
    }

    public RetType forExprOnly(Expr that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forTypeAnnotatedExprOnly(TypeAnnotatedExpr that, RetType expr_result, RetType type_result) {
        return forExprOnly(that);
    }

    public RetType forAsExprOnly(AsExpr that, RetType expr_result, RetType type_result) {
        return forTypeAnnotatedExprOnly(that, expr_result, type_result);
    }

    public RetType forAsIfExprOnly(AsIfExpr that, RetType expr_result, RetType type_result) {
        return forTypeAnnotatedExprOnly(that, expr_result, type_result);
    }

    public RetType forAssignmentOnly(Assignment that, List<RetType> lhs_result, Option<RetType> opr_result, RetType rhs_result) {
        return forExprOnly(that);
    }

    public RetType forDelimitedExprOnly(DelimitedExpr that) {
        return forExprOnly(that);
    }

    public RetType forBlockOnly(Block that, List<RetType> exprs_result) {
        return forDelimitedExprOnly(that);
    }

    public RetType forCaseExprOnly(CaseExpr that, Option<RetType> param_result, Option<RetType> compare_result, List<RetType> clauses_result, Option<RetType> elseClause_result) {
        return forDelimitedExprOnly(that);
    }

    public RetType forDoOnly(Do that, List<RetType> fronts_result) {
        return forDelimitedExprOnly(that);
    }

    public RetType forForOnly(For that, List<RetType> gens_result, RetType body_result) {
        return forDelimitedExprOnly(that);
    }

    public RetType forIfOnly(If that, List<RetType> clauses_result, Option<RetType> elseClause_result) {
        return forDelimitedExprOnly(that);
    }

    public RetType forLabelOnly(Label that, RetType name_result, RetType body_result) {
        return forDelimitedExprOnly(that);
    }

    public RetType forAbstractObjectExprOnly(AbstractObjectExpr that, List<RetType> extendsClause_result, List<RetType> decls_result) {
        return forDelimitedExprOnly(that);
    }

    public RetType forObjectExprOnly(ObjectExpr that, List<RetType> extendsClause_result, List<RetType> decls_result) {
        return forAbstractObjectExprOnly(that, extendsClause_result, decls_result);
    }

    public RetType for_RewriteObjectExprOnly(_RewriteObjectExpr that, List<RetType> extendsClause_result, List<RetType> decls_result, List<RetType> staticParams_result, List<RetType> staticArgs_result, Option<List<RetType>> params_result) {
        return forAbstractObjectExprOnly(that, extendsClause_result, decls_result);
    }

    public RetType forTryOnly(Try that, RetType body_result, Option<RetType> catchClause_result, List<RetType> forbid_result, Option<RetType> finallyClause_result) {
        return forDelimitedExprOnly(that);
    }

    public RetType forAbstractTupleExprOnly(AbstractTupleExpr that, List<RetType> exprs_result) {
        return forDelimitedExprOnly(that);
    }

    public RetType forTupleExprOnly(TupleExpr that, List<RetType> exprs_result) {
        return forAbstractTupleExprOnly(that, exprs_result);
    }

    public RetType forArgExprOnly(ArgExpr that, List<RetType> exprs_result, Option<RetType> varargs_result, List<RetType> keywords_result) {
        return forAbstractTupleExprOnly(that, exprs_result);
    }

    public RetType forTypecaseOnly(Typecase that, List<RetType> clauses_result, Option<RetType> elseClause_result) {
        return forDelimitedExprOnly(that);
    }

    public RetType forWhileOnly(While that, RetType test_result, RetType body_result) {
        return forDelimitedExprOnly(that);
    }

    public RetType forFlowExprOnly(FlowExpr that) {
        return forExprOnly(that);
    }

    public RetType forBigOprAppOnly(BigOprApp that, List<RetType> staticArgs_result) {
        return forFlowExprOnly(that);
    }

    public RetType forAccumulatorOnly(Accumulator that, List<RetType> staticArgs_result, RetType opr_result, List<RetType> gens_result, RetType body_result) {
        return forBigOprAppOnly(that, staticArgs_result);
    }

    public RetType forArrayComprehensionOnly(ArrayComprehension that, List<RetType> staticArgs_result, List<RetType> clauses_result) {
        return forBigOprAppOnly(that, staticArgs_result);
    }

    public RetType forAtomicExprOnly(AtomicExpr that, RetType expr_result) {
        return forFlowExprOnly(that);
    }

    public RetType forExitOnly(Exit that, Option<RetType> target_result, Option<RetType> returnExpr_result) {
        return forFlowExprOnly(that);
    }

    public RetType forSpawnOnly(Spawn that, RetType body_result) {
        return forFlowExprOnly(that);
    }

    public RetType forThrowOnly(Throw that, RetType expr_result) {
        return forFlowExprOnly(that);
    }

    public RetType forTryAtomicExprOnly(TryAtomicExpr that, RetType expr_result) {
        return forFlowExprOnly(that);
    }

    public RetType forFnExprOnly(FnExpr that, RetType name_result, List<RetType> staticParams_result, List<RetType> params_result, Option<RetType> returnType_result, RetType where_result, Option<List<RetType>> throwsClause_result, RetType body_result) {
        return forExprOnly(that);
    }

    public RetType forLetExprOnly(LetExpr that, List<RetType> body_result) {
        return forExprOnly(that);
    }

    public RetType forLetFnOnly(LetFn that, List<RetType> body_result, List<RetType> fns_result) {
        return forLetExprOnly(that, body_result);
    }

    public RetType forLocalVarDeclOnly(LocalVarDecl that, List<RetType> body_result, List<RetType> lhs_result, Option<RetType> rhs_result) {
        return forLetExprOnly(that, body_result);
    }

    public RetType forGeneratedExprOnly(GeneratedExpr that, RetType expr_result, List<RetType> gens_result) {
        return forExprOnly(that);
    }

    public RetType forOpExprOnly(OpExpr that) {
        return forExprOnly(that);
    }

    public RetType forSubscriptExprOnly(SubscriptExpr that, RetType obj_result, List<RetType> subs_result, Option<RetType> op_result) {
        return forOpExprOnly(that);
    }

    public RetType forPrimaryOnly(Primary that) {
        return forOpExprOnly(that);
    }

    public RetType forLiteralExprOnly(LiteralExpr that) {
        return forPrimaryOnly(that);
    }

    public RetType forNumberLiteralExprOnly(NumberLiteralExpr that) {
        return forLiteralExprOnly(that);
    }

    public RetType forFloatLiteralExprOnly(FloatLiteralExpr that) {
        return forNumberLiteralExprOnly(that);
    }

    public RetType forIntLiteralExprOnly(IntLiteralExpr that) {
        return forNumberLiteralExprOnly(that);
    }

    public RetType forCharLiteralExprOnly(CharLiteralExpr that) {
        return forLiteralExprOnly(that);
    }

    public RetType forStringLiteralExprOnly(StringLiteralExpr that) {
        return forLiteralExprOnly(that);
    }

    public RetType forVoidLiteralExprOnly(VoidLiteralExpr that) {
        return forLiteralExprOnly(that);
    }

    public RetType forVarRefOnly(VarRef that, RetType var_result) {
        return forPrimaryOnly(that);
    }

    public RetType forAbstractFieldRefOnly(AbstractFieldRef that, RetType obj_result) {
        return forPrimaryOnly(that);
    }

    public RetType forFieldRefOnly(FieldRef that, RetType obj_result, RetType field_result) {
        return forAbstractFieldRefOnly(that, obj_result);
    }

    public RetType forFieldRefForSureOnly(FieldRefForSure that, RetType obj_result, RetType field_result) {
        return forAbstractFieldRefOnly(that, obj_result);
    }

    public RetType for_RewriteFieldRefOnly(_RewriteFieldRef that, RetType obj_result, RetType field_result) {
        return forAbstractFieldRefOnly(that, obj_result);
    }

    public RetType forFunctionalRefOnly(FunctionalRef that, List<RetType> staticArgs_result) {
        return forPrimaryOnly(that);
    }

    public RetType forFnRefOnly(FnRef that, List<RetType> fns_result, List<RetType> staticArgs_result) {
        return forFunctionalRefOnly(that, staticArgs_result);
    }

    public RetType for_RewriteFnRefOnly(_RewriteFnRef that, RetType fn_result, List<RetType> staticArgs_result) {
        return forFunctionalRefOnly(that, staticArgs_result);
    }

    public RetType forOpRefOnly(OpRef that, List<RetType> ops_result, List<RetType> staticArgs_result) {
        return forFunctionalRefOnly(that, staticArgs_result);
    }

    public RetType forAppExprOnly(AppExpr that) {
        return forPrimaryOnly(that);
    }

    public RetType forJuxtOnly(Juxt that, List<RetType> exprs_result) {
        return forAppExprOnly(that);
    }

    public RetType forLooseJuxtOnly(LooseJuxt that, List<RetType> exprs_result) {
        return forJuxtOnly(that, exprs_result);
    }

    public RetType forTightJuxtOnly(TightJuxt that, List<RetType> exprs_result) {
        return forJuxtOnly(that, exprs_result);
    }

    public RetType forOprExprOnly(OprExpr that, RetType op_result, List<RetType> args_result) {
        return forAppExprOnly(that);
    }

    public RetType forChainExprOnly(ChainExpr that, RetType first_result) {
        return forAppExprOnly(that);
    }

    public RetType forCoercionInvocationOnly(CoercionInvocation that, RetType type_result, List<RetType> staticArgs_result, RetType arg_result) {
        return forAppExprOnly(that);
    }

    public RetType forMethodInvocationOnly(MethodInvocation that, RetType obj_result, RetType method_result, List<RetType> staticArgs_result, RetType arg_result) {
        return forAppExprOnly(that);
    }

    public RetType forMathPrimaryOnly(MathPrimary that, RetType front_result, List<RetType> rest_result) {
        return forPrimaryOnly(that);
    }

    public RetType forArrayExprOnly(ArrayExpr that) {
        return forPrimaryOnly(that);
    }

    public RetType forArrayElementOnly(ArrayElement that, RetType element_result) {
        return forArrayExprOnly(that);
    }

    public RetType forArrayElementsOnly(ArrayElements that, List<RetType> elements_result) {
        return forArrayExprOnly(that);
    }

    public RetType forTypeOnly(Type that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forDimExprOnly(DimExpr that) {
        return forTypeOnly(that);
    }

    public RetType forExponentTypeOnly(ExponentType that, RetType base_result, RetType power_result) {
        return forDimExprOnly(that);
    }

    public RetType forBaseDimOnly(BaseDim that) {
        return forDimExprOnly(that);
    }

    public RetType forDimRefOnly(DimRef that, RetType name_result) {
        return forDimExprOnly(that);
    }

    public RetType forProductDimOnly(ProductDim that, RetType multiplier_result, RetType multiplicand_result) {
        return forDimExprOnly(that);
    }

    public RetType forQuotientDimOnly(QuotientDim that, RetType numerator_result, RetType denominator_result) {
        return forDimExprOnly(that);
    }

    public RetType forExponentDimOnly(ExponentDim that, RetType base_result, RetType power_result) {
        return forDimExprOnly(that);
    }

    public RetType forOpDimOnly(OpDim that, RetType val_result, RetType op_result) {
        return forDimExprOnly(that);
    }

    public RetType forAbstractArrowTypeOnly(AbstractArrowType that, RetType domain_result, RetType range_result, Option<List<RetType>> throwsClause_result) {
        return forTypeOnly(that);
    }

    public RetType forArrowTypeOnly(ArrowType that, RetType domain_result, RetType range_result, Option<List<RetType>> throwsClause_result) {
        return forAbstractArrowTypeOnly(that, domain_result, range_result, throwsClause_result);
    }

    public RetType for_RewriteGenericArrowTypeOnly(_RewriteGenericArrowType that, RetType domain_result, RetType range_result, Option<List<RetType>> throwsClause_result, List<RetType> staticParams_result, RetType where_result) {
        return forAbstractArrowTypeOnly(that, domain_result, range_result, throwsClause_result);
    }

    public RetType forNonArrowTypeOnly(NonArrowType that) {
        return forTypeOnly(that);
    }

    public RetType forBottomTypeOnly(BottomType that) {
        return forNonArrowTypeOnly(that);
    }

    public RetType forTraitTypeOnly(TraitType that) {
        return forNonArrowTypeOnly(that);
    }

    public RetType forNamedTypeOnly(NamedType that, RetType name_result) {
        return forTraitTypeOnly(that);
    }

    public RetType forIdTypeOnly(IdType that, RetType name_result) {
        return forNamedTypeOnly(that, name_result);
    }

    public RetType forInstantiatedTypeOnly(InstantiatedType that, RetType name_result, List<RetType> args_result) {
        return forNamedTypeOnly(that, name_result);
    }

    public RetType forAbbreviatedTypeOnly(AbbreviatedType that, RetType element_result) {
        return forTraitTypeOnly(that);
    }

    public RetType forArrayTypeOnly(ArrayType that, RetType element_result, RetType indices_result) {
        return forAbbreviatedTypeOnly(that, element_result);
    }

    public RetType forMatrixTypeOnly(MatrixType that, RetType element_result, List<RetType> dimensions_result) {
        return forAbbreviatedTypeOnly(that, element_result);
    }

    public RetType forAbstractTupleTypeOnly(AbstractTupleType that, List<RetType> elements_result) {
        return forNonArrowTypeOnly(that);
    }

    public RetType forTupleTypeOnly(TupleType that, List<RetType> elements_result) {
        return forAbstractTupleTypeOnly(that, elements_result);
    }

    public RetType forArgTypeOnly(ArgType that, List<RetType> elements_result, Option<RetType> varargs_result, List<RetType> keywords_result) {
        return forAbstractTupleTypeOnly(that, elements_result);
    }

    public RetType forVoidTypeOnly(VoidType that) {
        return forNonArrowTypeOnly(that);
    }

    public RetType forInferenceVarTypeOnly(InferenceVarType that) {
        return forNonArrowTypeOnly(that);
    }

    public RetType forAndTypeOnly(AndType that, RetType first_result, RetType second_result) {
        return forNonArrowTypeOnly(that);
    }

    public RetType forOrTypeOnly(OrType that, RetType first_result, RetType second_result) {
        return forNonArrowTypeOnly(that);
    }

    public RetType forFixedPointTypeOnly(FixedPointType that, RetType name_result, RetType body_result) {
        return forNonArrowTypeOnly(that);
    }

    public RetType forDimTypeOnly(DimType that, RetType type_result) {
        return forNonArrowTypeOnly(that);
    }

    public RetType forTaggedDimTypeOnly(TaggedDimType that, RetType type_result, RetType dim_result, Option<RetType> unit_result) {
        return forDimTypeOnly(that, type_result);
    }

    public RetType forTaggedUnitTypeOnly(TaggedUnitType that, RetType type_result, RetType unit_result) {
        return forDimTypeOnly(that, type_result);
    }

    public RetType forStaticArgOnly(StaticArg that) {
        return forTypeOnly(that);
    }

    public RetType forIdArgOnly(IdArg that, RetType name_result) {
        return forStaticArgOnly(that);
    }

    public RetType forTypeArgOnly(TypeArg that, RetType type_result) {
        return forStaticArgOnly(that);
    }

    public RetType forIntArgOnly(IntArg that, RetType val_result) {
        return forStaticArgOnly(that);
    }

    public RetType forBoolArgOnly(BoolArg that, RetType bool_result) {
        return forStaticArgOnly(that);
    }

    public RetType forOprArgOnly(OprArg that, RetType name_result) {
        return forStaticArgOnly(that);
    }

    public RetType forDimArgOnly(DimArg that, RetType dim_result) {
        return forStaticArgOnly(that);
    }

    public RetType forUnitArgOnly(UnitArg that, RetType unit_result) {
        return forStaticArgOnly(that);
    }

    public RetType forStaticExprOnly(StaticExpr that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forIntExprOnly(IntExpr that) {
        return forStaticExprOnly(that);
    }

    public RetType forIntValOnly(IntVal that) {
        return forIntExprOnly(that);
    }

    public RetType forNumberConstraintOnly(NumberConstraint that, RetType val_result) {
        return forIntValOnly(that);
    }

    public RetType forIntRefOnly(IntRef that, RetType name_result) {
        return forIntValOnly(that);
    }

    public RetType forIntOpExprOnly(IntOpExpr that, RetType left_result, RetType right_result) {
        return forIntExprOnly(that);
    }

    public RetType forSumConstraintOnly(SumConstraint that, RetType left_result, RetType right_result) {
        return forIntOpExprOnly(that, left_result, right_result);
    }

    public RetType forMinusConstraintOnly(MinusConstraint that, RetType left_result, RetType right_result) {
        return forIntOpExprOnly(that, left_result, right_result);
    }

    public RetType forProductConstraintOnly(ProductConstraint that, RetType left_result, RetType right_result) {
        return forIntOpExprOnly(that, left_result, right_result);
    }

    public RetType forExponentConstraintOnly(ExponentConstraint that, RetType left_result, RetType right_result) {
        return forIntOpExprOnly(that, left_result, right_result);
    }

    public RetType forBoolExprOnly(BoolExpr that) {
        return forStaticExprOnly(that);
    }

    public RetType forBoolValOnly(BoolVal that) {
        return forBoolExprOnly(that);
    }

    public RetType forBoolConstantOnly(BoolConstant that) {
        return forBoolValOnly(that);
    }

    public RetType forBoolRefOnly(BoolRef that, RetType name_result) {
        return forBoolValOnly(that);
    }

    public RetType forBoolConstraintOnly(BoolConstraint that) {
        return forBoolExprOnly(that);
    }

    public RetType forNotConstraintOnly(NotConstraint that, RetType bool_result) {
        return forBoolConstraintOnly(that);
    }

    public RetType forBinaryBoolConstraintOnly(BinaryBoolConstraint that, RetType left_result, RetType right_result) {
        return forBoolConstraintOnly(that);
    }

    public RetType forOrConstraintOnly(OrConstraint that, RetType left_result, RetType right_result) {
        return forBinaryBoolConstraintOnly(that, left_result, right_result);
    }

    public RetType forAndConstraintOnly(AndConstraint that, RetType left_result, RetType right_result) {
        return forBinaryBoolConstraintOnly(that, left_result, right_result);
    }

    public RetType forImpliesConstraintOnly(ImpliesConstraint that, RetType left_result, RetType right_result) {
        return forBinaryBoolConstraintOnly(that, left_result, right_result);
    }

    public RetType forBEConstraintOnly(BEConstraint that, RetType left_result, RetType right_result) {
        return forBinaryBoolConstraintOnly(that, left_result, right_result);
    }

    public RetType forWhereClauseOnly(WhereClause that, List<RetType> bindings_result, List<RetType> constraints_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forWhereBindingOnly(WhereBinding that, RetType name_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forWhereTypeOnly(WhereType that, RetType name_result, List<RetType> supers_result) {
        return forWhereBindingOnly(that, name_result);
    }

    public RetType forWhereNatOnly(WhereNat that, RetType name_result) {
        return forWhereBindingOnly(that, name_result);
    }

    public RetType forWhereIntOnly(WhereInt that, RetType name_result) {
        return forWhereBindingOnly(that, name_result);
    }

    public RetType forWhereBoolOnly(WhereBool that, RetType name_result) {
        return forWhereBindingOnly(that, name_result);
    }

    public RetType forWhereUnitOnly(WhereUnit that, RetType name_result) {
        return forWhereBindingOnly(that, name_result);
    }

    public RetType forWhereConstraintOnly(WhereConstraint that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forWhereExtendsOnly(WhereExtends that, RetType name_result, List<RetType> supers_result) {
        return forWhereConstraintOnly(that);
    }

    public RetType forTypeAliasOnly(TypeAlias that, RetType name_result, List<RetType> staticParams_result, RetType type_result) {
        return forWhereConstraintOnly(that);
    }

    public RetType forWhereCoercesOnly(WhereCoerces that, RetType left_result, RetType right_result) {
        return forWhereConstraintOnly(that);
    }

    public RetType forWhereWidensOnly(WhereWidens that, RetType left_result, RetType right_result) {
        return forWhereConstraintOnly(that);
    }

    public RetType forWhereWidensCoercesOnly(WhereWidensCoerces that, RetType left_result, RetType right_result) {
        return forWhereConstraintOnly(that);
    }

    public RetType forWhereEqualsOnly(WhereEquals that, RetType left_result, RetType right_result) {
        return forWhereConstraintOnly(that);
    }

    public RetType forUnitConstraintOnly(UnitConstraint that, RetType name_result) {
        return forWhereConstraintOnly(that);
    }

    public RetType forIntConstraintOnly(IntConstraint that, RetType left_result, RetType right_result) {
        return forWhereConstraintOnly(that);
    }

    public RetType forLEConstraintOnly(LEConstraint that, RetType left_result, RetType right_result) {
        return forIntConstraintOnly(that, left_result, right_result);
    }

    public RetType forLTConstraintOnly(LTConstraint that, RetType left_result, RetType right_result) {
        return forIntConstraintOnly(that, left_result, right_result);
    }

    public RetType forGEConstraintOnly(GEConstraint that, RetType left_result, RetType right_result) {
        return forIntConstraintOnly(that, left_result, right_result);
    }

    public RetType forGTConstraintOnly(GTConstraint that, RetType left_result, RetType right_result) {
        return forIntConstraintOnly(that, left_result, right_result);
    }

    public RetType forIEConstraintOnly(IEConstraint that, RetType left_result, RetType right_result) {
        return forIntConstraintOnly(that, left_result, right_result);
    }

    public RetType forBoolConstraintExprOnly(BoolConstraintExpr that, RetType constraint_result) {
        return forWhereConstraintOnly(that);
    }

    public RetType forContractOnly(Contract that, Option<List<RetType>> requires_result, Option<List<RetType>> ensures_result, Option<List<RetType>> invariants_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forEnsuresClauseOnly(EnsuresClause that, RetType post_result, Option<RetType> pre_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forModifierOnly(Modifier that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forModifierAbstractOnly(ModifierAbstract that) {
        return forModifierOnly(that);
    }

    public RetType forModifierAtomicOnly(ModifierAtomic that) {
        return forModifierOnly(that);
    }

    public RetType forModifierGetterOnly(ModifierGetter that) {
        return forModifierOnly(that);
    }

    public RetType forModifierHiddenOnly(ModifierHidden that) {
        return forModifierOnly(that);
    }

    public RetType forModifierIOOnly(ModifierIO that) {
        return forModifierOnly(that);
    }

    public RetType forModifierOverrideOnly(ModifierOverride that) {
        return forModifierOnly(that);
    }

    public RetType forModifierPrivateOnly(ModifierPrivate that) {
        return forModifierOnly(that);
    }

    public RetType forModifierSettableOnly(ModifierSettable that) {
        return forModifierOnly(that);
    }

    public RetType forModifierSetterOnly(ModifierSetter that) {
        return forModifierOnly(that);
    }

    public RetType forModifierTestOnly(ModifierTest that) {
        return forModifierOnly(that);
    }

    public RetType forModifierTransientOnly(ModifierTransient that) {
        return forModifierOnly(that);
    }

    public RetType forModifierValueOnly(ModifierValue that) {
        return forModifierOnly(that);
    }

    public RetType forModifierVarOnly(ModifierVar that) {
        return forModifierOnly(that);
    }

    public RetType forModifierWidensOnly(ModifierWidens that) {
        return forModifierOnly(that);
    }

    public RetType forModifierWrappedOnly(ModifierWrapped that) {
        return forModifierOnly(that);
    }

    public RetType forStaticParamOnly(StaticParam that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forOperatorParamOnly(OperatorParam that, RetType name_result) {
        return forStaticParamOnly(that);
    }

    public RetType forIdStaticParamOnly(IdStaticParam that, RetType name_result) {
        return forStaticParamOnly(that);
    }

    public RetType forBoolParamOnly(BoolParam that, RetType name_result) {
        return forIdStaticParamOnly(that, name_result);
    }

    public RetType forDimensionParamOnly(DimensionParam that, RetType name_result) {
        return forIdStaticParamOnly(that, name_result);
    }

    public RetType forIntParamOnly(IntParam that, RetType name_result) {
        return forIdStaticParamOnly(that, name_result);
    }

    public RetType forNatParamOnly(NatParam that, RetType name_result) {
        return forIdStaticParamOnly(that, name_result);
    }

    public RetType forSimpleTypeParamOnly(SimpleTypeParam that, RetType name_result, List<RetType> extendsClause_result) {
        return forIdStaticParamOnly(that, name_result);
    }

    public RetType forUnitParamOnly(UnitParam that, RetType name_result, Option<RetType> dim_result) {
        return forIdStaticParamOnly(that, name_result);
    }

    public RetType forNameOnly(Name that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forAPINameOnly(APIName that, List<RetType> ids_result) {
        return forNameOnly(that);
    }

    public RetType forQualifiedNameOnly(QualifiedName that, Option<RetType> api_result, RetType name_result) {
        return forNameOnly(that);
    }

    public RetType forQualifiedIdNameOnly(QualifiedIdName that, Option<RetType> api_result, RetType name_result) {
        return forQualifiedNameOnly(that, api_result, name_result);
    }

    public RetType forQualifiedOpNameOnly(QualifiedOpName that, Option<RetType> api_result, RetType name_result) {
        return forQualifiedNameOnly(that, api_result, name_result);
    }

    public RetType forSimpleNameOnly(SimpleName that) {
        return forNameOnly(that);
    }

    public RetType forIdOnly(Id that) {
        return forSimpleNameOnly(that);
    }

    public RetType forOpNameOnly(OpName that) {
        return forSimpleNameOnly(that);
    }

    public RetType forOpOnly(Op that, Option<RetType> fixity_result) {
        return forOpNameOnly(that);
    }

    public RetType forEnclosingOnly(Enclosing that, RetType open_result, RetType close_result) {
        return forOpNameOnly(that);
    }

    public RetType forAnonymousFnNameOnly(AnonymousFnName that) {
        return forSimpleNameOnly(that);
    }

    public RetType forConstructorFnNameOnly(ConstructorFnName that, RetType def_result) {
        return forSimpleNameOnly(that);
    }

    public RetType forArrayComprehensionClauseOnly(ArrayComprehensionClause that, List<RetType> bind_result, RetType init_result, List<RetType> gens_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forKeywordExprOnly(KeywordExpr that, RetType name_result, RetType init_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forCaseClauseOnly(CaseClause that, RetType match_result, RetType body_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forCatchOnly(Catch that, RetType name_result, List<RetType> clauses_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forCatchClauseOnly(CatchClause that, RetType match_result, RetType body_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forDoFrontOnly(DoFront that, Option<RetType> loc_result, RetType expr_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forIfClauseOnly(IfClause that, RetType test_result, RetType body_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forTypecaseClauseOnly(TypecaseClause that, List<RetType> match_result, RetType body_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forExtentRangeOnly(ExtentRange that, Option<RetType> base_result, Option<RetType> size_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forGeneratorClauseOnly(GeneratorClause that, List<RetType> bind_result, RetType init_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forVarargsExprOnly(VarargsExpr that, RetType varargs_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forVarargsTypeOnly(VarargsType that, RetType type_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forKeywordTypeOnly(KeywordType that, RetType name_result, RetType type_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forTraitTypeWhereOnly(TraitTypeWhere that, RetType type_result, RetType where_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forIndicesOnly(Indices that, List<RetType> extents_result) {
        return forAbstractNodeOnly(that);
    }

    public RetType forMathItemOnly(MathItem that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forExprMIOnly(ExprMI that, RetType expr_result) {
        return forMathItemOnly(that);
    }

    public RetType forParenthesisDelimitedMIOnly(ParenthesisDelimitedMI that, RetType expr_result) {
        return forExprMIOnly(that, expr_result);
    }

    public RetType forNonParenthesisDelimitedMIOnly(NonParenthesisDelimitedMI that, RetType expr_result) {
        return forExprMIOnly(that, expr_result);
    }

    public RetType forNonExprMIOnly(NonExprMI that) {
        return forMathItemOnly(that);
    }

    public RetType forExponentiationMIOnly(ExponentiationMI that, RetType op_result, Option<RetType> expr_result) {
        return forNonExprMIOnly(that);
    }

    public RetType forSubscriptingMIOnly(SubscriptingMI that, RetType op_result, List<RetType> exprs_result) {
        return forNonExprMIOnly(that);
    }

    public RetType forFixityOnly(Fixity that) {
        return forAbstractNodeOnly(that);
    }

    public RetType forInFixityOnly(InFixity that) {
        return forFixityOnly(that);
    }

    public RetType forPreFixityOnly(PreFixity that) {
        return forFixityOnly(that);
    }

    public RetType forPostFixityOnly(PostFixity that) {
        return forFixityOnly(that);
    }

    public RetType forNoFixityOnly(NoFixity that) {
        return forFixityOnly(that);
    }

    public RetType forMultiFixityOnly(MultiFixity that) {
        return forFixityOnly(that);
    }

    public RetType forEnclosingFixityOnly(EnclosingFixity that) {
        return forFixityOnly(that);
    }

    public RetType forBigFixityOnly(BigFixity that) {
        return forFixityOnly(that);
    }


    /** Methods to recur on each child. */
    public RetType forComponent(Component that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> imports_result = recurOnListOfImport(that.getImports());
        List<RetType> exports_result = recurOnListOfExport(that.getExports());
        List<RetType> decls_result = recurOnListOfDecl(that.getDecls());
        return forComponentOnly(that, name_result, imports_result, exports_result, decls_result);
    }

    public RetType forApi(Api that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> imports_result = recurOnListOfImport(that.getImports());
        List<RetType> decls_result = recurOnListOfAbsDecl(that.getDecls());
        return forApiOnly(that, name_result, imports_result, decls_result);
    }

    public RetType forImportStar(ImportStar that) {
        RetType api_result = that.getApi().accept(this);
        List<RetType> except_result = recurOnListOfSimpleName(that.getExcept());
        return forImportStarOnly(that, api_result, except_result);
    }

    public RetType forImportNames(ImportNames that) {
        RetType api_result = that.getApi().accept(this);
        List<RetType> aliasedNames_result = recurOnListOfAliasedSimpleName(that.getAliasedNames());
        return forImportNamesOnly(that, api_result, aliasedNames_result);
    }

    public RetType forImportApi(ImportApi that) {
        List<RetType> apis_result = recurOnListOfAliasedAPIName(that.getApis());
        return forImportApiOnly(that, apis_result);
    }

    public RetType forAliasedSimpleName(AliasedSimpleName that) {
        RetType name_result = that.getName().accept(this);
        Option<RetType> alias_result = recurOnOptionOfSimpleName(that.getAlias());
        return forAliasedSimpleNameOnly(that, name_result, alias_result);
    }

    public RetType forAliasedAPIName(AliasedAPIName that) {
        RetType api_result = that.getApi().accept(this);
        Option<RetType> alias_result = recurOnOptionOfId(that.getAlias());
        return forAliasedAPINameOnly(that, api_result, alias_result);
    }

    public RetType forExport(Export that) {
        List<RetType> apis_result = recurOnListOfAPIName(that.getApis());
        return forExportOnly(that, apis_result);
    }

    public RetType forAbsTraitDecl(AbsTraitDecl that) {
        List<RetType> mods_result = recurOnListOfModifier(that.getMods());
        RetType name_result = that.getName().accept(this);
        List<RetType> staticParams_result = recurOnListOfStaticParam(that.getStaticParams());
        List<RetType> extendsClause_result = recurOnListOfTraitTypeWhere(that.getExtendsClause());
        RetType where_result = that.getWhere().accept(this);
        List<RetType> excludes_result = recurOnListOfTraitType(that.getExcludes());
        Option<List<RetType>> comprises_result = recurOnOptionOfListOfTraitType(that.getComprises());
        List<RetType> decls_result = recurOnListOfAbsDecl(that.getDecls());
        return forAbsTraitDeclOnly(that, mods_result, name_result, staticParams_result, extendsClause_result, where_result, excludes_result, comprises_result, decls_result);
    }

    public RetType forTraitDecl(TraitDecl that) {
        List<RetType> mods_result = recurOnListOfModifier(that.getMods());
        RetType name_result = that.getName().accept(this);
        List<RetType> staticParams_result = recurOnListOfStaticParam(that.getStaticParams());
        List<RetType> extendsClause_result = recurOnListOfTraitTypeWhere(that.getExtendsClause());
        RetType where_result = that.getWhere().accept(this);
        List<RetType> excludes_result = recurOnListOfTraitType(that.getExcludes());
        Option<List<RetType>> comprises_result = recurOnOptionOfListOfTraitType(that.getComprises());
        List<RetType> decls_result = recurOnListOfDecl(that.getDecls());
        return forTraitDeclOnly(that, mods_result, name_result, staticParams_result, extendsClause_result, where_result, excludes_result, comprises_result, decls_result);
    }

    public RetType forAbsObjectDecl(AbsObjectDecl that) {
        List<RetType> mods_result = recurOnListOfModifier(that.getMods());
        RetType name_result = that.getName().accept(this);
        List<RetType> staticParams_result = recurOnListOfStaticParam(that.getStaticParams());
        List<RetType> extendsClause_result = recurOnListOfTraitTypeWhere(that.getExtendsClause());
        RetType where_result = that.getWhere().accept(this);
        Option<List<RetType>> params_result = recurOnOptionOfListOfParam(that.getParams());
        Option<List<RetType>> throwsClause_result = recurOnOptionOfListOfTraitType(that.getThrowsClause());
        RetType contract_result = that.getContract().accept(this);
        List<RetType> decls_result = recurOnListOfAbsDecl(that.getDecls());
        return forAbsObjectDeclOnly(that, mods_result, name_result, staticParams_result, extendsClause_result, where_result, params_result, throwsClause_result, contract_result, decls_result);
    }

    public RetType forObjectDecl(ObjectDecl that) {
        List<RetType> mods_result = recurOnListOfModifier(that.getMods());
        RetType name_result = that.getName().accept(this);
        List<RetType> staticParams_result = recurOnListOfStaticParam(that.getStaticParams());
        List<RetType> extendsClause_result = recurOnListOfTraitTypeWhere(that.getExtendsClause());
        RetType where_result = that.getWhere().accept(this);
        Option<List<RetType>> params_result = recurOnOptionOfListOfParam(that.getParams());
        Option<List<RetType>> throwsClause_result = recurOnOptionOfListOfTraitType(that.getThrowsClause());
        RetType contract_result = that.getContract().accept(this);
        List<RetType> decls_result = recurOnListOfDecl(that.getDecls());
        return forObjectDeclOnly(that, mods_result, name_result, staticParams_result, extendsClause_result, where_result, params_result, throwsClause_result, contract_result, decls_result);
    }

    public RetType forAbsVarDecl(AbsVarDecl that) {
        List<RetType> lhs_result = recurOnListOfLValueBind(that.getLhs());
        return forAbsVarDeclOnly(that, lhs_result);
    }

    public RetType forVarDecl(VarDecl that) {
        List<RetType> lhs_result = recurOnListOfLValueBind(that.getLhs());
        RetType init_result = that.getInit().accept(this);
        return forVarDeclOnly(that, lhs_result, init_result);
    }

    public RetType forLValueBind(LValueBind that) {
        RetType name_result = that.getName().accept(this);
        Option<RetType> type_result = recurOnOptionOfType(that.getType());
        List<RetType> mods_result = recurOnListOfModifier(that.getMods());
        return forLValueBindOnly(that, name_result, type_result, mods_result);
    }

    public RetType forUnpastingBind(UnpastingBind that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> dim_result = recurOnListOfExtentRange(that.getDim());
        return forUnpastingBindOnly(that, name_result, dim_result);
    }

    public RetType forUnpastingSplit(UnpastingSplit that) {
        List<RetType> elems_result = recurOnListOfUnpasting(that.getElems());
        return forUnpastingSplitOnly(that, elems_result);
    }

    public RetType forAbsFnDecl(AbsFnDecl that) {
        List<RetType> mods_result = recurOnListOfModifier(that.getMods());
        RetType name_result = that.getName().accept(this);
        List<RetType> staticParams_result = recurOnListOfStaticParam(that.getStaticParams());
        List<RetType> params_result = recurOnListOfParam(that.getParams());
        Option<RetType> returnType_result = recurOnOptionOfType(that.getReturnType());
        Option<List<RetType>> throwsClause_result = recurOnOptionOfListOfTraitType(that.getThrowsClause());
        RetType where_result = that.getWhere().accept(this);
        RetType contract_result = that.getContract().accept(this);
        return forAbsFnDeclOnly(that, mods_result, name_result, staticParams_result, params_result, returnType_result, throwsClause_result, where_result, contract_result);
    }

    public RetType forFnDef(FnDef that) {
        List<RetType> mods_result = recurOnListOfModifier(that.getMods());
        RetType name_result = that.getName().accept(this);
        List<RetType> staticParams_result = recurOnListOfStaticParam(that.getStaticParams());
        List<RetType> params_result = recurOnListOfParam(that.getParams());
        Option<RetType> returnType_result = recurOnOptionOfType(that.getReturnType());
        Option<List<RetType>> throwsClause_result = recurOnOptionOfListOfTraitType(that.getThrowsClause());
        RetType where_result = that.getWhere().accept(this);
        RetType contract_result = that.getContract().accept(this);
        RetType body_result = that.getBody().accept(this);
        return forFnDefOnly(that, mods_result, name_result, staticParams_result, params_result, returnType_result, throwsClause_result, where_result, contract_result, body_result);
    }

    public RetType forNormalParam(NormalParam that) {
        List<RetType> mods_result = recurOnListOfModifier(that.getMods());
        RetType name_result = that.getName().accept(this);
        Option<RetType> type_result = recurOnOptionOfType(that.getType());
        Option<RetType> defaultExpr_result = recurOnOptionOfExpr(that.getDefaultExpr());
        return forNormalParamOnly(that, mods_result, name_result, type_result, defaultExpr_result);
    }

    public RetType forVarargsParam(VarargsParam that) {
        List<RetType> mods_result = recurOnListOfModifier(that.getMods());
        RetType name_result = that.getName().accept(this);
        RetType varargsType_result = that.getVarargsType().accept(this);
        return forVarargsParamOnly(that, mods_result, name_result, varargsType_result);
    }

    public RetType forDimDecl(DimDecl that) {
        RetType dim_result = that.getDim().accept(this);
        Option<RetType> derived_result = recurOnOptionOfType(that.getDerived());
        Option<RetType> default_result = recurOnOptionOfId(that.getDefault());
        return forDimDeclOnly(that, dim_result, derived_result, default_result);
    }

    public RetType forUnitDecl(UnitDecl that) {
        List<RetType> units_result = recurOnListOfId(that.getUnits());
        Option<RetType> dim_result = recurOnOptionOfType(that.getDim());
        Option<RetType> def_result = recurOnOptionOfExpr(that.getDef());
        return forUnitDeclOnly(that, units_result, dim_result, def_result);
    }

    public RetType forTestDecl(TestDecl that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> gens_result = recurOnListOfGeneratorClause(that.getGens());
        RetType expr_result = that.getExpr().accept(this);
        return forTestDeclOnly(that, name_result, gens_result, expr_result);
    }

    public RetType forPropertyDecl(PropertyDecl that) {
        Option<RetType> name_result = recurOnOptionOfId(that.getName());
        List<RetType> params_result = recurOnListOfParam(that.getParams());
        RetType expr_result = that.getExpr().accept(this);
        return forPropertyDeclOnly(that, name_result, params_result, expr_result);
    }

    public RetType forAbsExternalSyntax(AbsExternalSyntax that) {
        RetType openExpander_result = that.getOpenExpander().accept(this);
        RetType name_result = that.getName().accept(this);
        RetType closeExpander_result = that.getCloseExpander().accept(this);
        return forAbsExternalSyntaxOnly(that, openExpander_result, name_result, closeExpander_result);
    }

    public RetType forExternalSyntax(ExternalSyntax that) {
        RetType openExpander_result = that.getOpenExpander().accept(this);
        RetType name_result = that.getName().accept(this);
        RetType closeExpander_result = that.getCloseExpander().accept(this);
        RetType expr_result = that.getExpr().accept(this);
        return forExternalSyntaxOnly(that, openExpander_result, name_result, closeExpander_result, expr_result);
    }

    public RetType forGrammarDef(GrammarDef that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> extends_result = recurOnListOfQualifiedIdName(that.getExtends());
        List<RetType> members_result = recurOnListOfGrammarMemberDecl(that.getMembers());
        return forGrammarDefOnly(that, name_result, extends_result, members_result);
    }

    public RetType forNonterminalDef(NonterminalDef that) {
        RetType name_result = that.getName().accept(this);
        Option<RetType> type_result = recurOnOptionOfTraitType(that.getType());
        Option<RetType> modifier_result = recurOnOptionOfModifier(that.getModifier());
        List<RetType> syntaxDefs_result = recurOnListOfSyntaxDef(that.getSyntaxDefs());
        return forNonterminalDefOnly(that, name_result, type_result, modifier_result, syntaxDefs_result);
    }

    public RetType forNonterminalExtensionDef(NonterminalExtensionDef that) {
        RetType name_result = that.getName().accept(this);
        Option<RetType> type_result = recurOnOptionOfTraitType(that.getType());
        Option<RetType> modifier_result = recurOnOptionOfModifier(that.getModifier());
        List<RetType> syntaxDefs_result = recurOnListOfSyntaxDef(that.getSyntaxDefs());
        return forNonterminalExtensionDefOnly(that, name_result, type_result, modifier_result, syntaxDefs_result);
    }

    public RetType for_TerminalDef(_TerminalDef that) {
        RetType name_result = that.getName().accept(this);
        Option<RetType> type_result = recurOnOptionOfTraitType(that.getType());
        Option<RetType> modifier_result = recurOnOptionOfModifier(that.getModifier());
        RetType syntaxDef_result = that.getSyntaxDef().accept(this);
        return for_TerminalDefOnly(that, name_result, type_result, modifier_result, syntaxDef_result);
    }

    public RetType forSyntaxDef(SyntaxDef that) {
        List<RetType> syntaxSymbols_result = recurOnListOfSyntaxSymbol(that.getSyntaxSymbols());
        RetType transformationExpression_result = that.getTransformationExpression().accept(this);
        return forSyntaxDefOnly(that, syntaxSymbols_result, transformationExpression_result);
    }

    public RetType forPrefixedSymbol(PrefixedSymbol that) {
        Option<RetType> id_result = recurOnOptionOfId(that.getId());
        RetType symbol_result = that.getSymbol().accept(this);
        return forPrefixedSymbolOnly(that, id_result, symbol_result);
    }

    public RetType forOptionalSymbol(OptionalSymbol that) {
        RetType symbol_result = that.getSymbol().accept(this);
        return forOptionalSymbolOnly(that, symbol_result);
    }

    public RetType forRepeatSymbol(RepeatSymbol that) {
        RetType symbol_result = that.getSymbol().accept(this);
        return forRepeatSymbolOnly(that, symbol_result);
    }

    public RetType forRepeatOneOrMoreSymbol(RepeatOneOrMoreSymbol that) {
        RetType symbol_result = that.getSymbol().accept(this);
        return forRepeatOneOrMoreSymbolOnly(that, symbol_result);
    }

    public RetType forNoWhitespaceSymbol(NoWhitespaceSymbol that) {
        RetType symbol_result = that.getSymbol().accept(this);
        return forNoWhitespaceSymbolOnly(that, symbol_result);
    }

    public RetType forWhitespaceSymbol(WhitespaceSymbol that) {
        return forWhitespaceSymbolOnly(that);
    }

    public RetType forTabSymbol(TabSymbol that) {
        return forTabSymbolOnly(that);
    }

    public RetType forFormfeedSymbol(FormfeedSymbol that) {
        return forFormfeedSymbolOnly(that);
    }

    public RetType forCarriageReturnSymbol(CarriageReturnSymbol that) {
        return forCarriageReturnSymbolOnly(that);
    }

    public RetType forBackspaceSymbol(BackspaceSymbol that) {
        return forBackspaceSymbolOnly(that);
    }

    public RetType forNewlineSymbol(NewlineSymbol that) {
        return forNewlineSymbolOnly(that);
    }

    public RetType forBreaklineSymbol(BreaklineSymbol that) {
        return forBreaklineSymbolOnly(that);
    }

    public RetType forItemSymbol(ItemSymbol that) {
        return forItemSymbolOnly(that);
    }

    public RetType forNonterminalSymbol(NonterminalSymbol that) {
        RetType nonterminal_result = that.getNonterminal().accept(this);
        return forNonterminalSymbolOnly(that, nonterminal_result);
    }

    public RetType forKeywordSymbol(KeywordSymbol that) {
        return forKeywordSymbolOnly(that);
    }

    public RetType forTokenSymbol(TokenSymbol that) {
        return forTokenSymbolOnly(that);
    }

    public RetType forNotPredicateSymbol(NotPredicateSymbol that) {
        RetType symbol_result = that.getSymbol().accept(this);
        return forNotPredicateSymbolOnly(that, symbol_result);
    }

    public RetType forAndPredicateSymbol(AndPredicateSymbol that) {
        RetType symbol_result = that.getSymbol().accept(this);
        return forAndPredicateSymbolOnly(that, symbol_result);
    }

    public RetType forCharacterClassSymbol(CharacterClassSymbol that) {
        List<RetType> characters_result = recurOnListOfCharacterSymbol(that.getCharacters());
        return forCharacterClassSymbolOnly(that, characters_result);
    }

    public RetType forCharSymbol(CharSymbol that) {
        return forCharSymbolOnly(that);
    }

    public RetType forCharacterInterval(CharacterInterval that) {
        return forCharacterIntervalOnly(that);
    }

    public RetType forAsExpr(AsExpr that) {
        RetType expr_result = that.getExpr().accept(this);
        RetType type_result = that.getType().accept(this);
        return forAsExprOnly(that, expr_result, type_result);
    }

    public RetType forAsIfExpr(AsIfExpr that) {
        RetType expr_result = that.getExpr().accept(this);
        RetType type_result = that.getType().accept(this);
        return forAsIfExprOnly(that, expr_result, type_result);
    }

    public RetType forAssignment(Assignment that) {
        List<RetType> lhs_result = recurOnListOfLHS(that.getLhs());
        Option<RetType> opr_result = recurOnOptionOfOp(that.getOpr());
        RetType rhs_result = that.getRhs().accept(this);
        return forAssignmentOnly(that, lhs_result, opr_result, rhs_result);
    }

    public RetType forBlock(Block that) {
        List<RetType> exprs_result = recurOnListOfExpr(that.getExprs());
        return forBlockOnly(that, exprs_result);
    }

    public RetType forCaseExpr(CaseExpr that) {
        Option<RetType> param_result = recurOnOptionOfExpr(that.getParam());
        Option<RetType> compare_result = recurOnOptionOfOp(that.getCompare());
        List<RetType> clauses_result = recurOnListOfCaseClause(that.getClauses());
        Option<RetType> elseClause_result = recurOnOptionOfBlock(that.getElseClause());
        return forCaseExprOnly(that, param_result, compare_result, clauses_result, elseClause_result);
    }

    public RetType forDo(Do that) {
        List<RetType> fronts_result = recurOnListOfDoFront(that.getFronts());
        return forDoOnly(that, fronts_result);
    }

    public RetType forFor(For that) {
        List<RetType> gens_result = recurOnListOfGeneratorClause(that.getGens());
        RetType body_result = that.getBody().accept(this);
        return forForOnly(that, gens_result, body_result);
    }

    public RetType forIf(If that) {
        List<RetType> clauses_result = recurOnListOfIfClause(that.getClauses());
        Option<RetType> elseClause_result = recurOnOptionOfBlock(that.getElseClause());
        return forIfOnly(that, clauses_result, elseClause_result);
    }

    public RetType forLabel(Label that) {
        RetType name_result = that.getName().accept(this);
        RetType body_result = that.getBody().accept(this);
        return forLabelOnly(that, name_result, body_result);
    }

    public RetType forObjectExpr(ObjectExpr that) {
        List<RetType> extendsClause_result = recurOnListOfTraitTypeWhere(that.getExtendsClause());
        List<RetType> decls_result = recurOnListOfDecl(that.getDecls());
        return forObjectExprOnly(that, extendsClause_result, decls_result);
    }

    public RetType for_RewriteObjectExpr(_RewriteObjectExpr that) {
        List<RetType> extendsClause_result = recurOnListOfTraitTypeWhere(that.getExtendsClause());
        List<RetType> decls_result = recurOnListOfDecl(that.getDecls());
        List<RetType> staticParams_result = recurOnListOfStaticParam(that.getStaticParams());
        List<RetType> staticArgs_result = recurOnListOfStaticArg(that.getStaticArgs());
        Option<List<RetType>> params_result = recurOnOptionOfListOfParam(that.getParams());
        return for_RewriteObjectExprOnly(that, extendsClause_result, decls_result, staticParams_result, staticArgs_result, params_result);
    }

    public RetType forTry(Try that) {
        RetType body_result = that.getBody().accept(this);
        Option<RetType> catchClause_result = recurOnOptionOfCatch(that.getCatchClause());
        List<RetType> forbid_result = recurOnListOfTraitType(that.getForbid());
        Option<RetType> finallyClause_result = recurOnOptionOfBlock(that.getFinallyClause());
        return forTryOnly(that, body_result, catchClause_result, forbid_result, finallyClause_result);
    }

    public RetType forTupleExpr(TupleExpr that) {
        List<RetType> exprs_result = recurOnListOfExpr(that.getExprs());
        return forTupleExprOnly(that, exprs_result);
    }

    public RetType forArgExpr(ArgExpr that) {
        List<RetType> exprs_result = recurOnListOfExpr(that.getExprs());
        Option<RetType> varargs_result = recurOnOptionOfVarargsExpr(that.getVarargs());
        List<RetType> keywords_result = recurOnListOfKeywordExpr(that.getKeywords());
        return forArgExprOnly(that, exprs_result, varargs_result, keywords_result);
    }

    public RetType forTypecase(Typecase that) {
        List<RetType> clauses_result = recurOnListOfTypecaseClause(that.getClauses());
        Option<RetType> elseClause_result = recurOnOptionOfBlock(that.getElseClause());
        return forTypecaseOnly(that, clauses_result, elseClause_result);
    }

    public RetType forWhile(While that) {
        RetType test_result = that.getTest().accept(this);
        RetType body_result = that.getBody().accept(this);
        return forWhileOnly(that, test_result, body_result);
    }

    public RetType forAccumulator(Accumulator that) {
        List<RetType> staticArgs_result = recurOnListOfStaticArg(that.getStaticArgs());
        RetType opr_result = that.getOpr().accept(this);
        List<RetType> gens_result = recurOnListOfGeneratorClause(that.getGens());
        RetType body_result = that.getBody().accept(this);
        return forAccumulatorOnly(that, staticArgs_result, opr_result, gens_result, body_result);
    }

    public RetType forArrayComprehension(ArrayComprehension that) {
        List<RetType> staticArgs_result = recurOnListOfStaticArg(that.getStaticArgs());
        List<RetType> clauses_result = recurOnListOfArrayComprehensionClause(that.getClauses());
        return forArrayComprehensionOnly(that, staticArgs_result, clauses_result);
    }

    public RetType forAtomicExpr(AtomicExpr that) {
        RetType expr_result = that.getExpr().accept(this);
        return forAtomicExprOnly(that, expr_result);
    }

    public RetType forExit(Exit that) {
        Option<RetType> target_result = recurOnOptionOfId(that.getTarget());
        Option<RetType> returnExpr_result = recurOnOptionOfExpr(that.getReturnExpr());
        return forExitOnly(that, target_result, returnExpr_result);
    }

    public RetType forSpawn(Spawn that) {
        RetType body_result = that.getBody().accept(this);
        return forSpawnOnly(that, body_result);
    }

    public RetType forThrow(Throw that) {
        RetType expr_result = that.getExpr().accept(this);
        return forThrowOnly(that, expr_result);
    }

    public RetType forTryAtomicExpr(TryAtomicExpr that) {
        RetType expr_result = that.getExpr().accept(this);
        return forTryAtomicExprOnly(that, expr_result);
    }

    public RetType forFnExpr(FnExpr that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> staticParams_result = recurOnListOfStaticParam(that.getStaticParams());
        List<RetType> params_result = recurOnListOfParam(that.getParams());
        Option<RetType> returnType_result = recurOnOptionOfType(that.getReturnType());
        RetType where_result = that.getWhere().accept(this);
        Option<List<RetType>> throwsClause_result = recurOnOptionOfListOfTraitType(that.getThrowsClause());
        RetType body_result = that.getBody().accept(this);
        return forFnExprOnly(that, name_result, staticParams_result, params_result, returnType_result, where_result, throwsClause_result, body_result);
    }

    public RetType forLetFn(LetFn that) {
        List<RetType> body_result = recurOnListOfExpr(that.getBody());
        List<RetType> fns_result = recurOnListOfFnDef(that.getFns());
        return forLetFnOnly(that, body_result, fns_result);
    }

    public RetType forLocalVarDecl(LocalVarDecl that) {
        List<RetType> body_result = recurOnListOfExpr(that.getBody());
        List<RetType> lhs_result = recurOnListOfLValue(that.getLhs());
        Option<RetType> rhs_result = recurOnOptionOfExpr(that.getRhs());
        return forLocalVarDeclOnly(that, body_result, lhs_result, rhs_result);
    }

    public RetType forGeneratedExpr(GeneratedExpr that) {
        RetType expr_result = that.getExpr().accept(this);
        List<RetType> gens_result = recurOnListOfGeneratorClause(that.getGens());
        return forGeneratedExprOnly(that, expr_result, gens_result);
    }

    public RetType forSubscriptExpr(SubscriptExpr that) {
        RetType obj_result = that.getObj().accept(this);
        List<RetType> subs_result = recurOnListOfExpr(that.getSubs());
        Option<RetType> op_result = recurOnOptionOfEnclosing(that.getOp());
        return forSubscriptExprOnly(that, obj_result, subs_result, op_result);
    }

    public RetType forFloatLiteralExpr(FloatLiteralExpr that) {
        return forFloatLiteralExprOnly(that);
    }

    public RetType forIntLiteralExpr(IntLiteralExpr that) {
        return forIntLiteralExprOnly(that);
    }

    public RetType forCharLiteralExpr(CharLiteralExpr that) {
        return forCharLiteralExprOnly(that);
    }

    public RetType forStringLiteralExpr(StringLiteralExpr that) {
        return forStringLiteralExprOnly(that);
    }

    public RetType forVoidLiteralExpr(VoidLiteralExpr that) {
        return forVoidLiteralExprOnly(that);
    }

    public RetType forVarRef(VarRef that) {
        RetType var_result = that.getVar().accept(this);
        return forVarRefOnly(that, var_result);
    }

    public RetType forFieldRef(FieldRef that) {
        RetType obj_result = that.getObj().accept(this);
        RetType field_result = that.getField().accept(this);
        return forFieldRefOnly(that, obj_result, field_result);
    }

    public RetType forFieldRefForSure(FieldRefForSure that) {
        RetType obj_result = that.getObj().accept(this);
        RetType field_result = that.getField().accept(this);
        return forFieldRefForSureOnly(that, obj_result, field_result);
    }

    public RetType for_RewriteFieldRef(_RewriteFieldRef that) {
        RetType obj_result = that.getObj().accept(this);
        RetType field_result = that.getField().accept(this);
        return for_RewriteFieldRefOnly(that, obj_result, field_result);
    }

    public RetType forFnRef(FnRef that) {
        List<RetType> fns_result = recurOnListOfQualifiedIdName(that.getFns());
        List<RetType> staticArgs_result = recurOnListOfStaticArg(that.getStaticArgs());
        return forFnRefOnly(that, fns_result, staticArgs_result);
    }

    public RetType for_RewriteFnRef(_RewriteFnRef that) {
        RetType fn_result = that.getFn().accept(this);
        List<RetType> staticArgs_result = recurOnListOfStaticArg(that.getStaticArgs());
        return for_RewriteFnRefOnly(that, fn_result, staticArgs_result);
    }

    public RetType forOpRef(OpRef that) {
        List<RetType> ops_result = recurOnListOfQualifiedOpName(that.getOps());
        List<RetType> staticArgs_result = recurOnListOfStaticArg(that.getStaticArgs());
        return forOpRefOnly(that, ops_result, staticArgs_result);
    }

    public RetType forLooseJuxt(LooseJuxt that) {
        List<RetType> exprs_result = recurOnListOfExpr(that.getExprs());
        return forLooseJuxtOnly(that, exprs_result);
    }

    public RetType forTightJuxt(TightJuxt that) {
        List<RetType> exprs_result = recurOnListOfExpr(that.getExprs());
        return forTightJuxtOnly(that, exprs_result);
    }

    public RetType forOprExpr(OprExpr that) {
        RetType op_result = that.getOp().accept(this);
        List<RetType> args_result = recurOnListOfExpr(that.getArgs());
        return forOprExprOnly(that, op_result, args_result);
    }

    public RetType forChainExpr(ChainExpr that) {
        RetType first_result = that.getFirst().accept(this);
        return forChainExprOnly(that, first_result);
    }

    public RetType forCoercionInvocation(CoercionInvocation that) {
        RetType type_result = that.getType().accept(this);
        List<RetType> staticArgs_result = recurOnListOfStaticArg(that.getStaticArgs());
        RetType arg_result = that.getArg().accept(this);
        return forCoercionInvocationOnly(that, type_result, staticArgs_result, arg_result);
    }

    public RetType forMethodInvocation(MethodInvocation that) {
        RetType obj_result = that.getObj().accept(this);
        RetType method_result = that.getMethod().accept(this);
        List<RetType> staticArgs_result = recurOnListOfStaticArg(that.getStaticArgs());
        RetType arg_result = that.getArg().accept(this);
        return forMethodInvocationOnly(that, obj_result, method_result, staticArgs_result, arg_result);
    }

    public RetType forMathPrimary(MathPrimary that) {
        RetType front_result = that.getFront().accept(this);
        List<RetType> rest_result = recurOnListOfMathItem(that.getRest());
        return forMathPrimaryOnly(that, front_result, rest_result);
    }

    public RetType forArrayElement(ArrayElement that) {
        RetType element_result = that.getElement().accept(this);
        return forArrayElementOnly(that, element_result);
    }

    public RetType forArrayElements(ArrayElements that) {
        List<RetType> elements_result = recurOnListOfArrayExpr(that.getElements());
        return forArrayElementsOnly(that, elements_result);
    }

    public RetType forExponentType(ExponentType that) {
        RetType base_result = that.getBase().accept(this);
        RetType power_result = that.getPower().accept(this);
        return forExponentTypeOnly(that, base_result, power_result);
    }

    public RetType forBaseDim(BaseDim that) {
        return forBaseDimOnly(that);
    }

    public RetType forDimRef(DimRef that) {
        RetType name_result = that.getName().accept(this);
        return forDimRefOnly(that, name_result);
    }

    public RetType forProductDim(ProductDim that) {
        RetType multiplier_result = that.getMultiplier().accept(this);
        RetType multiplicand_result = that.getMultiplicand().accept(this);
        return forProductDimOnly(that, multiplier_result, multiplicand_result);
    }

    public RetType forQuotientDim(QuotientDim that) {
        RetType numerator_result = that.getNumerator().accept(this);
        RetType denominator_result = that.getDenominator().accept(this);
        return forQuotientDimOnly(that, numerator_result, denominator_result);
    }

    public RetType forExponentDim(ExponentDim that) {
        RetType base_result = that.getBase().accept(this);
        RetType power_result = that.getPower().accept(this);
        return forExponentDimOnly(that, base_result, power_result);
    }

    public RetType forOpDim(OpDim that) {
        RetType val_result = that.getVal().accept(this);
        RetType op_result = that.getOp().accept(this);
        return forOpDimOnly(that, val_result, op_result);
    }

    public RetType forArrowType(ArrowType that) {
        RetType domain_result = that.getDomain().accept(this);
        RetType range_result = that.getRange().accept(this);
        Option<List<RetType>> throwsClause_result = recurOnOptionOfListOfType(that.getThrowsClause());
        return forArrowTypeOnly(that, domain_result, range_result, throwsClause_result);
    }

    public RetType for_RewriteGenericArrowType(_RewriteGenericArrowType that) {
        RetType domain_result = that.getDomain().accept(this);
        RetType range_result = that.getRange().accept(this);
        Option<List<RetType>> throwsClause_result = recurOnOptionOfListOfType(that.getThrowsClause());
        List<RetType> staticParams_result = recurOnListOfStaticParam(that.getStaticParams());
        RetType where_result = that.getWhere().accept(this);
        return for_RewriteGenericArrowTypeOnly(that, domain_result, range_result, throwsClause_result, staticParams_result, where_result);
    }

    public RetType forBottomType(BottomType that) {
        return forBottomTypeOnly(that);
    }

    public RetType forIdType(IdType that) {
        RetType name_result = that.getName().accept(this);
        return forIdTypeOnly(that, name_result);
    }

    public RetType forInstantiatedType(InstantiatedType that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> args_result = recurOnListOfStaticArg(that.getArgs());
        return forInstantiatedTypeOnly(that, name_result, args_result);
    }

    public RetType forArrayType(ArrayType that) {
        RetType element_result = that.getElement().accept(this);
        RetType indices_result = that.getIndices().accept(this);
        return forArrayTypeOnly(that, element_result, indices_result);
    }

    public RetType forMatrixType(MatrixType that) {
        RetType element_result = that.getElement().accept(this);
        List<RetType> dimensions_result = recurOnListOfExtentRange(that.getDimensions());
        return forMatrixTypeOnly(that, element_result, dimensions_result);
    }

    public RetType forTupleType(TupleType that) {
        List<RetType> elements_result = recurOnListOfType(that.getElements());
        return forTupleTypeOnly(that, elements_result);
    }

    public RetType forArgType(ArgType that) {
        List<RetType> elements_result = recurOnListOfType(that.getElements());
        Option<RetType> varargs_result = recurOnOptionOfVarargsType(that.getVarargs());
        List<RetType> keywords_result = recurOnListOfKeywordType(that.getKeywords());
        return forArgTypeOnly(that, elements_result, varargs_result, keywords_result);
    }

    public RetType forVoidType(VoidType that) {
        return forVoidTypeOnly(that);
    }

    public RetType forInferenceVarType(InferenceVarType that) {
        return forInferenceVarTypeOnly(that);
    }

    public RetType forAndType(AndType that) {
        RetType first_result = that.getFirst().accept(this);
        RetType second_result = that.getSecond().accept(this);
        return forAndTypeOnly(that, first_result, second_result);
    }

    public RetType forOrType(OrType that) {
        RetType first_result = that.getFirst().accept(this);
        RetType second_result = that.getSecond().accept(this);
        return forOrTypeOnly(that, first_result, second_result);
    }

    public RetType forFixedPointType(FixedPointType that) {
        RetType name_result = that.getName().accept(this);
        RetType body_result = that.getBody().accept(this);
        return forFixedPointTypeOnly(that, name_result, body_result);
    }

    public RetType forTaggedDimType(TaggedDimType that) {
        RetType type_result = that.getType().accept(this);
        RetType dim_result = that.getDim().accept(this);
        Option<RetType> unit_result = recurOnOptionOfExpr(that.getUnit());
        return forTaggedDimTypeOnly(that, type_result, dim_result, unit_result);
    }

    public RetType forTaggedUnitType(TaggedUnitType that) {
        RetType type_result = that.getType().accept(this);
        RetType unit_result = that.getUnit().accept(this);
        return forTaggedUnitTypeOnly(that, type_result, unit_result);
    }

    public RetType forIdArg(IdArg that) {
        RetType name_result = that.getName().accept(this);
        return forIdArgOnly(that, name_result);
    }

    public RetType forTypeArg(TypeArg that) {
        RetType type_result = that.getType().accept(this);
        return forTypeArgOnly(that, type_result);
    }

    public RetType forIntArg(IntArg that) {
        RetType val_result = that.getVal().accept(this);
        return forIntArgOnly(that, val_result);
    }

    public RetType forBoolArg(BoolArg that) {
        RetType bool_result = that.getBool().accept(this);
        return forBoolArgOnly(that, bool_result);
    }

    public RetType forOprArg(OprArg that) {
        RetType name_result = that.getName().accept(this);
        return forOprArgOnly(that, name_result);
    }

    public RetType forDimArg(DimArg that) {
        RetType dim_result = that.getDim().accept(this);
        return forDimArgOnly(that, dim_result);
    }

    public RetType forUnitArg(UnitArg that) {
        RetType unit_result = that.getUnit().accept(this);
        return forUnitArgOnly(that, unit_result);
    }

    public RetType forNumberConstraint(NumberConstraint that) {
        RetType val_result = that.getVal().accept(this);
        return forNumberConstraintOnly(that, val_result);
    }

    public RetType forIntRef(IntRef that) {
        RetType name_result = that.getName().accept(this);
        return forIntRefOnly(that, name_result);
    }

    public RetType forSumConstraint(SumConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forSumConstraintOnly(that, left_result, right_result);
    }

    public RetType forMinusConstraint(MinusConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forMinusConstraintOnly(that, left_result, right_result);
    }

    public RetType forProductConstraint(ProductConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forProductConstraintOnly(that, left_result, right_result);
    }

    public RetType forExponentConstraint(ExponentConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forExponentConstraintOnly(that, left_result, right_result);
    }

    public RetType forBoolConstant(BoolConstant that) {
        return forBoolConstantOnly(that);
    }

    public RetType forBoolRef(BoolRef that) {
        RetType name_result = that.getName().accept(this);
        return forBoolRefOnly(that, name_result);
    }

    public RetType forNotConstraint(NotConstraint that) {
        RetType bool_result = that.getBool().accept(this);
        return forNotConstraintOnly(that, bool_result);
    }

    public RetType forOrConstraint(OrConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forOrConstraintOnly(that, left_result, right_result);
    }

    public RetType forAndConstraint(AndConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forAndConstraintOnly(that, left_result, right_result);
    }

    public RetType forImpliesConstraint(ImpliesConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forImpliesConstraintOnly(that, left_result, right_result);
    }

    public RetType forBEConstraint(BEConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forBEConstraintOnly(that, left_result, right_result);
    }

    public RetType forWhereClause(WhereClause that) {
        List<RetType> bindings_result = recurOnListOfWhereBinding(that.getBindings());
        List<RetType> constraints_result = recurOnListOfWhereConstraint(that.getConstraints());
        return forWhereClauseOnly(that, bindings_result, constraints_result);
    }

    public RetType forWhereType(WhereType that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> supers_result = recurOnListOfTraitType(that.getSupers());
        return forWhereTypeOnly(that, name_result, supers_result);
    }

    public RetType forWhereNat(WhereNat that) {
        RetType name_result = that.getName().accept(this);
        return forWhereNatOnly(that, name_result);
    }

    public RetType forWhereInt(WhereInt that) {
        RetType name_result = that.getName().accept(this);
        return forWhereIntOnly(that, name_result);
    }

    public RetType forWhereBool(WhereBool that) {
        RetType name_result = that.getName().accept(this);
        return forWhereBoolOnly(that, name_result);
    }

    public RetType forWhereUnit(WhereUnit that) {
        RetType name_result = that.getName().accept(this);
        return forWhereUnitOnly(that, name_result);
    }

    public RetType forWhereExtends(WhereExtends that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> supers_result = recurOnListOfTraitType(that.getSupers());
        return forWhereExtendsOnly(that, name_result, supers_result);
    }

    public RetType forTypeAlias(TypeAlias that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> staticParams_result = recurOnListOfStaticParam(that.getStaticParams());
        RetType type_result = that.getType().accept(this);
        return forTypeAliasOnly(that, name_result, staticParams_result, type_result);
    }

    public RetType forWhereCoerces(WhereCoerces that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forWhereCoercesOnly(that, left_result, right_result);
    }

    public RetType forWhereWidens(WhereWidens that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forWhereWidensOnly(that, left_result, right_result);
    }

    public RetType forWhereWidensCoerces(WhereWidensCoerces that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forWhereWidensCoercesOnly(that, left_result, right_result);
    }

    public RetType forWhereEquals(WhereEquals that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forWhereEqualsOnly(that, left_result, right_result);
    }

    public RetType forUnitConstraint(UnitConstraint that) {
        RetType name_result = that.getName().accept(this);
        return forUnitConstraintOnly(that, name_result);
    }

    public RetType forLEConstraint(LEConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forLEConstraintOnly(that, left_result, right_result);
    }

    public RetType forLTConstraint(LTConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forLTConstraintOnly(that, left_result, right_result);
    }

    public RetType forGEConstraint(GEConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forGEConstraintOnly(that, left_result, right_result);
    }

    public RetType forGTConstraint(GTConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forGTConstraintOnly(that, left_result, right_result);
    }

    public RetType forIEConstraint(IEConstraint that) {
        RetType left_result = that.getLeft().accept(this);
        RetType right_result = that.getRight().accept(this);
        return forIEConstraintOnly(that, left_result, right_result);
    }

    public RetType forBoolConstraintExpr(BoolConstraintExpr that) {
        RetType constraint_result = that.getConstraint().accept(this);
        return forBoolConstraintExprOnly(that, constraint_result);
    }

    public RetType forContract(Contract that) {
        Option<List<RetType>> requires_result = recurOnOptionOfListOfExpr(that.getRequires());
        Option<List<RetType>> ensures_result = recurOnOptionOfListOfEnsuresClause(that.getEnsures());
        Option<List<RetType>> invariants_result = recurOnOptionOfListOfExpr(that.getInvariants());
        return forContractOnly(that, requires_result, ensures_result, invariants_result);
    }

    public RetType forEnsuresClause(EnsuresClause that) {
        RetType post_result = that.getPost().accept(this);
        Option<RetType> pre_result = recurOnOptionOfExpr(that.getPre());
        return forEnsuresClauseOnly(that, post_result, pre_result);
    }

    public RetType forModifierAbstract(ModifierAbstract that) {
        return forModifierAbstractOnly(that);
    }

    public RetType forModifierAtomic(ModifierAtomic that) {
        return forModifierAtomicOnly(that);
    }

    public RetType forModifierGetter(ModifierGetter that) {
        return forModifierGetterOnly(that);
    }

    public RetType forModifierHidden(ModifierHidden that) {
        return forModifierHiddenOnly(that);
    }

    public RetType forModifierIO(ModifierIO that) {
        return forModifierIOOnly(that);
    }

    public RetType forModifierOverride(ModifierOverride that) {
        return forModifierOverrideOnly(that);
    }

    public RetType forModifierPrivate(ModifierPrivate that) {
        return forModifierPrivateOnly(that);
    }

    public RetType forModifierSettable(ModifierSettable that) {
        return forModifierSettableOnly(that);
    }

    public RetType forModifierSetter(ModifierSetter that) {
        return forModifierSetterOnly(that);
    }

    public RetType forModifierTest(ModifierTest that) {
        return forModifierTestOnly(that);
    }

    public RetType forModifierTransient(ModifierTransient that) {
        return forModifierTransientOnly(that);
    }

    public RetType forModifierValue(ModifierValue that) {
        return forModifierValueOnly(that);
    }

    public RetType forModifierVar(ModifierVar that) {
        return forModifierVarOnly(that);
    }

    public RetType forModifierWidens(ModifierWidens that) {
        return forModifierWidensOnly(that);
    }

    public RetType forModifierWrapped(ModifierWrapped that) {
        return forModifierWrappedOnly(that);
    }

    public RetType forOperatorParam(OperatorParam that) {
        RetType name_result = that.getName().accept(this);
        return forOperatorParamOnly(that, name_result);
    }

    public RetType forBoolParam(BoolParam that) {
        RetType name_result = that.getName().accept(this);
        return forBoolParamOnly(that, name_result);
    }

    public RetType forDimensionParam(DimensionParam that) {
        RetType name_result = that.getName().accept(this);
        return forDimensionParamOnly(that, name_result);
    }

    public RetType forIntParam(IntParam that) {
        RetType name_result = that.getName().accept(this);
        return forIntParamOnly(that, name_result);
    }

    public RetType forNatParam(NatParam that) {
        RetType name_result = that.getName().accept(this);
        return forNatParamOnly(that, name_result);
    }

    public RetType forSimpleTypeParam(SimpleTypeParam that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> extendsClause_result = recurOnListOfTraitType(that.getExtendsClause());
        return forSimpleTypeParamOnly(that, name_result, extendsClause_result);
    }

    public RetType forUnitParam(UnitParam that) {
        RetType name_result = that.getName().accept(this);
        Option<RetType> dim_result = recurOnOptionOfType(that.getDim());
        return forUnitParamOnly(that, name_result, dim_result);
    }

    public RetType forAPIName(APIName that) {
        List<RetType> ids_result = recurOnListOfId(that.getIds());
        return forAPINameOnly(that, ids_result);
    }

    public RetType forQualifiedIdName(QualifiedIdName that) {
        Option<RetType> api_result = recurOnOptionOfAPIName(that.getApi());
        RetType name_result = that.getName().accept(this);
        return forQualifiedIdNameOnly(that, api_result, name_result);
    }

    public RetType forQualifiedOpName(QualifiedOpName that) {
        Option<RetType> api_result = recurOnOptionOfAPIName(that.getApi());
        RetType name_result = that.getName().accept(this);
        return forQualifiedOpNameOnly(that, api_result, name_result);
    }

    public RetType forId(Id that) {
        return forIdOnly(that);
    }

    public RetType forOp(Op that) {
        Option<RetType> fixity_result = recurOnOptionOfFixity(that.getFixity());
        return forOpOnly(that, fixity_result);
    }

    public RetType forEnclosing(Enclosing that) {
        RetType open_result = that.getOpen().accept(this);
        RetType close_result = that.getClose().accept(this);
        return forEnclosingOnly(that, open_result, close_result);
    }

    public RetType forAnonymousFnName(AnonymousFnName that) {
        return forAnonymousFnNameOnly(that);
    }

    public RetType forConstructorFnName(ConstructorFnName that) {
        RetType def_result = that.getDef().accept(this);
        return forConstructorFnNameOnly(that, def_result);
    }

    public RetType forArrayComprehensionClause(ArrayComprehensionClause that) {
        List<RetType> bind_result = recurOnListOfExpr(that.getBind());
        RetType init_result = that.getInit().accept(this);
        List<RetType> gens_result = recurOnListOfGeneratorClause(that.getGens());
        return forArrayComprehensionClauseOnly(that, bind_result, init_result, gens_result);
    }

    public RetType forKeywordExpr(KeywordExpr that) {
        RetType name_result = that.getName().accept(this);
        RetType init_result = that.getInit().accept(this);
        return forKeywordExprOnly(that, name_result, init_result);
    }

    public RetType forCaseClause(CaseClause that) {
        RetType match_result = that.getMatch().accept(this);
        RetType body_result = that.getBody().accept(this);
        return forCaseClauseOnly(that, match_result, body_result);
    }

    public RetType forCatch(Catch that) {
        RetType name_result = that.getName().accept(this);
        List<RetType> clauses_result = recurOnListOfCatchClause(that.getClauses());
        return forCatchOnly(that, name_result, clauses_result);
    }

    public RetType forCatchClause(CatchClause that) {
        RetType match_result = that.getMatch().accept(this);
        RetType body_result = that.getBody().accept(this);
        return forCatchClauseOnly(that, match_result, body_result);
    }

    public RetType forDoFront(DoFront that) {
        Option<RetType> loc_result = recurOnOptionOfExpr(that.getLoc());
        RetType expr_result = that.getExpr().accept(this);
        return forDoFrontOnly(that, loc_result, expr_result);
    }

    public RetType forIfClause(IfClause that) {
        RetType test_result = that.getTest().accept(this);
        RetType body_result = that.getBody().accept(this);
        return forIfClauseOnly(that, test_result, body_result);
    }

    public RetType forTypecaseClause(TypecaseClause that) {
        List<RetType> match_result = recurOnListOfType(that.getMatch());
        RetType body_result = that.getBody().accept(this);
        return forTypecaseClauseOnly(that, match_result, body_result);
    }

    public RetType forExtentRange(ExtentRange that) {
        Option<RetType> base_result = recurOnOptionOfStaticArg(that.getBase());
        Option<RetType> size_result = recurOnOptionOfStaticArg(that.getSize());
        return forExtentRangeOnly(that, base_result, size_result);
    }

    public RetType forGeneratorClause(GeneratorClause that) {
        List<RetType> bind_result = recurOnListOfId(that.getBind());
        RetType init_result = that.getInit().accept(this);
        return forGeneratorClauseOnly(that, bind_result, init_result);
    }

    public RetType forVarargsExpr(VarargsExpr that) {
        RetType varargs_result = that.getVarargs().accept(this);
        return forVarargsExprOnly(that, varargs_result);
    }

    public RetType forVarargsType(VarargsType that) {
        RetType type_result = that.getType().accept(this);
        return forVarargsTypeOnly(that, type_result);
    }

    public RetType forKeywordType(KeywordType that) {
        RetType name_result = that.getName().accept(this);
        RetType type_result = that.getType().accept(this);
        return forKeywordTypeOnly(that, name_result, type_result);
    }

    public RetType forTraitTypeWhere(TraitTypeWhere that) {
        RetType type_result = that.getType().accept(this);
        RetType where_result = that.getWhere().accept(this);
        return forTraitTypeWhereOnly(that, type_result, where_result);
    }

    public RetType forIndices(Indices that) {
        List<RetType> extents_result = recurOnListOfExtentRange(that.getExtents());
        return forIndicesOnly(that, extents_result);
    }

    public RetType forParenthesisDelimitedMI(ParenthesisDelimitedMI that) {
        RetType expr_result = that.getExpr().accept(this);
        return forParenthesisDelimitedMIOnly(that, expr_result);
    }

    public RetType forNonParenthesisDelimitedMI(NonParenthesisDelimitedMI that) {
        RetType expr_result = that.getExpr().accept(this);
        return forNonParenthesisDelimitedMIOnly(that, expr_result);
    }

    public RetType forExponentiationMI(ExponentiationMI that) {
        RetType op_result = that.getOp().accept(this);
        Option<RetType> expr_result = recurOnOptionOfExpr(that.getExpr());
        return forExponentiationMIOnly(that, op_result, expr_result);
    }

    public RetType forSubscriptingMI(SubscriptingMI that) {
        RetType op_result = that.getOp().accept(this);
        List<RetType> exprs_result = recurOnListOfExpr(that.getExprs());
        return forSubscriptingMIOnly(that, op_result, exprs_result);
    }

    public RetType forInFixity(InFixity that) {
        return forInFixityOnly(that);
    }

    public RetType forPreFixity(PreFixity that) {
        return forPreFixityOnly(that);
    }

    public RetType forPostFixity(PostFixity that) {
        return forPostFixityOnly(that);
    }

    public RetType forNoFixity(NoFixity that) {
        return forNoFixityOnly(that);
    }

    public RetType forMultiFixity(MultiFixity that) {
        return forMultiFixityOnly(that);
    }

    public RetType forEnclosingFixity(EnclosingFixity that) {
        return forEnclosingFixityOnly(that);
    }

    public RetType forBigFixity(BigFixity that) {
        return forBigFixityOnly(that);
    }


    public List<RetType> recurOnListOfImport(List<Import> that) {
        List<RetType> result = new java.util.ArrayList<RetType>(0);
        for (Import elt : that) {
        result.add(elt.accept(this));
    }
    return result;
}

public List<RetType> recurOnListOfExport(List<Export> that) {
    List<RetType> result = new java.util.ArrayList<RetType>(0);
    for (Export elt : that) {
    result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfDecl(List<Decl> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (Decl elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfAbsDecl(List<AbsDecl> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (AbsDecl elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfSimpleName(List<SimpleName> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (SimpleName elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfAliasedSimpleName(List<AliasedSimpleName> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (AliasedSimpleName elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfAliasedAPIName(List<AliasedAPIName> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (AliasedAPIName elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<RetType> recurOnOptionOfSimpleName(Option<SimpleName> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<SimpleName, Option<RetType>>() {
public Option<RetType> forSome(SimpleName elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public Option<RetType> recurOnOptionOfId(Option<Id> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<Id, Option<RetType>>() {
public Option<RetType> forSome(Id elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfAPIName(List<APIName> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (APIName elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfModifier(List<Modifier> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (Modifier elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfStaticParam(List<StaticParam> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (StaticParam elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfTraitTypeWhere(List<TraitTypeWhere> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (TraitTypeWhere elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfTraitType(List<TraitType> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (TraitType elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<List<RetType>> recurOnOptionOfListOfTraitType(Option<List<TraitType>> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<List<TraitType>, Option<List<RetType>>>() {
public Option<List<RetType>> forSome(List<TraitType> elt) { return edu.rice.cs.plt.tuple.Option.some(recurOnListOfTraitType(elt)); }
public Option<List<RetType>> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public Option<List<RetType>> recurOnOptionOfListOfParam(Option<List<Param>> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<List<Param>, Option<List<RetType>>>() {
public Option<List<RetType>> forSome(List<Param> elt) { return edu.rice.cs.plt.tuple.Option.some(recurOnListOfParam(elt)); }
public Option<List<RetType>> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfParam(List<Param> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (Param elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfLValueBind(List<LValueBind> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (LValueBind elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<RetType> recurOnOptionOfType(Option<Type> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<Type, Option<RetType>>() {
public Option<RetType> forSome(Type elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfExtentRange(List<ExtentRange> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (ExtentRange elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfUnpasting(List<Unpasting> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (Unpasting elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<RetType> recurOnOptionOfExpr(Option<Expr> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<Expr, Option<RetType>>() {
public Option<RetType> forSome(Expr elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfId(List<Id> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (Id elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfGeneratorClause(List<GeneratorClause> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (GeneratorClause elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfQualifiedIdName(List<QualifiedIdName> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (QualifiedIdName elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfGrammarMemberDecl(List<GrammarMemberDecl> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (GrammarMemberDecl elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<RetType> recurOnOptionOfTraitType(Option<TraitType> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<TraitType, Option<RetType>>() {
public Option<RetType> forSome(TraitType elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public Option<RetType> recurOnOptionOfModifier(Option<? extends Modifier> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<Modifier, Option<RetType>>() {
public Option<RetType> forSome(Modifier elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfSyntaxDef(List<SyntaxDef> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (SyntaxDef elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfSyntaxSymbol(List<SyntaxSymbol> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (SyntaxSymbol elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfCharacterSymbol(List<CharacterSymbol> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (CharacterSymbol elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfLHS(List<LHS> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (LHS elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<RetType> recurOnOptionOfOp(Option<Op> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<Op, Option<RetType>>() {
public Option<RetType> forSome(Op elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfExpr(List<Expr> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (Expr elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfCaseClause(List<CaseClause> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (CaseClause elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<RetType> recurOnOptionOfBlock(Option<Block> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<Block, Option<RetType>>() {
public Option<RetType> forSome(Block elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfDoFront(List<DoFront> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (DoFront elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfIfClause(List<IfClause> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (IfClause elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfStaticArg(List<StaticArg> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (StaticArg elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<RetType> recurOnOptionOfCatch(Option<Catch> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<Catch, Option<RetType>>() {
public Option<RetType> forSome(Catch elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public Option<RetType> recurOnOptionOfVarargsExpr(Option<VarargsExpr> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<VarargsExpr, Option<RetType>>() {
public Option<RetType> forSome(VarargsExpr elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfKeywordExpr(List<KeywordExpr> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (KeywordExpr elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfTypecaseClause(List<TypecaseClause> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (TypecaseClause elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfArrayComprehensionClause(List<ArrayComprehensionClause> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (ArrayComprehensionClause elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfFnDef(List<FnDef> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (FnDef elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfLValue(List<LValue> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (LValue elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<RetType> recurOnOptionOfEnclosing(Option<Enclosing> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<Enclosing, Option<RetType>>() {
public Option<RetType> forSome(Enclosing elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfQualifiedOpName(List<QualifiedOpName> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (QualifiedOpName elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfMathItem(List<MathItem> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (MathItem elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfArrayExpr(List<ArrayExpr> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (ArrayExpr elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<List<RetType>> recurOnOptionOfListOfType(Option<List<Type>> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<List<Type>, Option<List<RetType>>>() {
public Option<List<RetType>> forSome(List<Type> elt) { return edu.rice.cs.plt.tuple.Option.some(recurOnListOfType(elt)); }
public Option<List<RetType>> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfType(List<Type> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (Type elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<RetType> recurOnOptionOfVarargsType(Option<VarargsType> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<VarargsType, Option<RetType>>() {
public Option<RetType> forSome(VarargsType elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfKeywordType(List<KeywordType> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (KeywordType elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfWhereBinding(List<WhereBinding> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (WhereBinding elt : that) {
result.add(elt.accept(this));
}
return result;
}

public List<RetType> recurOnListOfWhereConstraint(List<WhereConstraint> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (WhereConstraint elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<List<RetType>> recurOnOptionOfListOfExpr(Option<List<Expr>> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<List<Expr>, Option<List<RetType>>>() {
public Option<List<RetType>> forSome(List<Expr> elt) { return edu.rice.cs.plt.tuple.Option.some(recurOnListOfExpr(elt)); }
public Option<List<RetType>> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public Option<List<RetType>> recurOnOptionOfListOfEnsuresClause(Option<List<EnsuresClause>> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<List<EnsuresClause>, Option<List<RetType>>>() {
public Option<List<RetType>> forSome(List<EnsuresClause> elt) { return edu.rice.cs.plt.tuple.Option.some(recurOnListOfEnsuresClause(elt)); }
public Option<List<RetType>> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfEnsuresClause(List<EnsuresClause> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (EnsuresClause elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<RetType> recurOnOptionOfAPIName(Option<APIName> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<APIName, Option<RetType>>() {
public Option<RetType> forSome(APIName elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public Option<RetType> recurOnOptionOfFixity(Option<Fixity> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<Fixity, Option<RetType>>() {
public Option<RetType> forSome(Fixity elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}

public List<RetType> recurOnListOfCatchClause(List<CatchClause> that) {
List<RetType> result = new java.util.ArrayList<RetType>(0);
for (CatchClause elt : that) {
result.add(elt.accept(this));
}
return result;
}

public Option<RetType> recurOnOptionOfStaticArg(Option<StaticArg> that) {
return that.apply(new edu.rice.cs.plt.tuple.OptionVisitor<StaticArg, Option<RetType>>() {
public Option<RetType> forSome(StaticArg elt) { return edu.rice.cs.plt.tuple.Option.some(elt.accept(NodeDepthFirstVisitor.this)); }
public Option<RetType> forNone() { return edu.rice.cs.plt.tuple.Option.none(); }
});
}
}
