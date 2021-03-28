package br.ufscar;

import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class SemanticoVisitor extends AlBaseVisitor<Void> {

    Escopos escopos;
    List<Token> expressaoComparacao = new ArrayList<>();

    @Override
    public Void visitProgr(AlParser.ProgrContext ctx) {
        escopos = new Escopos();
        if (ctx.declarations() != null)
            visitDeclarations(ctx.declarations());
        visitBody(ctx.body());

        escopos.percorrerEscoposAninhados().forEach(it -> {
            System.out.println(it.toString());
        });

        return null;
    }

    @Override
    public Void visitDeclarations(AlParser.DeclarationsContext ctx) {

        ctx.local_decl_global().forEach(it -> visitLocal_decl_global(it));

        return null;
    }

    @Override
    public Void visitLocal_decl_global(AlParser.Local_decl_globalContext ctx) {

        if (ctx.local_declaration() != null){
            visitLocal_declaration(ctx.local_declaration());
        }
        else if(ctx.global_declaration() != null){
            return null;
        }

        return null;
    }

    @Override
    public Void visitBody(AlParser.BodyContext ctx) {

        ctx.local_declaration().forEach(it -> visitLocal_declaration(it));
        ctx.cmd().forEach(it -> visitCmd(it));
        return null;
    }

    @Override
    public Void visitLocal_declaration(AlParser.Local_declarationContext ctx) {

        if (ctx.variable() != null){
            visitVariable(ctx.variable());
        }
        else if(ctx.CONSTANTE() != null){
            var ident = ctx.IDENT();
            var tipo = SemanticoUtils.verificarTipo(ctx.basic_type().getText());
            var const_value = ctx.const_value().getText();

            if(escopos.obterEscopoAtual().existe(ident.getText())){
                var entradaTabela = escopos.obterEscopoAtual().verificar(ident.getText());
                SemanticoUtils.adicionaErroSemantico(entradaTabela, ident.getSymbol().getLine(), ErrosSemanticos.IDENTIFICADOR_EXISTENTE);
            } else {
                escopos.obterEscopoAtual().inserir(ident.getText(), tipo, false);
            }
        }
        else if (ctx.TIPOW() != null){
            visitType(ctx.type());
        }

        return null;
    }

    @Override
    public Void visitVariable(AlParser.VariableContext ctx) {

        if (ctx.type().extend_type() != null) {
            var test = ctx.type().getText();
            TipoAl tipo = SemanticoUtils.verificarTipo(ctx.type().getText());
            if (tipo == null){
                SemanticoUtils.adicionaErroSemantico(ctx.type().getText(), ctx.type().getStart().getLine(), ErrosSemanticos.TIPO_INEXISTENTE);
            }
            else {
                for(var id: ctx.identifier()) {
                    for(var ident: id.IDENT()) {
                        String nome = ident.getText();
                        if(escopos.obterEscopoAtual().existe(nome)){
                            var entradaTabela = escopos.obterEscopoAtual().verificar(nome);
                            SemanticoUtils.adicionaErroSemantico(entradaTabela, ident.getSymbol().getLine(), ErrosSemanticos.IDENTIFICADOR_EXISTENTE);
                        } else {
                            var ponteiro = false;
                            if (ctx.type().extend_type() != null){
                                ponteiro = ctx.type().extend_type().ESTENDIDO() != null;
                            }
                            escopos.obterEscopoAtual().inserir(nome, tipo, ponteiro);
                        }
                    }
                }
            }
        }
        else if(ctx.type().register() != null){

            ctx.identifier().forEach(identfier -> {
                ctx.type().register().variable().forEach(it -> {
                    TipoAl tipo = SemanticoUtils.verificarTipo(it.type().getText());
                    if (tipo == null){
                        SemanticoUtils.adicionaErroSemantico(it.type().getText(), it.type().getStart().getLine(), ErrosSemanticos.TIPO_INEXISTENTE);
                    }
                    else {
                        for (var id : it.identifier()) {
                            for (var ident : id.IDENT()) {
                                String nome = identfier.getText() + "." + ident.getText();
                                if (escopos.obterEscopoAtual().existe(nome)) {
                                    var entradaTabela = escopos.obterEscopoAtual().verificar(nome);
                                    SemanticoUtils.adicionaErroSemantico(entradaTabela, ident.getSymbol().getLine(), ErrosSemanticos.IDENTIFICADOR_EXISTENTE);
                                } else {
                                    var ponteiro = false;
                                    if (it.type().extend_type() != null) {
                                        ponteiro = it.type().extend_type().ESTENDIDO() != null;
                                    }
                                    escopos.obterEscopoAtual().inserir(nome, tipo, ponteiro);
                                }
                            }
                        }
                    }

                });
            });
        }


        return null;
    }


    @Override
    public Void visitRegister(AlParser.RegisterContext ctx) {


        return null;
    }

    @Override
    public Void visitCmd_read(AlParser.Cmd_readContext ctx) {

        ctx.identifier().forEach(it -> {
            var test = it.getText();
            if (!escopos.obterEscopoAtual().existe(it.getText())){
                 SemanticoUtils.adicionaErroSemantico(it.getText(), it.getStart().getLine(), ErrosSemanticos.IDENTIFICADOR_INEXISTENTE);
            }
        });

        return null;
    }

    @Override
    public Void visitCmd_write(AlParser.Cmd_writeContext ctx) {

        ctx.arithmetic_exp().forEach(it -> visitArithmetic_exp(it));

        return null;
    }

    @Override
    public Void visitArithmetic_exp(AlParser.Arithmetic_expContext ctx) {
        var test = ctx.getText();
        ctx.term().forEach(it -> visitTerm(it));

        return null;
    }

    @Override
    public Void visitTerm(AlParser.TermContext ctx) {

        var test = ctx.getText();

        ctx.factor().forEach(it -> {
            if (ctx.op2() != null) {
                SemanticoUtils.validaFatoracao(ctx, ctx.op2());
            }
            visitFactor(it);
        });


        return null;
    }

    @Override
    public Void visitFactor(AlParser.FactorContext ctx) {
        var test = ctx.getText();
        ctx.parcel().forEach(it -> visitParcel(it));

        return null;
    }

    @Override
    public Void visitParcel(AlParser.ParcelContext ctx) {
        var test = ctx.getText();
        if (ctx.single_parcel() != null){
            visitSingle_parcel(ctx.single_parcel());
        }
        else if(ctx.non_unary_portion() != null){
            visitNon_unary_portion(ctx.non_unary_portion());
        }

        return null;
    }

    @Override
    public Void visitSingle_parcel(AlParser.Single_parcelContext ctx) {

        var ctxx = ctx.getText();

        if (ctx.identifier() != null) {
            var parcel = escopos.obterEscopoAtual().verificar(ctx.getText());
            if (!escopos.obterEscopoAtual().existe(ctx.getText()) && !SemanticoUtils.getErrosSemanticos().contains(ctx.getText())){
                SemanticoUtils.adicionaErroSemantico(ctx.identifier().getText(), ctx.identifier().getStart().getLine(), ErrosSemanticos.IDENTIFICADOR_INEXISTENTE);
            }
            else if(parcel != null && SemanticoUtils.getIdentifierEntradaTabela() != null && parcel.tipo != SemanticoUtils.getIdentifierEntradaTabela().tipo && !SemanticoUtils.getErrosSemanticos().contains(ctx.getText())) {
                var checkLogicalTerm = SemanticoUtils.getExpression().getText();
                if (!checkLogicalTerm.contains("<") && !checkLogicalTerm.contains(">") && !checkLogicalTerm.contains("=") && !checkLogicalTerm.contains("<>") && !checkLogicalTerm.contains("<=") && !checkLogicalTerm.contains(">="))
                    SemanticoUtils.adicionaErroSemantico(SemanticoUtils.getIdentifierEntradaTabela(), SemanticoUtils.getIdentifier().getStart().getLine(), ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
            }
        }
        else if(ctx.NUM_INT() != null) {
            if (SemanticoUtils.getIdentifierEntradaTabela() != null && !SemanticoUtils.getIdentifierEntradaTabela().tipo.equals(TipoAl.INTEIRO)){
                if(SemanticoUtils.getIdentifierEntradaTabela().tipo == TipoAl.REAL && ctx.NUM_INT().getText().equals("0")){
                    return null;
                }
                SemanticoUtils.adicionaErroSemantico(SemanticoUtils.getIdentifierEntradaTabela(), SemanticoUtils.getIdentifier().getStart().getLine(), ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
            }
        }
        else if(ctx.NUM_REAL() != null){
            if (SemanticoUtils.getIdentifierEntradaTabela() != null && !SemanticoUtils.getIdentifierEntradaTabela().tipo.equals(TipoAl.REAL)){
                SemanticoUtils.adicionaErroSemantico(SemanticoUtils.getIdentifierEntradaTabela(), SemanticoUtils.getIdentifier().getStart().getLine(), ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
            }
        }
        else if(ctx.expression() != null){
            ctx.expression().forEach(it -> visitExpression(it));
        }

        return null;
    }

    @Override
    public Void visitNon_unary_portion(AlParser.Non_unary_portionContext ctx) {

        var test = ctx.getText();

        if (ctx.OP_E_COMERCIAL() != null){
            var nome = ctx.getText().replace("&", "");
            var identEntradaTabelaDeSimbolos = escopos.obterEscopoAtual().verificar(nome);
            if (SemanticoUtils.getIdentifierEntradaTabela().tipo != identEntradaTabelaDeSimbolos.tipo){
                SemanticoUtils.adicionaErroSemantico(SemanticoUtils.getIdentifierEntradaTabela(), SemanticoUtils.getIdentifier().getStart().getLine(), ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
            }
        }
        else if(ctx.CADEIA() != null){
            if (SemanticoUtils.getIdentifierEntradaTabela() != null && SemanticoUtils.getIdentifierEntradaTabela().tipo != TipoAl.CADEIA){
                if (SemanticoUtils.getIdentifier().getText().contains("["))
                    SemanticoUtils.adicionaErroSemantico(SemanticoUtils.getIdentifier().getText(), SemanticoUtils.getIdentifier().getStart().getLine(), ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
                else
                    SemanticoUtils.adicionaErroSemantico(SemanticoUtils.getIdentifierEntradaTabela(), SemanticoUtils.getIdentifier().getStart().getLine(), ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
            }
        }

        return null;
    }

    @Override
    public Void visitCmd_assignment(AlParser.Cmd_assignmentContext ctx) {

        var nome = ctx.identifier().getText();
        var ident = escopos.obterEscopoAtual().verificar(nome);

        if(ident != null){
            SemanticoUtils.setValidacaoAtribuicao(ctx.identifier(), ident, ctx.expression());
            visitExpression(ctx.expression());
            SemanticoUtils.setValidacaoAtribuicao(null, null, null);
        }
        else {
            SemanticoUtils.adicionaErroSemantico(nome, ctx.identifier().getStart().getLine(), ErrosSemanticos.IDENTIFICADOR_INEXISTENTE);
        }
        return null;
    }

    @Override
    public Void visitExpression(AlParser.ExpressionContext ctx) {
        var test = ctx.getText();
        var identifierLeft = ctx.getParent().getText().split("<-")[0];
        var identLeft = escopos.obterEscopoAtual().verificar(identifierLeft);

        ctx.logical_term().forEach(it -> {
            expressaoComparacao.add(it.getStart());
            visitLogical_term(it);
        });

        return null;
    }

    @Override
    public Void visitLogical_term(AlParser.Logical_termContext ctx) {

        ctx.logical_factor().forEach(it -> visitLogical_factor(it));

        return null;
    }

    @Override
    public Void visitLogical_factor(AlParser.Logical_factorContext ctx) {

        visitLogical_plot(ctx.logical_plot());

        return null;
    }

    @Override
    public Void visitLogical_plot(AlParser.Logical_plotContext ctx) {

        if (ctx.relational_exp() != null){
            visitRelational_exp(ctx.relational_exp());
        }
        else if(ctx.VERDADEIRO() != null || ctx.FALSO() != null){
            if (SemanticoUtils.getIdentifier() != null && SemanticoUtils.getIdentifierEntradaTabela().tipo != TipoAl.LOGICO){
                SemanticoUtils.adicionaErroSemantico(SemanticoUtils.getIdentifierEntradaTabela(), SemanticoUtils.getIdentifier().getStart().getLine(), ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
            }
        }

        return null;
    }

    @Override
    public Void visitRelational_exp(AlParser.Relational_expContext ctx) {

        ctx.arithmetic_exp().forEach(it -> visitArithmetic_exp(it));

        return null;
    }
}
