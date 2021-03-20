package br.ufscar;

import java.util.HashMap;

public class EntradaTabelaDeSimbolos {
    public String nome;
    public TipoAl tipo;
    public HashMap<Integer, TipoAl> parametros;
    public HashMap<String, TipoAl> atributos;
    public TipoAl retorno;

    public EntradaTabelaDeSimbolos(String nome, TipoAl tipo, HashMap<Integer, TipoAl> parametros, HashMap<String, TipoAl> atributos, TipoAl retorno) {
        this.nome = nome;
        this.tipo = tipo;
        this.parametros = parametros;
        this.atributos = atributos;
        this.retorno = retorno;
    }
}
