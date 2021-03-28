package br.ufscar;

import static br.ufscar.TipoAl.REAL;
import static br.ufscar.GeradorUtils.verificaTipoVarParam;

public class GeradorCodigoC extends AlBaseVisitor<Void>{
    StringBuilder saida;
    TabelaDeSimbolos tabela;

    public GeradorCodigoC() {
        saida = new StringBuilder();
        this.tabela = new TabelaDeSimbolos();
    }

    @Override
    public Void visitProgr(AlParser.ProgrContext ctx) {
        saida.append("#include <stdio.h>\n");
        saida.append("#include <stdlib.h>\n");
        saida.append("#include <string.h>\n");
        saida.append("\n");
            visitDeclarations(ctx.declarations());
        saida.append("int main() {\n");
            visitBody(ctx.body());
        saida.append("return 0;\n}\n");
        System.out.println(tabela.toString()); // imprime a tabela de simbolos
        return null;
    }

    @Override
    public Void visitBody(AlParser.BodyContext ctx) {
        ctx.local_declaration().forEach(ld -> visitLocal_declaration(ld));
        ctx.cmd().forEach(cmd -> visitCmd(cmd));

        return null;
    }

    @Override
    public Void visitDeclarations(AlParser.DeclarationsContext ctx) {
        if(ctx != null){
            ctx.local_decl_global().forEach(ldg -> {
                visitLocal_declaration(ldg.local_declaration());
            });
        }

        return null;
    }

    @Override
    public Void visitLocal_declaration(AlParser.Local_declarationContext ctx) {

        if (ctx.variable() != null)
            visitVariable(ctx.variable());
        else if(ctx.basic_type() != null) {
            saida.append("#define " + ctx.IDENT().getText() + " " + ctx.const_value().getText());
            saida.append("\n");
        }
        else if(ctx.TIPOW() != null){
            saida.append("typedef ");
            visitType(ctx.type());
            saida.append(ctx.IDENT() + ";\n");
        }

        return null;
    }

    @Override
    public Void visitVariable(AlParser.VariableContext ctx) {
        String nomeVar = "";
        String strTipoVar = ctx.type().getText();
        TipoAl tipoVar = TipoAl.INVALIDO;
        boolean ponteiro = false;

        if (ctx.type().register() != null) {
            visitRegister(ctx.type().register());
            for(int i=0; i < ctx.identifier().size(); i++) {
                nomeVar = ctx.identifier(i).getText();
                saida.append(nomeVar);
                saida.append(";\n");
            }
        }
        else if (ctx.type().extend_type() != null) {
            strTipoVar = strTipoVar.replace("^", "");
            switch (strTipoVar) {
                case "inteiro":
                    tipoVar = TipoAl.INTEIRO;
                    strTipoVar = "int";
                    break;
                case "real":
                    tipoVar = REAL;
                    strTipoVar = "float";
                    break;
                case "literal":
                    tipoVar = TipoAl.CADEIA;
                    strTipoVar = "char";
                    break;
                default:
                    tipoVar = TipoAl.TYPEDEF;
            }

            if (ctx.type().extend_type().ESTENDIDO() != null){
                strTipoVar = strTipoVar.concat("*");
                ponteiro = true;
            }


            for(int i=0; i < ctx.identifier().size(); i++){
                nomeVar = ctx.identifier(i).getText();

                // Tipos customizados
                if (tipoVar == TipoAl.TYPEDEF){
                    tabela.verificarTipoCustomizados(nomeVar, strTipoVar);
                }
                else {
                    tabela.inserir(nomeVar, tipoVar, ponteiro);
                }

                saida.append(strTipoVar).append(" ").append(nomeVar);
                if(tipoVar.equals(TipoAl.CADEIA)){
                    saida.append("[80]");
                }
                saida.append(";\n");
            }
        }


        return null;
    }

    @Override
    public Void visitType(AlParser.TypeContext ctx) {

        if (ctx.extend_type() != null){
            saida.append(ctx.extend_type().getText());
        }
        else if(ctx.register() != null){
            visitRegister(ctx.register());
        }

        return null;
    }

    @Override
    public Void visitCmd_read(AlParser.Cmd_readContext ctx) {
        String nomeVar = ctx.identifier().get(0).getText();
        TipoAl tipoVar = tabela.verificar(nomeVar).tipo;
        String tipoVarParam = "";
        tipoVarParam = verificaTipoVarParam(tipoVar);

        saida.append("scanf(\""+tipoVarParam+"\", &").append(nomeVar).append(");\n");
        return null;
    }

//    public Void visitcmd_write(AlParser.Cmd_writeContext ctx){
//        String nomeVar = "";
//        TipoAl tipoVar = TipoAl.INVALIDO;
//        String tipoVarParam = "";
//        String cadeia;
//
//        AlParser.Cadeia_afterContext cadeia_after = ctx.cadeia_after();
//        AlParser.Cadeia_beforeContext cadeia_before = ctx.cadeia_before();
//
//        if ((cadeia_after != null && cadeia_after.CADEIA() != null) ||
//                cadeia_before != null && cadeia_before.CADEIA() != null) {
//            if(cadeia_after != null){
//                cadeia = ctx.cadeia_after().CADEIA().getText();
//                cadeia = cadeia.substring(1, cadeia.length() - 1);
//                if(ctx.cadeia_after().arithmetic_exp() != null){
//                    nomeVar = ctx.cadeia_after().arithmetic_exp().term(0).getText();
//                    tipoVar = tabela.verificar(nomeVar).tipo;
//                    tipoVarParam = verificaTipoVarParam(tipoVar);
//
//                    saida.append("printf(\"" + tipoVarParam + cadeia +"\", ");
//                    visitArithmetic_exp(ctx.cadeia_after().arithmetic_exp());
//                    saida.append(");\n");
//                }
//                else {
//                    saida.append("printf(\"" + cadeia + "\");\n");
//                }
//
//            }
//            else{
//                cadeia = ctx.cadeia_before().CADEIA().getText();
//                cadeia = cadeia.substring(1, cadeia.length() - 1);
//                if(ctx.cadeia_before().arithmetic_exp() != null){
//                    nomeVar = ctx.cadeia_before().arithmetic_exp().term(0).getText();
//                    tipoVar = tabela.verificar(nomeVar).tipo;
//                    tipoVarParam = verificaTipoVarParam(tipoVar);
//
//                    saida.append("printf(\"" + cadeia + tipoVarParam +"\", ");
//                    visitArithmetic_exp(ctx.cadeia_before().arithmetic_exp());
//                    saida.append(");\n");
//                }
//                else {
//                    saida.append("printf(\"" + cadeia + "\");\n");
//                }
//            }
//        } else {
//            nomeVar = ctx.cadeia_after().arithmetic_exp().term(0).getText();
//            tipoVar = tabela.verificar(nomeVar).tipo;
//
//            tipoVarParam = verificaTipoVarParam(tipoVar);
//            saida.append("printf(\"" + tipoVarParam + "\", ");
//            visitArithmetic_exp(ctx.cadeia_after().arithmetic_exp());
//            saida.append(");\n");
//        }
//        return null;
//    }

    @Override
    public Void visitCmd_write(AlParser.Cmd_writeContext ctx) {
        String nomeVar;
        String tipoVar;
        EntradaTabelaDeSimbolos entradaTabelaDeSimbolos;
        for(int i=0; i < ctx.arithmetic_exp().size(); i++){
            saida.append("printf(");
            nomeVar = ctx.arithmetic_exp(i).term(0).getText();
            entradaTabelaDeSimbolos = tabela.verificar(nomeVar);
            if(entradaTabelaDeSimbolos != null){
                tipoVar = GeradorUtils.verificaTipoVarParam(entradaTabelaDeSimbolos.tipo);
                saida.append("\"").append(tipoVar).append("\", ");
            }
            visitArithmetic_exp(ctx.arithmetic_exp(i));
            saida.append(");\n");
        }

        return null;
    }

    @Override
    public Void visitLogical_op_1(AlParser.Logical_op_1Context ctx) {
        saida.append(" || ");
        return null;
    }

    @Override
    public Void visitLogical_op_2(AlParser.Logical_op_2Context ctx) {
        saida.append(" && ");
        return null;
    }

    @Override
    public Void visitCmd_case(AlParser.Cmd_caseContext ctx) {

        saida.append("switch(");
        visitArithmetic_exp(ctx.arithmetic_exp());
        saida.append("){\n");
        visitSelection(ctx.selection());

        if(ctx.SENAO() != null){
            saida.append("default:\n");
            visitCmd(ctx.cmd());
            saida.append("break;\n");
        }

        saida.append("}");

        return null;
    }

    @Override
    public Void visitSelection(AlParser.SelectionContext ctx) {

        ctx.selection_item().forEach(si -> visitSelection_item(si));

        return null;
    }

    @Override
    public Void visitSelection_item(AlParser.Selection_itemContext ctx) {
        visitConstant(ctx.constant());
        ctx.cmd().forEach(cmd -> {
            visitCmd(cmd);
            saida.append("break;\n");
        });
        return null;
    }

    @Override
    public Void visitConstant(AlParser.ConstantContext ctx) {
        ctx.interval_number().forEach(in -> visitInterval_number(in));
        return null;
    }

    @Override
    public Void visitInterval_number(AlParser.Interval_numberContext ctx) {

        int inicio = Integer.parseInt(ctx.NUM_INT(0).getText());
        int fim = inicio;
        if(ctx.NUM_INT().size() > 1){
            fim = Integer.parseInt(ctx.NUM_INT(1).getText());
        }
        for(int i=inicio; i <= fim; i++){
            saida.append("case ").append(i).append(":\n");
        }
        return null;
    }

    @Override
    public Void visitCmd_if(AlParser.Cmd_ifContext ctx) {
        saida.append("if").append("(");

        visitExpression(ctx.expression());

        saida.append(") {\n");

        visitCmd(ctx.cmd(0));

        saida.append("}\n");

        if(ctx.SENAO() != null){
            saida.append("else {\n");
            visitCmd(ctx.cmd(1));
            saida.append("}\n");
        }

        return null;
    }

    @Override
    public Void visitExpression(AlParser.ExpressionContext ctx) {
        var test = ctx.getText();

        for(int i=0; i < ctx.logical_term().size(); i++){
            visitLogical_term(ctx.logical_term(i));
            if(ctx.logical_op_1().size() > 0 && i != ctx.logical_term().size()-1){
                saida.append(" || ");
            }
        }
        return null;
    }

    @Override
    public Void visitLogical_term(AlParser.Logical_termContext ctx) {
        var test = ctx.getText();

        for(int i=0; i < ctx.logical_factor().size(); i++){
            visitLogical_factor(ctx.logical_factor(i));
            if(ctx.logical_op_2().size() > 0 && i != ctx.logical_factor().size()-1){
                saida.append(" && ");
            }
        }

        return null;
    }

    @Override
    public Void visitLogical_factor(AlParser.Logical_factorContext ctx) {
        var test = ctx.getText();

        if(ctx.NAO() != null){
            saida.append("!");
        }
        visitRelational_exp(ctx.logical_plot().relational_exp());
        return null;
    }

    @Override
    public Void visitRelational_exp(AlParser.Relational_expContext ctx) {
        var test = ctx.getText();

        visitArithmetic_exp(ctx.arithmetic_exp(0));
        if (ctx.relational_op() != null) {
            visitRelational_op(ctx.relational_op());
            visitArithmetic_exp(ctx.arithmetic_exp(1));
        }

        return null;
    }

    @Override
    public Void visitRelational_op(AlParser.Relational_opContext ctx) {
        String aux = ctx.getText();
        switch (aux){
            case "=":
                aux = "==";
                break;
            case "<>":
                aux = "!=";
                break;
        }
        saida.append(" " + aux + " ");
        return null;
    }

    @Override
    public Void visitArithmetic_exp(AlParser.Arithmetic_expContext ctx) {
        var test = ctx.getText();
        if(ctx.ABRE_PARENTESE() != null){
            saida.append("(");
        }
        for(int i=0; i < ctx.term().size(); i++){
            visitTerm(ctx.term(i));
            if(ctx.op1().size() > 0 && i != ctx.term().size()-1){
                saida.append(" " + ctx.op1(0).getText() + " ");
            }
        }
        if(ctx.FECHA_PARENTESE() != null){
            saida.append(")");
        }

        return null;
    }

    @Override
    public Void visitTerm(AlParser.TermContext ctx) {
        var test = ctx.getText();

        if(ctx.factor().size() > 0){
            for(int i=0; i < ctx.factor().size(); i++){
                visitFactor(ctx.factor(i));
                if(ctx.op2().size() > 0 && i != ctx.factor().size()-1){
                    saida.append(" " + ctx.op2(0).getText() + " ");
                }
            }
        }
        else{
            saida.append(ctx.getText());
        }

        return null;
    }

    @Override
    public Void visitFactor(AlParser.FactorContext ctx) {
        var test = ctx.getText();

        if(ctx.parcel().size() > 0){
            for(int i=0; i < ctx.parcel().size(); i++){
                visitParcel(ctx.parcel(i));
                if(ctx.op3().size() > 0 && i != ctx.parcel().size()-1){
                    saida.append(" " + ctx.op3(0).getText() + " ");
                }
            }
        }
        else{
            saida.append(ctx.getText());
        }
        return null;
    }

    @Override
    public Void visitParcel(AlParser.ParcelContext ctx) {

        saida.append(ctx.getText());

        return null;
    }


    @Override
    public Void visitCmd_assignment(AlParser.Cmd_assignmentContext ctx) {
        String nomeVar = ctx.identifier().getText();
        EntradaTabelaDeSimbolos tipoVar = tabela.verificar(nomeVar);
        boolean ponteiro = false;

        if (tipoVar != null) {
            ponteiro = tipoVar.ponteiro;
            if (ponteiro && !ctx.expression().getText().contains("&")) {
                nomeVar = "*" + nomeVar;
            }

            if(tipoVar.tipo.equals(TipoAl.CADEIA)) {
                saida.append("strcpy(");
                saida.append(nomeVar);
                saida.append(", ");
                visitExpression(ctx.expression());
                saida.append(")");
            }
            else {
                saida.append(nomeVar);
                saida.append(" = ");
                visitExpression(ctx.expression());
            }
        }

        saida.append(";\n");
        return null;
    }

    @Override
    public Void visitCmd_for(AlParser.Cmd_forContext ctx) {
        saida.append("for(").append(ctx.IDENT()).append("=");

        visitArithmetic_exp(ctx.arithmetic_exp(0));

        saida.append("; ").append(ctx.IDENT()).append(" <= ");

        visitArithmetic_exp(ctx.arithmetic_exp(1));

        saida.append("; ");

        saida.append(ctx.IDENT() + "++");

        saida.append("){\n");

        ctx.cmd().forEach(cmd -> visitCmd(cmd));

        saida.append("}\n");

        return null;
    }

    @Override
    public Void visitCmd_while(AlParser.Cmd_whileContext ctx) {
        saida.append("while(");
        visitExpression(ctx.expression());
        saida.append("){\n");
        ctx.cmd().forEach(cmd -> visitCmd(cmd));
        saida.append("}\n");
        return null;
    }

    @Override
    public Void visitCmd_do(AlParser.Cmd_doContext ctx) {
        saida.append("do {\n");

        ctx.cmd().forEach(cmd -> visitCmd(cmd));

        saida.append("} while(");

        visitExpression(ctx.expression());

        saida.append(");\n");

        return null;
    }


    @Override
    public Void visitRegister(AlParser.RegisterContext ctx) {
        String nomeVar;
        String strTipoVar;
        TipoAl tipoVar = TipoAl.INVALIDO;
        boolean ponteiro = false;
        String identifier = ctx.getParent().getParent().getPayload().getText().split(":")[0].replace("tipo", "");
        saida.append("struct {\n");
        for(int i=0; i < ctx.variable().size(); i++){
            visitVariable(ctx.variable(i));
            nomeVar = identifier + "." + ctx.variable(i).identifier(0).getText();
            strTipoVar = ctx.variable(i).type().getText();
            switch (strTipoVar){
                case "inteiro":
                    tipoVar = TipoAl.INTEIRO;
                    break;
                case "real":
                    tipoVar = REAL;
                    break;
                case "literal":
                    tipoVar = TipoAl.CADEIA;
                    break;
            }

            tabela.inserir(nomeVar, tipoVar, ponteiro);
        }

        saida.append("} ");

        return null;
    }
}
