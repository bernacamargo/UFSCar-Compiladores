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

    private Token operadorAritmetico() {
        int caractereRecebido = leitorDeArquivos.lerProximoCaractere();
        char c = (char) caractereRecebido;
        switch (c) {
            case '*':
                return new Token("*", "*");
            case '/':
                return new Token("/", "/");
            case '+':
                return new Token("+", "+");
            case '-':
                return new Token("-", "-");
        }

        return null;
    }

    private Token operadorRelacional(){
        int caractereRecebido = leitorDeArquivos.lerProximoCaractere();
        char c = (char) caractereRecebido;
        if(c == '<') {
            c = (char)leitorDeArquivos.lerProximoCaractere();
            if(c == '>')
                return new Token("<>","<>");
            else if(c == '=')
                return new Token("<=","<=");
            else{
                leitorDeArquivos.retroceder();
                return new Token("<","<");
            }
        }
        else if(c == '>') {
            c = (char)leitorDeArquivos.lerProximoCaractere();
            if(c == '=')
                return new Token(">=",">=");
            else
                return new Token(">",">");
        }
        else if(c == '='){
            return new Token("=", "=");
        }

        return null;
    }

    private Token delimitador() {
        int caractereLido = leitorDeArquivos.lerProximoCaractere();
        char c = (char) caractereLido;
        if (c == ':' || c == ',') {
            return new Token(leitorDeArquivos.getLexema(), leitorDeArquivos.getLexema());
        }
        else {
            return null;
        }
    }

    private Token parenteses(){
        int caractereRecebido = leitorDeArquivos.lerProximoCaractere();
        char c = (char) caractereRecebido;

        if(c == '(')
            return new Token("(","(");
        else if(c == ')')
            return new Token(")",")");

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
                        return new Token(leitorDeArquivos.getLexema(), TipoToken.NUM_INT.toString());
                    }
                    break;
                case 3: // retorna o token quando encontrar algo diferente de um numero
                    if (!Character.isDigit(c)) {
                        leitorDeArquivos.retroceder();
                        return new Token(leitorDeArquivos.getLexema(), TipoToken.NUM_REAL.toString());
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
                        return new Token(leitorDeArquivos.getLexema(), TipoToken.IDENT.toString());
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
                if (c == '\"') {
                    estado = 2;
                } else {
                    return null;
                }
            } else if (estado == 2) {
                if (c == '\n') {
                    return null;
                }
                if (c == '\"') {
                    return new Token(leitorDeArquivos.getLexema(), TipoToken.CADEIA.toString());
                } else if (c == '\\') {
                    estado = 3;
                }
            } else if (estado == 3) {
                if (c == '\n') {
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
            switch (estado) {
                case 1:
                    if (Character.isWhitespace(c) || c == ' ') {
                        if(c == '\n'){
                            leitorDeArquivos.incrementarLinha();
                        }
                        estado = 2;
                    } else if (c == '{') {
                        estado = 3;
                    } else {
                        leitorDeArquivos.retroceder();
                        return;
                    }
                    break;
                case 2:
                    if (c == '{') {
                        estado = 3;
                    }
                    else if(c == '\n'){
                        leitorDeArquivos.incrementarLinha();
                    }
                    else if (!(Character.isWhitespace(c) || c == ' ')) {
                        leitorDeArquivos.retroceder();
                        return;
                    }
                    break;
                case 3:
                    if(c == '}') {
                        break;
                    }
                    else if (c == '\n') {
                        estado = 2;
                        leitorDeArquivos.incrementarLinha();
                    }
                    break;
            }
        }
    }
    private Token palavrasChave() {
        while (true) {
            char c = (char) leitorDeArquivos.lerProximoCaractere();
            if (!Character.isLetter(c) && c != '_') {
                leitorDeArquivos.retroceder();
                String lexema = leitorDeArquivos.getLexema();
                switch (lexema) {
                    case "declare":
                        return new Token(lexema, lexema);
                    case "algoritmo":
                        return new Token(lexema, lexema);
                    case "inteiro":
                        return new Token(lexema, lexema);
                    case "literal":
                        return new Token(lexema, lexema);
                    case "REAL":
                        return new Token(lexema, lexema);
                    case "ATRIBUIR":
                        return new Token(lexema, lexema);
                    case "A":
                        return new Token(lexema, lexema);
                    case "leia":
                        return new Token(lexema, lexema);
                    case "escreva":
                        return new Token(lexema, lexema);
                    case "SE":
                        return new Token(lexema, lexema);
                    case "ENTAO":
                        return new Token(lexema, lexema);
                    case "ENQUANTO":
                        return new Token(lexema, lexema);
                    case "INICIO":
                        return new Token(lexema, lexema);
                    case "fim_algoritmo":
                        return new Token(lexema, lexema);
                    case "E":
                        return new Token(lexema, lexema);
                    case "OU":
                        return new Token(lexema, lexema);
                    default:
                        return null;
                }
            }
        }
    }

    private Token fim() {
        int caractereLido = leitorDeArquivos.lerProximoCaractere();
        if (caractereLido == -1) {
            return new Token("fim_algoritmo", "fim_algoritmo");
        }
        return null;
    }

}
