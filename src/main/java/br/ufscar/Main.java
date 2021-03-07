package br.ufscar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Validação dos parâmetros do programa
        if(args.length != 2){
            throw new Error("Para executar este programa são necessários dois parâmetros: " +
                    "1. O caminho absoluto do arquivo de entrada" +
                    "2. O caminho absoluto do arquivo de saída");
        }

        String inputFilePath = args[0];
        String outputFIlePath = args[1];
        // Cria a instância do analalisador lexico passando o caminho do arquivo de entrada como parâmetro
        AnalisadorLexico lex = new AnalisadorLexico(inputFilePath);
        // Cria a instância do escritor de arquivos passando o caminho do arquivo de saída como parâmetro
        EscritorDeArquivos escritor = new EscritorDeArquivos(outputFIlePath);

        Token t;
        List<Token> tokenList = new ArrayList<>();

        // Percorre o arquivo de entrada, criando os tokens
        while(true){
            // Busca pelo próximo token
            t = lex.proximoToken();
            if (t != null){ // Caso encontre um token
                // Adiciona na lista de tokens
                tokenList.add(t);

                // Verifica se chegou ao fim do algoritmo
                if(t.getNome().equals("fim_algoritmo")){
                    break; // Finaliza o programa
                }
            }
            else{ // Caso não encontre um próximo token
                break; // Finaliza o programa
            }
        }

        try{
            // Escreve o arquivo de saída com os respectivos tokens encontrados
            escritor.criaOutput(tokenList, lex.erroLexico);
        }
        catch (IOException e){
            e.printStackTrace();
        }


    }
}
