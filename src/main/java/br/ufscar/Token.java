package br.ufscar;

public class Token {
    private TipoToken nome;
    private String lexema;

    public Token(TipoToken nome, String lexema) {
        this.nome = nome;
        this.lexema = lexema;
    }

    public TipoToken getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "<'"+nome+"', '"+lexema+"'>";
    }
}
