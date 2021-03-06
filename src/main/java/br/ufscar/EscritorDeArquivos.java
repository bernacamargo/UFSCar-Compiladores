package br.ufscar;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class EscritorDeArquivos {
    String filePath;

    public EscritorDeArquivos(String filePath) {
        this.filePath = filePath;
    }

    public void criaOutput(List<Token> tokenList, ErroLexico erroLexico) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
        Token t;
        for (Token token : tokenList) {
            t = token;

            bufferedWriter
                    .append(t.toString())
                    .append('\n');
        }

        if(erroLexico.getTemErroLexio()){
            bufferedWriter
                    .append(erroLexico.getMensagem())
                    .append('\n');
        }

        bufferedWriter.close();
    }
}
