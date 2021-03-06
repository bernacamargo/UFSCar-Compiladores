package br.ufscar;

public class AnalisadorLexico {
    LeitorDeArquivos leitorDeArquivos;
    ErroLexico erroLexico = new ErroLexico();

    public AnalisadorLexico(String arquivo){
        this.leitorDeArquivos = new LeitorDeArquivos(arquivo);
    }

    public Token proximoToken(){
        char charInvalido;
        boolean validaComentario;

        Token proximo;
        validaComentario = espacosEComentarios();
        if(validaComentario){
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
            proximo = agrupamentos();
            if (proximo == null) {
                leitorDeArquivos.zerar();
            } else {
                leitorDeArquivos.confirmar();
                return proximo;
            }
            proximo = operadoresMemoria();
            if (proximo == null) {
                leitorDeArquivos.zerar();
            } else {
                leitorDeArquivos.confirmar();
                return proximo;
            }
            proximo = variavelRegistro();
            if (proximo == null) {
                leitorDeArquivos.zerar();
            } else {
                leitorDeArquivos.confirmar();
                return proximo;
            }
            proximo = definicaoArray();
            if (proximo == null) {
                leitorDeArquivos.zerar();
            } else {
                leitorDeArquivos.confirmar();
                return proximo;
            }


            charInvalido = (char) leitorDeArquivos.bufferDeLeitura[leitorDeArquivos.ponteiro];
            erroLexico.setTemErroLexio(true);
            erroLexico.setCharInvalido(charInvalido);
            if(charInvalido == '"'){
                erroLexico.setMensagem("Linha " + leitorDeArquivos.linhaAtual + ": cadeia literal nao fechada");
            }
            else {
                erroLexico.setMensagem("Linha " + leitorDeArquivos.linhaAtual + ": " + charInvalido + " - simbolo nao identificado");
            }
        }
        else{
            erroLexico.setTemErroLexio(true);
            erroLexico.setMensagem("Linha " + leitorDeArquivos.linhaAtual + ": comentario nao fechado");
        }

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
            case '%':
                return new Token("%", "%");
        }

        return null;
    }

    private Token operadoresMemoria(){
        char c = (char) leitorDeArquivos.lerProximoCaractere();
        switch (c){
            case '^':
                return new Token("^", "^");
            case '&':
                return new Token("&", "&");
            default:
                return null;
        }
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
            else if(c == '-')
                return new Token("<-", "<-");
            else{
                leitorDeArquivos.retroceder();
                return new Token("<","<");
            }
        }
        else if(c == '>') {
            c = (char)leitorDeArquivos.lerProximoCaractere();
            if(c == '=')
                return new Token(">=",">=");
            else{
                leitorDeArquivos.retroceder();
                return new Token(">",">");
            }
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
                            if(c == '.'){
                                leitorDeArquivos.retroceder();
                                leitorDeArquivos.retroceder();
                                return new Token(leitorDeArquivos.getLexema(), TipoToken.NUM_INT.toString());
                            }
                            else{
                                return null;
                            }
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
                    if (!Character.isLetterOrDigit(c) && c != '_') {
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
    private boolean espacosEComentarios() {
        int estado = 1;
        boolean validaComentario = false;
        boolean temComentario = false;
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
                        temComentario = true;
                        estado = 3;
                    } else {
                        leitorDeArquivos.retroceder();
                        return true;
                    }
                    break;
                case 2:
                    if (c == '{') {
                        temComentario = true;
                        validaComentario = false;
                        estado = 3;
                    }
                    else if(temComentario && !validaComentario && c == '\n'){
                        return false;
                    }
                    else if(c == '\n'){
                        leitorDeArquivos.incrementarLinha();
                    }
                    else if (!(Character.isWhitespace(c) || c == ' ')) {
                        leitorDeArquivos.retroceder();
                        return !temComentario || validaComentario;
                    }
                    break;
                case 3:
                    if(c == '}') {
                        validaComentario = true;
                        estado = 2;
                    }
                    else if (c == '\n') {
                        estado = 2;
                        leitorDeArquivos.retroceder();
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
                    case "real":
                        return new Token(lexema, lexema);
                    case "logico":
                        return new Token(lexema, lexema);
                    case "nao":
                        return new Token(lexema, lexema);
                    case "leia":
                        return new Token(lexema, lexema);
                    case "escreva":
                        return new Token(lexema, lexema);
                    case "se":
                        return new Token(lexema, lexema);
                    case "entao":
                        return new Token(lexema, lexema);
                    case "senao":
                        return new Token(lexema, lexema);
                    case "fim_se":
                        return new Token(lexema, lexema);
                    case "fim_algoritmo":
                        return new Token(lexema, lexema);
                    case "caso":
                        return new Token(lexema, lexema);
                    case "fim_caso":
                        return new Token(lexema, lexema);
                    case "seja":
                        return new Token(lexema, lexema);
                    case "e":
                        return new Token(lexema, lexema);
                    case "ou":
                        return new Token(lexema, lexema);
                    case "..":
                        return new Token(lexema, lexema);
                    case "para":
                        return new Token(lexema, lexema);
                    case "fim_para":
                        return new Token(lexema, lexema);
                    case "ate":
                        return new Token(lexema, lexema);
                    case "faca":
                        return new Token(lexema, lexema);
                    case "enquanto":
                        return new Token(lexema, lexema);
                    case "fim_enquanto":
                        return new Token(lexema, lexema);
                    case "registro":
                        return new Token(lexema, lexema);
                    case "fim_registro":
                        return new Token(lexema, lexema);
                    case "tipo":
                        return new Token(lexema, lexema);
                    case "var":
                        return new Token(lexema, lexema);
                    case "procedimento":
                        return new Token(lexema, lexema);
                    case "fim_procedimento":
                        return new Token(lexema, lexema);
                    case "funcao":
                        return new Token(lexema, lexema);
                    case "fim_funcao":
                        return new Token(lexema, lexema);
                    case "retorne":
                        return new Token(lexema, lexema);
                    case "constante":
                        return new Token(lexema, lexema);
                    case "falso":
                        return new Token(lexema, lexema);
                    case "verdadeiro":
                        return new Token(lexema, lexema);
                    default:
                        return null;
                }
            }
        }
    }

    private Token agrupamentos(){
        char c = (char) leitorDeArquivos.lerProximoCaractere();

        if(c == '.'){
            c = (char) leitorDeArquivos.lerProximoCaractere();
            if(c == '.'){
                return new Token(leitorDeArquivos.getLexema(), leitorDeArquivos.getLexema());
            }
        }

        return null;
    }

    private Token variavelRegistro() {
        char c = (char) leitorDeArquivos.lerProximoCaractere();
        if(c == '.'){
            return new Token(leitorDeArquivos.getLexema(), leitorDeArquivos.getLexema());
        }

        return null;
    }

    private Token definicaoArray(){
        char c = (char) leitorDeArquivos.lerProximoCaractere();
        if(c == '['){
            return new Token(leitorDeArquivos.getLexema(), leitorDeArquivos.getLexema());
        }
        else if(c == ']'){
            return new Token(leitorDeArquivos.getLexema(), leitorDeArquivos.getLexema());
        }

        return null;

    }

    private Token fim() {
        int caractereLido = leitorDeArquivos.lerProximoCaractere();
        if (caractereLido == -1) {
            return new Token("fim_algoritmo", "fim_algoritmo");
        }
        return null;
    }

}
