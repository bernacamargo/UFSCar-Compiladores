package br.ufscar;

public class EntradaTabelaDeSimbolos {
    public String nome;
    public TipoAl tipo;
    public boolean ponteiro;

    //Para conseguir encontrar o tipo e o nome da variavel e se é ponteiro ou não
    public EntradaTabelaDeSimbolos(String nome, TipoAl tipo, boolean ponteiro) {
        this.nome = nome;
        this.tipo = tipo;
        this.ponteiro = ponteiro;
    }
}
