package br.ufscar;

import java.util.HashMap;

public class TabelaDeSimbolos {
    private HashMap<String, EntradaTabelaDeSimbolos> tabelaDeSimbolos;

    public TabelaDeSimbolos() {
        tabelaDeSimbolos = new HashMap<>();
    }

    public void inserir(String nome, TipoAl tipo) {
        EntradaTabelaDeSimbolos entradaTabelaDeSimbolos = new EntradaTabelaDeSimbolos(nome, tipo);
        tabelaDeSimbolos.put(nome, entradaTabelaDeSimbolos);
    }

    public EntradaTabelaDeSimbolos verificar(String nome) {
        return tabelaDeSimbolos.get(nome);
    }
}
