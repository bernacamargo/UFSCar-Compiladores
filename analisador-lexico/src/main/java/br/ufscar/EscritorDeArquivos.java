package br.ufscar;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * EscritorDeArquivos
 *
 * Responsavel por escrever no arquivo de saída os respectivos tokens encontrados
 */
public class EscritorDeArquivos {
    String filePath; // Caminho absoluto do arquivo de saída

    /**
     * EscritorDeArquivos()
     *
     * Construtor da classe
     *
     * @param filePath - Caminho para arquivo de saída
     */
    public EscritorDeArquivos(String filePath) {
        this.filePath = filePath;
    }

    /**
     * criaOutput()
     *
     * Percorre a lista de tokens e escreve no arquivo
     *
     * @param tokenList - Lista de tokens obtidos pelo analisador
     * @param erroLexico - Validações de erro lexico
     * @throws IOException - Validações de input/output
     */
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
