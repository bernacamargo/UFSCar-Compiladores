package br.ufscar;

import java.util.Arrays;

public class Token {
    private String nome;
    private String lexema;

    public Token(String lexema, String nome) {
        this.nome = nome;
        this.lexema = lexema;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        boolean isTipoToken;
        TipoToken[] arrTipoToken = TipoToken.values();
        try{
            isTipoToken = Arrays.asList(arrTipoToken).contains(TipoToken.valueOf(nome));
        }catch (IllegalArgumentException e){
            isTipoToken = false;
        }

        return isTipoToken ? "<'"+lexema+"',"+nome+">" : "<'"+lexema+"','"+nome+"'>";
    }
}
