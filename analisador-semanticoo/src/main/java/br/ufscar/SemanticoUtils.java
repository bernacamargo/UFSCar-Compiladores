package br.ufscar;

import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class SemanticoUtils {

    public static List<String> errosSemanticos = new ArrayList<>();

    public static void adicionaErroSemantico(Token token, ErrosSemanticos tipoErro){

        if (getErrosSemanticos().contains(token.getText())){
            return;
        }

        StringBuilder erroSemantico = new StringBuilder();
        var linha = token.getLine();
        var variavel = token.getText();

        erroSemantico.append("Linha ").append(linha).append(": ");
        switch (tipoErro){
            case IDENTIFICADOR_EXISTENTE:
                erroSemantico.append("identificador " + variavel + " ja declarado anteriormente\n");
                break;
            case IDENTIFICADOR_INEXISTENTE:
                erroSemantico.append("identificador " + variavel + " nao declarado\n");
                break;
            case TIPO_INEXISTENTE:
                erroSemantico.append("tipo " + variavel + " nao declarado\n");
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

    public static TipoAl verificarTipo(String tipo){

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

        saida.append("Fim da compilacao\n");

        return saida.toString();
    }

}
