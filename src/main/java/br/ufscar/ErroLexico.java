package br.ufscar;

public class ErroLexico {
    private boolean temErroLexio;
    private char charInvalido;
    private String mensagem;

    public ErroLexico() {
        this.temErroLexio = false;
    }

    public boolean getTemErroLexio() {
        return temErroLexio;
    }

    public char getCharInvalido() {
        return charInvalido;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setTemErroLexio(boolean temErroLexio) {
        this.temErroLexio = temErroLexio;
    }

    public void setCharInvalido(char charInvalido) {
        this.charInvalido = charInvalido;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
