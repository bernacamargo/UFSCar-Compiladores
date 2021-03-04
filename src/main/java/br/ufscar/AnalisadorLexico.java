package br.ufscar;

public class AnalisadorLexico {
    LeitorDeArquivos leitorDeArquivos;

    public AnalisadorLexico(String arquivo){
        this.leitorDeArquivos = new LeitorDeArquivos(arquivo);
    }

    public Token proximoToken(){
        char charInvalido;

        Token proximo;
        espacosEComentarios();
        leitorDeArquivos.confirmar();
        proximo = fim();
        if (proximo == null) {
            leitorDeArquivos.zerar();
        } else {
            leitorDeArquivos.confirmar();
            return proximo;
        }
        proximo = palavrasChave();
        if (proximo == null) {
            leitorDeArquivos.zerar();
        } else {
            leitorDeArquivos.confirmar();
            return proximo;
        }
        proximo = variavel();
        if (proximo == null) {
            leitorDeArquivos.zerar();
        } else {
            leitorDeArquivos.confirmar();
            return proximo;
        }
        proximo = numeros();
        if (proximo == null) {
            leitorDeArquivos.zerar();
        } else {
            leitorDeArquivos.confirmar();
            return proximo;
        }
        proximo = operadorAritmetico();
        if (proximo == null) {
            leitorDeArquivos.zerar();
        } else {
            leitorDeArquivos.confirmar();
            return proximo;
        }
        proximo = operadorRelacional();
        if (proximo == null) {
            leitorDeArquivos.zerar();
        } else {
            leitorDeArquivos.confirmar();
            return proximo;
        }
        proximo = delimitador();
        if (proximo == null) {
            leitorDeArquivos.zerar();
        } else {
            leitorDeArquivos.confirmar();
            return proximo;
        }
        proximo = parenteses();
        if (proximo == null) {
            leitorDeArquivos.zerar();
        } else {
            leitorDeArquivos.confirmar();
            return proximo;
        }
        proximo = cadeia();
        if (proximo == null) {
            leitorDeArquivos.zerar();
        } else {
            leitorDeArquivos.confirmar();
            return proximo;
        }
        charInvalido = (char) leitorDeArquivos.bufferDeLeitura[leitorDeArquivos.ponteiro];
        System.err.println("Linha " + leitorDeArquivos.linhaAtual + ": " + charInvalido + " - simbolo nao identificado");
        return null;
    }

    private Token operadorAritmetico(){
        int caractereRecebido = leitorDeArquivos.lerProximoCaractere();
        char c = (char) caractereRecebido;
        switch (c) {
            case '*':
                return new Token(TipoToken.OpAritMult, "*");
            case '/':
                return new Token(TipoToken.OpAritDiv, "/");
            case '+':
                return new Token(TipoToken.OpAritSoma, "+");
            case '-':
                return new Token(TipoToken.OpAritSub, "-");
        }

        return null;
    }

    private Token operadorRelacional(){
        int caractereRecebido = leitorDeArquivos.lerProximoCaractere();
        char c = (char) caractereRecebido;
        if(c == '<') {
            c = (char)leitorDeArquivos.lerProximoCaractere();
            if(c == '>')
                return new Token(TipoToken.OpRelDif,"<>");
            else if(c == '=')
                return new Token(TipoToken.OpRelMenorIgual,"<=");
            else{
                leitorDeArquivos.retroceder();
                return new Token(TipoToken.OpRelMenor,"<");
            }
        }
        else if(c == '>') {
            c = (char)leitorDeArquivos.lerProximoCaractere();
            if(c == '=')
                return new Token(TipoToken.OpRelMaiorIgual,">=");
            else
                return new Token(TipoToken.OpRelMaior,">");
        }
        else if(c == '='){
            return new Token(TipoToken.OpRelIgual, "=");
        }

        return null;
    }

    private Token delimitador() {
        int caractereLido = leitorDeArquivos.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == ':') {
            return new Token(TipoToken.Delim, leitorDeArquivos.getLexema());
        } else {
            return null;
        }
    }

    private Token parenteses(){
        int caractereRecebido = leitorDeArquivos.lerProximoCaractere();
        char c = (char) caractereRecebido;

        if(c == '(')
            return new Token(TipoToken.AbrePar,"(");
        else if(c == ')')
            return new Token(TipoToken.FechaPar,")");

        return null;
    }

    private Token numeros() {
        int estado = 1;
        while (true) {
            char c = (char) leitorDeArquivos.lerProximoCaractere();
            switch (estado) {
                case 1: // verifica se é um numero
                    if (Character.isDigit(c)) {
                        estado = 2;
                    } else {
                        return null;
                    }
                    break;
                case 2: // verifica se é inteiro ou real
                    if (c == '.') {
                        c = (char) leitorDeArquivos.lerProximoCaractere();
                        if (Character.isDigit(c)) {
                            estado = 3;
                        } else {
                            return null;
                        }
                    } else if (!Character.isDigit(c)) {
                        leitorDeArquivos.retroceder();
                        return new Token(TipoToken.NumInt, leitorDeArquivos.getLexema());
                    }
                    break;
                case 3: // retorna o token quando encontrar algo diferente de um numero
                    if (!Character.isDigit(c)) {
                        leitorDeArquivos.retroceder();
                        return new Token(TipoToken.NumReal, leitorDeArquivos.getLexema());
                    }
                    break;
            }
        }
    }
    private Token variavel() {
        int estado = 1;
        while (true) {
            char c = (char) leitorDeArquivos.lerProximoCaractere();
            switch (estado) {
                case 1:
                    if (Character.isLetter(c)) {
                        estado = 2;
                    } else {
                        return null;
                    }
                    break;
                case 2:
                    if (!Character.isLetterOrDigit(c)) {
                        leitorDeArquivos.retroceder();
                        return new Token(TipoToken.Var, leitorDeArquivos.getLexema());
                    }
                    break;

                default:
                    return null;
            }
        }
    }
    private Token cadeia() {
        int estado = 1;
        while (true) {
            char c = (char) leitorDeArquivos.lerProximoCaractere();
            if (estado == 1) {
                if (c == '\'') {
                    estado = 2;
                } else {
                    return null;
                }
            } else if (estado == 2) {
                if (c == '\n') {
                    leitorDeArquivos.incrementarLinha();
                    return null;
                }
                if (c == '\'') {
                    return new Token(TipoToken.Cadeia, leitorDeArquivos.getLexema());
                } else if (c == '\\') {
                    estado = 3;
                }
            } else if (estado == 3) {
                if (c == '\n') {
                    leitorDeArquivos.incrementarLinha();
                    return null;
                } else {
                    estado = 2;
                }
            }
        }
    }
    private void espacosEComentarios() {
        int estado = 1;
        while (true) {
            char c = (char) leitorDeArquivos.lerProximoCaractere();
            if (estado == 1) {
                if (Character.isWhitespace(c) || c == ' ') {
                    estado = 2;
                } else if (c == '%') {
                    estado = 3;
                } else {
                    if(c == '\n')
                        leitorDeArquivos.incrementarLinha();

                    leitorDeArquivos.retroceder();
                    return;
                }
            } else if (estado == 2) {
                if (c == '%') {
                    estado = 3;
                } else if (!(Character.isWhitespace(c) || c == ' ')) {
                    leitorDeArquivos.retroceder();
                    return;
                }
            } else if (estado == 3) {
                if (c == '\n') {
                    leitorDeArquivos.incrementarLinha();
                    return;
                }
            }
        }
    }
    private Token palavrasChave() {
        while (true) {
            char c = (char) leitorDeArquivos.lerProximoCaractere();
            if (!Character.isLetter(c)) {
                leitorDeArquivos.retroceder();
                String lexema = leitorDeArquivos.getLexema();
                switch (lexema) {
                    case "DECLARACOES":
                        return new Token(TipoToken.PCDeclaracoes, lexema);
                    case "ALGORITMO":
                        return new Token(TipoToken.PCAlgoritmo, lexema);
                    case "INT":
                        return new Token(TipoToken.PCInteiro, lexema);
                    case "REAL":
                        return new Token(TipoToken.PCReal, lexema);
                    case "ATRIBUIR":
                        return new Token(TipoToken.PCAtribuir, lexema);
                    case "A":
                        return new Token(TipoToken.PCA, lexema);
                    case "LER":
                        return new Token(TipoToken.PCLer, lexema);
                    case "IMPRIMIR":
                        return new Token(TipoToken.PCImprimir, lexema);
                    case "SE":
                        return new Token(TipoToken.PCSe, lexema);
                    case "ENTAO":
                        return new Token(TipoToken.PCEntao, lexema);
                    case "ENQUANTO":
                        return new Token(TipoToken.PCEnquanto, lexema);
                    case "INICIO":
                        return new Token(TipoToken.PCInicio, lexema);
                    case "FIM":
                        return new Token(TipoToken.PCFim, lexema);
                    case "E":
                        return new Token(TipoToken.OpBoolE, lexema);
                    case "OU":
                        return new Token(TipoToken.OpBoolOu, lexema);
                    default:
                        return null;
                }
            }
        }
    }

    private Token fim() {
        int caractereLido = leitorDeArquivos.lerProximoCaractere();
        if (caractereLido == -1) {
            leitorDeArquivos.incrementarLinha();
            return new Token(TipoToken.Fim, "Fim");
        }
        return null;
    }

}
