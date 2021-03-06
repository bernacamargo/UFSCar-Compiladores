package br.ufscar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        AnalisadorLexico lex = new AnalisadorLexico(args[0]);
        Token t = null;
        List<Token> tokenList = new ArrayList<>();
        String outputFIlePath = args[1];
        EscritorDeArquivos escritor = new EscritorDeArquivos(outputFIlePath);

        while(true){
            t = lex.proximoToken();
            if (t != null){
                tokenList.add(t);
                if(t.getNome().equals("fim_algoritmo")){
                    break;
                }
            }
            else{
                break;
            }
        }

        try{
            escritor.criaOutput(tokenList, lex.erroLexico);
        }
        catch (IOException e){
            e.printStackTrace();
        }


    }
}
