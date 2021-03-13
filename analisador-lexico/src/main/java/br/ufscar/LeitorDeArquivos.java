package br.ufscar;

import java.io.*;

/**
 * LeitorDeArquivos
 *
 * Realiza a leitura do arquivo de entrada e executa a lógica para gerar os Tokens
 */
public class LeitorDeArquivos {
    InputStream is;
    private final static int TAMANHO_BUFFER = 20; // Define o tamanho do buffer de leitura
    int[] bufferDeLeitura; // Utilizado para armazenar uma pequena quantidade de caracteres e permitir percorre-los de forma a manipular o ponteiro de leitura para frente e para trás.
    int ponteiro; // Define a posição do buffer de leitura que está sendo lida
    int bufferAtual; // Define de qual buffer a leitura deve ser feita
    int linhaAtual; // Define a linha em que o analisador está atuando
    private String lexema; // Armazena os caracteres que representam o lexema que está sendo analisado
    int inicioLexema; // Registra o valor do ponteiro quando um lexema começa a ser analisado


    /**
     * LeitorDeArquivos()
     *
     * Construtor da classe. Inicializa o buffer de leitura
     *
     * @param arquivo String - Caminho absoluto para o arquivo de entrada
     */
    public LeitorDeArquivos(String arquivo) {
        try {
            is = new FileInputStream(new File(arquivo));
            inicializarBuffer();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * inicializarBuffer()
     *
     * Inicializa as variáveis responsáveis por controlar o buffer de leitura
     * e escreve na primeira metade do buffer os primeiros 20 caracteres do arquivo.
     *
     */
    private void inicializarBuffer(){
        bufferAtual = 2;
        inicioLexema = 0;
        lexema = "";
        bufferDeLeitura = new int[TAMANHO_BUFFER * 2];
        ponteiro = 0;
        linhaAtual = 1;
        recarregarBuffer1();
    }

    /**
     * recarregarBuffer1()
     *
     * Realiza a escrita dos próximos 20 caracteres no buffer 1.
     * Também verifica se é uma mudança de buffer e informa através da variável bufferAtual.
     */
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

    /**
     * recarregarBuffer2()
     *
     * Realiza a escrita dos próximos 20 caracteres no buffer 2.
     * Também verifica se é uma mudança de buffer e informa através da variável bufferAtual.
     */
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

    /**
     * incrementarPonteiro()
     *
     * Incrementa o ponteiro responsável por marcar a posição de leitura no buffer
     */
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

    /**
     * incrementarLinha()
     *
     * Incrementa o valor da linha atual.
     * Tem a utilidade de acompanhar o analisador léxico definindo em qual linha ele está trabalhando.
     */
    public void incrementarLinha(){
        linhaAtual++;
    }

    /**
     * lerCaractereDoBuffer()
     *
     * Retorna o valor inteiro que representa o próximo caractere a ser lido pelo analisador
     *
     * @return int
     */
    private int lerCaractereDoBuffer(){
        int ret = bufferDeLeitura[ponteiro];
        incrementarPonteiro();
        return ret;
    }

    /**
     * lerProximoCaractere()
     *
     * Adiciona ao lexema atual o valor do caracterer lido do buffer
     *
     * @return int - Valor inteiro do caracterer
     */
    public int lerProximoCaractere () {
        int c = lerCaractereDoBuffer();
        lexema += (char) c;
        return c;
    }

    /**
     * retroceder()
     *
     * Decrementa o ponteiro e remove o último caracterer do lexema
     */
    public void retroceder(){
        ponteiro--;
        lexema = lexema.substring(0, lexema.length() -1);
        if(ponteiro < 0){
            ponteiro = (TAMANHO_BUFFER * 2) - 1;
        }
    }

    /**
     * zerar()
     *
     * Volta o ponteiro de leitura para a posição inicial do lexema e reinicia seu valor
     */
    public void zerar(){
        ponteiro = inicioLexema;
        lexema = "";
    }

    /**
     * confirmar()
     *
     * Apos um lexema ser encontrado e ter seu respectivo token gerado
     * o valor do inicio do lexema deve ser o valor do ponteiro e o
     * lexema deve ter seu valor resetado.
     */
    public void confirmar(){
        inicioLexema = ponteiro;
        lexema = "";
    }

    /**
     * getLexema()
     *
     * Funçao getter para o valor do lexema
     *
     * @return String - Retorna a sequencia de caracter encontrada
     */
    public String getLexema() {
        return lexema;
    }
}
