package br.ufscar;

import org.antlr.v4.runtime.Token;

import java.util.Optional;

public class SemanticoVisitor extends AlBaseVisitor<Void> {

    Escopos escopos;
    EntradaTabelaDeSimbolos identComp = null;
    Token identCompToken = null;

    @Override
    public Void visitProgr(AlParser.ProgrContext ctx) {
        escopos = new Escopos();
        visitBody(ctx.body());

        escopos.percorrerEscoposAninhados().forEach(it -> {
            System.out.println(it.toString());
        });

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

        return null;
    }

    @Override
    public Void visitVariable(AlParser.VariableContext ctx) {

        TipoAl tipo = SemanticoUtils.verificarTipo(ctx.type().getText());
        if (tipo == null){
            SemanticoUtils.adicionaErroSemantico(ctx.type().getStart(), ErrosSemanticos.TIPO_INEXISTENTE);
        }
        else {
            for(var id: ctx.identifier()){
                for(var ident: id.IDENT()){
                    String nome = ident.getText();
                    if(escopos.obterEscopoAtual().existe(nome)){
                        SemanticoUtils.adicionaErroSemantico(ident.getSymbol(), ErrosSemanticos.IDENTIFICADOR_EXISTENTE);
                    } else {
                        escopos.obterEscopoAtual().inserir(nome, tipo, false);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Void visitCmd_read(AlParser.Cmd_readContext ctx) {

        ctx.identifier().forEach(it -> {
            var test = it.getText();
            if (!escopos.obterEscopoAtual().existe(it.getText())){
                 SemanticoUtils.adicionaErroSemantico(it.getStart(), ErrosSemanticos.IDENTIFICADOR_INEXISTENTE);
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

        ctx.term().forEach(it -> visitTerm(it));

        return null;
    }

    @Override
    public Void visitTerm(AlParser.TermContext ctx) {

        var test = ctx.getText();

        ctx.factor().forEach(it -> visitFactor(it));

        return null;
    }

    @Override
    public Void visitFactor(AlParser.FactorContext ctx) {

        ctx.parcel().forEach(it -> visitParcel(it));

        return null;
    }

    @Override
    public Void visitParcel(AlParser.ParcelContext ctx) {

        if (ctx.single_parcel() != null){
            visitSingle_parcel(ctx.single_parcel());
        }
        else if(ctx.non_unary_portion() != null){
            if (identComp != null && identComp.tipo != TipoAl.CADEIA){
                SemanticoUtils.adicionaErroSemantico(identCompToken, ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
            }
        }

        return null;
    }

    @Override
    public Void visitSingle_parcel(AlParser.Single_parcelContext ctx) {

        var ctxx = ctx.getText();

        if (ctx.identifier() != null) {
            var parcel = escopos.obterEscopoAtual().verificar(ctx.getText());

            if (!escopos.obterEscopoAtual().existe(ctx.getText()) && !SemanticoUtils.getErrosSemanticos().contains(ctx.getText())){
                SemanticoUtils.adicionaErroSemantico(ctx.getStart(), ErrosSemanticos.IDENTIFICADOR_INEXISTENTE);
            }
            else if(parcel != null && identComp != null && parcel.tipo != identComp.tipo && !SemanticoUtils.getErrosSemanticos().contains(ctx.getText())) {
                var checkLogicalTerm = Optional.ofNullable(ctx.parent.parent.parent.parent.parent.getText()).orElse(null);
                if (checkLogicalTerm != null && !checkLogicalTerm.contains("<") && !checkLogicalTerm.contains(">") && !checkLogicalTerm.contains("=") && !checkLogicalTerm.contains("<>") && !checkLogicalTerm.contains("<=") && !checkLogicalTerm.contains(">="))
                    SemanticoUtils.adicionaErroSemantico(identCompToken, ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
            }
        }
        else if(ctx.NUM_INT() != null){
            String checkDivisorMult2;
            var test = Optional.ofNullable(ctx.parent.parent.parent.getText().split(ctx.NUM_INT().getText())).orElse(null);
            if (test.length >= 2) {
                checkDivisorMult2 = test[1];
            }
            else{
                checkDivisorMult2 = null;
            }
            if (identComp != null && !identComp.tipo.equals(TipoAl.INTEIRO)){
                if(checkDivisorMult2 != null && (checkDivisorMult2.contains("/") || checkDivisorMult2.contains("*"))){
                    return null;
                }
                if(identComp.tipo == TipoAl.REAL && ctx.NUM_INT().getText().equals("0")){
                    return null;
                }
                SemanticoUtils.adicionaErroSemantico(identCompToken, ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
            }
        }
        else if(ctx.NUM_REAL() != null){
            if (identComp != null && !identComp.tipo.equals(TipoAl.REAL)){
                SemanticoUtils.adicionaErroSemantico(identCompToken, ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
            }
        }

        return null;
    }

    @Override
    public Void visitCmd_assignment(AlParser.Cmd_assignmentContext ctx) {

        var nome = ctx.identifier().getText();
        var ident = escopos.obterEscopoAtual().verificar(nome);
        identComp = ident;
        identCompToken = ctx.identifier().getStart();
        visitExpression(ctx.expression());
        identComp = null;
        identCompToken = null;
        return null;
    }

    @Override
    public Void visitExpression(AlParser.ExpressionContext ctx) {
        var identifierLeft = ctx.getParent().getText().split("<-")[0];
        var identLeft = escopos.obterEscopoAtual().verificar(identifierLeft);

        ctx.logical_term().forEach(it -> visitLogical_term(it));

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
            if (identComp != null && identComp.tipo != TipoAl.LOGICO){
                SemanticoUtils.adicionaErroSemantico(identCompToken, ErrosSemanticos.ATRIBUICAO_INCOMPATIVEL);
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
