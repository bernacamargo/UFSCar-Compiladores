package br.ufscar;

import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class SemanticoUtils {

    public static List<String> errosSemanticos = new ArrayList<>();
    private static AlParser.IdentifierContext identifier = null;
    private static AlParser.ExpressionContext expression = null;
    private static EntradaTabelaDeSimbolos identifierEntradaTabela = null;

    public static void adicionaErroSemantico(EntradaTabelaDeSimbolos entradaTabelaDeSimbolos, int linha, ErrosSemanticos tipoErro){

        var variavel = entradaTabelaDeSimbolos.nome;

        if (entradaTabelaDeSimbolos.ponteiro){
            variavel = "^" + variavel;
        }

        if (getErrosSemanticos().contains(variavel)){
            return;
        }

        StringBuilder erroSemantico = new StringBuilder();

        erroSemantico.append("Linha ").append(linha).append(": ");

        switch (tipoErro){
            case IDENTIFICADOR_EXISTENTE:
                erroSemantico.append("identificador " + variavel + " ja declarado anteriormente\n");
                break;
            case ATRIBUICAO_INCOMPATIVEL:
                erroSemantico.append("atribuicao nao compativel para " + variavel + "\n");
                break;
            case PARAMETROS_INCOMPATIVEIS:
                erroSemantico.append("incompatibilidade de parametros na chamada de " + variavel + "\n");
                break;
            case ESCOPO_INVALIDO:
                erroSemantico.append("comando retorne nao permitido nesse escopo\n");
                break;
        }

        errosSemanticos.add(erroSemantico.toString());
    }

    public static void adicionaErroSemantico(String variavel, int linha, ErrosSemanticos tipoErro) {

        if (getErrosSemanticos().contains(variavel)){
            return;
        }

        StringBuilder erroSemantico = new StringBuilder();

        erroSemantico.append("Linha ").append(linha).append(": ");

        switch (tipoErro) {
            case IDENTIFICADOR_INEXISTENTE:
                erroSemantico.append("identificador " + variavel + " nao declarado\n");
                break;
            case TIPO_INEXISTENTE:
                erroSemantico.append("tipo " + variavel + " nao declarado\n");
                break;
            case ATRIBUICAO_INCOMPATIVEL:
                erroSemantico.append("atribuicao nao compativel para " + variavel + "\n");
                break;
        }

        errosSemanticos.add(erroSemantico.toString());
    }

        public static TipoAl verificarTipo(String tipo){

        if (tipo.contains("^")){
            tipo = tipo.replace("^", "");
        }

        switch (tipo){
            case "inteiro":
                return TipoAl.INTEIRO;
            case "real":
                return TipoAl.REAL;
            case "literal":
                return TipoAl.CADEIA;
            case "logico":
                return TipoAl.LOGICO;
            default:
                return null;
        }

    }

    public static void exibeErrosSemanticos() {
        System.out.println("\n");
        errosSemanticos.forEach(it -> System.out.println(it));
    }

    public static String getErrosSemanticos() {
        StringBuilder saida = new StringBuilder();

        for(String erro: errosSemanticos){
            saida.append(erro);
        }

        if(saida.length() > 0)
            saida.append("Fim da compilacao\n");

        return saida.toString();
    }

    public static void setValidacaoAtribuicao(AlParser.IdentifierContext identifier, EntradaTabelaDeSimbolos identifierEntradaTabela, AlParser.ExpressionContext expression){
        SemanticoUtils.identifier = identifier;
        SemanticoUtils.expression = expression;
        SemanticoUtils.identifierEntradaTabela = identifierEntradaTabela;
        var test = "";
    }

    public static AlParser.IdentifierContext getIdentifier() {
        return identifier;
    }

    public static AlParser.ExpressionContext getExpression() {
        return expression;
    }

    public static EntradaTabelaDeSimbolos getIdentifierEntradaTabela() {
        return identifierEntradaTabela;
    }

    public static void validaFatoracao(AlParser.TermContext term, List<AlParser.Op2Context> op2List) {
        var termText = term.getText();
        var test2 = term.getText();
//
//        term.factor().forEach((AlParser.FactorContext it) -> {
//            System.out.println(it.getText());
//        });
//
//        op2List.forEach(op2 -> {
////            termText.split(op2.getText());
//            System.out.println(op2.getText());
//        });
    }
}
