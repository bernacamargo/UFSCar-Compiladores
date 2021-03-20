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

        ctx.declarations().local_decl_global().forEach(dec -> visitLocal_decl_global(dec));

        saida.append("\n");
        saida.append("int main() {\n");

        ctx.body().cmd().forEach(cmd -> visitCmd(cmd));

        return super.visitProgr(ctx);
    }

    @Override
    public Void visitLocal_decl_global(AlParser.Local_decl_globalContext ctx) {
        String nomeVar = ctx.local_declaration().variable().getText();
        String strTipoVar = ctx.local_declaration().TIPOW().getText();

        switch (strTipoVar){
            case "inteiro":
                strTipoVar = "int";
                break;
            case "real":
                strTipoVar = "float";
                break;
        }
        saida.append(strTipoVar).append(" ").append(nomeVar).append(";\n");
        return null;
    }
}
