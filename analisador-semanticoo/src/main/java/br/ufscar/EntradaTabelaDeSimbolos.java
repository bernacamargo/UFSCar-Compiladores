package br.ufscar;

public class EntradaTabelaDeSimbolos {
    public String nome;
    public TipoAl tipo;
    public boolean ponteiro;

    public EntradaTabelaDeSimbolos(String nome, TipoAl tipo, boolean ponteiro) {
        this.nome = nome;
        this.tipo = tipo;
        this.ponteiro = ponteiro;
    }
}
