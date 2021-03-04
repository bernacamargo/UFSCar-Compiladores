package br.ufscar;

import java.io.*;

public class LeitorDeArquivos {
    private final static int TAMANHO_BUFFER = 20;
    int[] bufferDeLeitura;
    int ponteiro;
    int linhaAtual;
    InputStream is;
    int bufferAtual;
    int inicioLexema;
    private String lexema;

    public LeitorDeArquivos(String arquivo) {
        try {
            is = new FileInputStream(new File(arquivo));
            inicializarBuffer();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void inicializarBuffer(){
        bufferAtual = 2;
        inicioLexema = 0;
        lexema = "";
        bufferDeLeitura = new int[TAMANHO_BUFFER * 2];
        ponteiro = 0;
        linhaAtual = 1;
        recarregarBuffer1();
    }

    private void recarregarBuffer1() {
        if (bufferAtual == 2) {
            bufferAtual = 1;

            for (int i = 0; i < TAMANHO_BUFFER; i++) {
                try {
                    bufferDeLeitura[i] = is.read();
                    if (bufferDeLeitura[i] == -1)
                        break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void recarregarBuffer2() {
        if (bufferAtual == 1) {
            bufferAtual = 2;

            for (int i = TAMANHO_BUFFER; i < TAMANHO_BUFFER * 2; i++) {
                try {
                    bufferDeLeitura[i] = is.read();
                    if (bufferDeLeitura[i] == -1)
                        break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void incrementarPonteiro(){
        ponteiro++;

        if(ponteiro == TAMANHO_BUFFER){
            recarregarBuffer2();
        }
        else if(ponteiro == TAMANHO_BUFFER * 2) {
            recarregarBuffer1();
            ponteiro = 0;
        }
    }

    public void incrementarLinha(){
        linhaAtual++;
//        System.out.println(linhaAtual);
    }

    private int lerCaractereDoBuffer(){
        int ret = bufferDeLeitura[ponteiro];
        incrementarPonteiro();
        return ret;
    }

    public int lerProximoCaractere () {
        int c = lerCaractereDoBuffer();
        lexema += (char) c;
        return c;
    }

    public void retroceder(){
        ponteiro--;
        lexema = lexema.substring(0, lexema.length() -1);
        if(ponteiro < 0){
            ponteiro = (TAMANHO_BUFFER * 2) - 1;
        }
    }

    public void zerar(){
        ponteiro = inicioLexema;
        lexema = "";
    }

    public void confirmar(){
        inicioLexema = ponteiro;
        lexema = "";
    }

    public String getLexema() {
        return lexema;
    }

    @Override
    public String toString() {
        String ret = "Buffer:[";
        for (int i : bufferDeLeitura) {
            char c = (char) i;
            if (Character.isWhitespace(c)) {
                ret += ' ';
            } else {
                ret += (char) i;
            }
        }
        ret += "]\n";
        ret += "        ";
        for (int i = 0; i < TAMANHO_BUFFER * 2; i++) {
            if (i == inicioLexema && i == ponteiro) {
                ret += "%";
            } else if (i == inicioLexema) {
                ret += "^";
            } else if (i == ponteiro) {
                ret += "*";
            } else {
                ret += " ";
            }
        }
        return ret;
    }

}
