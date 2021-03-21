package br.ufscar;

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
        saida.append("\n");
            visitDeclarations(ctx.declarations());
        saida.append("int main() {\n");
            visitBody(ctx.body());
        saida.append("return 0;\n}\n");

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
        ctx.local_decl_global().forEach(ldg -> {
            visitLocal_declaration(ldg.local_declaration());
//            if(ldg.global_declaration().local_declaration().size() > 0)
//                ldg.global_declaration().local_declaration().forEach(ld -> visitLocal_declaration(ld));
        });

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

        return null;
    }

    @Override
    public Void visitVariable(AlParser.VariableContext ctx) {
        String nomeVar = "";
        String strTipoVar = ctx.type().getText();
        TipoAl tipoVar = TipoAl.INVALIDO;

        switch (strTipoVar){
            case "inteiro":
                tipoVar = TipoAl.INTEIRO;
                strTipoVar = "int";
                break;
            case "real":
                tipoVar = TipoAl.REAL;
                strTipoVar = "float";
                break;
            case "literal":
                tipoVar = TipoAl.CADEIA;
                strTipoVar = "char";
                break;
        }

        for(int i=0; i < ctx.identifier().size(); i++){
            nomeVar = ctx.identifier(i).getText();
            tabela.inserir(nomeVar, tipoVar);
            saida.append(strTipoVar).append(" ").append(nomeVar);
            if(tipoVar.equals(TipoAl.CADEIA)){
                saida.append("[80]");
            }
            saida.append(";\n");
        }


        return null;
    }

    @Override
    public Void visitIdentifier(AlParser.IdentifierContext ctx) {

        return null;
    }

    @Override
    public Void visitCmd_read(AlParser.Cmd_readContext ctx) {
        String nomeVar = ctx.identifier().get(0).getText();
        TipoAl tipoVar = tabela.verificar(nomeVar).tipo;
        String tipoVarParam = "";
        switch (tipoVar){
            case INTEIRO:
                tipoVarParam = "%d";
                break;
            case REAL:
                tipoVarParam = "%f";
                break;
            case CADEIA:
                tipoVarParam = "%s";
                break;
        }
        saida.append("scanf(\""+tipoVarParam+"\", &").append(nomeVar).append(");\n");
        return null;
    }

    @Override
    public Void visitCmd_write(AlParser.Cmd_writeContext ctx) {
        String nomeVar = "";
        TipoAl tipoVar = TipoAl.INVALIDO;
        String tipoVarParam = "";
        if (ctx.CADEIA() != null) {
            String aux = ctx.CADEIA().getText();
            aux = aux.substring(1, aux.length() - 1);
            if(ctx.arithmetic_exp() != null){
                nomeVar = ctx.arithmetic_exp().term(0).getText();
                tipoVar = tabela.verificar(nomeVar).tipo;
                switch (tipoVar){
                    case INTEIRO:
                        tipoVarParam = "%d";
                        break;
                    case REAL:
                        tipoVarParam = "%f";
                        break;
                    case CADEIA:
                        tipoVarParam = "%s";
                        break;
                }

                saida.append("printf(\"" + aux + tipoVarParam +"\", ");
                visitArithmetic_exp(ctx.arithmetic_exp());
                saida.append(");\n");
            }
            else {
                saida.append("printf(\"" + aux + "\");\n");
            }
        } else {
            nomeVar = ctx.arithmetic_exp().term(0).getText();
            tipoVar = tabela.verificar(nomeVar).tipo;
            switch (tipoVar){
                case INTEIRO:
                    tipoVarParam = "%d";
                    break;
                case REAL:
                    tipoVarParam = "%f";
                    break;
                case CADEIA:
                    tipoVarParam = "%s";
                    break;
            }
            saida.append("printf(\"" + tipoVarParam + "\", ");
            visitArithmetic_exp(ctx.arithmetic_exp());
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
        visitRelational_exp(ctx.logical_plot().relational_exp());
        return null;
    }

    @Override
    public Void visitRelational_exp(AlParser.Relational_expContext ctx) {
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

        for(int i=0; i < ctx.term().size(); i++){
            visitTerm(ctx.term(i));
            if(ctx.op1().size() > 0 && i != ctx.term().size()-1){
                saida.append(" " + ctx.op1(0).getText() + " ");
            }
        }

        return null;
    }

    @Override
    public Void visitTerm(AlParser.TermContext ctx) {

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
        saida.append(ctx.identifier().getText());
        saida.append(" = ");
        visitExpression(ctx.expression());
        saida.append(";\n");
        return null;
    }
}
