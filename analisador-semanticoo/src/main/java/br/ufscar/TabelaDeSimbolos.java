package br.ufscar;

import java.util.HashMap;

public class TabelaDeSimbolos {
    private HashMap<String, EntradaTabelaDeSimbolos> tabelaDeSimbolos;

    public TabelaDeSimbolos() {
        tabelaDeSimbolos = new HashMap<>();
    }

    public void inserir(String nome, TipoAl tipo, boolean ponteiro) {
        EntradaTabelaDeSimbolos entradaTabelaDeSimbolos = new EntradaTabelaDeSimbolos(nome, tipo, ponteiro);
        tabelaDeSimbolos.put(nome, entradaTabelaDeSimbolos);
    }

    public EntradaTabelaDeSimbolos verificar(String nome) {
        return tabelaDeSimbolos.get(nome);
    }

    public void verificarTipoCustomizados(String nome, String tipoVar) {

        HashMap<String, EntradaTabelaDeSimbolos> tabelaDeSimbolosAuxiliar = new HashMap<>();

        tabelaDeSimbolos.forEach((key, itemTabela) -> {
            EntradaTabelaDeSimbolos entrada = new EntradaTabelaDeSimbolos(itemTabela.nome, itemTabela.tipo, itemTabela.ponteiro);
            tabelaDeSimbolosAuxiliar.put(itemTabela.nome, entrada);
        });

        tabelaDeSimbolosAuxiliar.forEach((key, itemTabela) -> {
            if (itemTabela.nome.contains(tipoVar)){
                inserir(itemTabela.nome.replace(tipoVar, nome), itemTabela.tipo, itemTabela.ponteiro);
            }
        });
    }


    @Override
    public String toString() {
        StringBuilder tabela = new StringBuilder();
        tabela.append("\n");
        tabelaDeSimbolos.forEach((t, s) -> {
            tabela.append("key: ").append(t.toString()).append("\n");
            tabela.append("value: \n");
            tabela.append("\t nome:").append(s.nome).append("\n");
            tabela.append("\t tipo:").append(s.tipo).append("\n");
            tabela.append("\t ponteiro:").append(s.ponteiro).append("\n\n");
        });

        return tabela.toString();
    }
}
