package br.ufscar;

import java.util.HashMap;

public class TabelaDeSimbolos {
    private HashMap<String, EntradaTabelaDeSimbolos> tabelaDeSimbolos;

    public TabelaDeSimbolos() {
        tabelaDeSimbolos = new HashMap<>();
    }

    public void inserir(String nome, TipoAl tipo, HashMap parametros, HashMap atributos, TipoAl retorno) {
        EntradaTabelaDeSimbolos entradaTabelaDeSimbolos = new EntradaTabelaDeSimbolos(nome, tipo, parametros, atributos, retorno);
        tabelaDeSimbolos.put(nome, entradaTabelaDeSimbolos);
    }

    public EntradaTabelaDeSimbolos verificar(String nome) {
        return tabelaDeSimbolos.get(nome);
    }
}
