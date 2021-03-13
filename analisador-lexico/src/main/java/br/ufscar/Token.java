package br.ufscar;

import java.util.Arrays;

/**
 * Token
 *
 * Representa o token gerado a partir de um lexema
 */
public class Token {
    private String nome;
    private String lexema;

    /**
     * Token()
     *
     * Construtor da Classe
     *
     * @param lexema - Sequencia de caracteres
     * @param nome - Titulo do token
     */
    public Token(String lexema, String nome) {
        this.nome = nome;
        this.lexema = lexema;
    }

    /**
     * getNome()
     *
     * Retorna o nome do token
     *
     * @return String
     */
    public String getNome() {
        return nome;
    }

    /**
     * toString()
     *
     * Sobreescrita do método toString() para que exiba o token da maneira correta
     *
     * @return String - Token formatado com nome e o lexema
     */
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
