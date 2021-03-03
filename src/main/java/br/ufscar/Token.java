package br.ufscar;

public class Token {
    private String nome;
    private String lexema;

    public Token(TipoToken nome, String lexema) {
        this.nome = nome;
        this.lexema = lexema;
    }

    @Override
    public String toString() {
        return "<'"+nome+"', '"+lexema+"'>";
    }
}
