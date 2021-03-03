package br.ufscar;

public class AnalisadorLexico {
    LeitorDeArquivos leitorDeArquivos;

    public AnalisadorLexico(String arquivo){
        this.leitorDeArquivos = new LeitorDeArquivos(arquivo);
    }

    public Token proximoToken(){
        int caractereLido = -1;

        while((caractereLido = leitorDeArquivos.lerProximoCaractere()) != -1){
            char c = (char) caractereLido;

            if(c == ' ' || c == '\n') continue;

            if(c == '*')
                return new Token(TipoToken.OpAritMult,"*");
            else if(c == '/')
                return new Token(TipoToken.OpAritDiv,"/");
            else if(c == '+')
                return new Token(TipoToken.OpAritSoma,"+");
            else if(c == '-')
                return new Token(TipoToken.OpAritSub,"-");
            else if(c == '(')
                return new Token(TipoToken.AbrePar,"(");
            else if(c == ')')
                return new Token(TipoToken.FechaPar,")");
            else if(c == '<') {
                c = (char)leitorDeArquivos.lerProximoCaractere();
                if(c == '>')
                    return new Token(TipoToken.OpRelDif,"<>");
                else if(c == '=')
                    return new Token(TipoToken.OpRelMenorIgual,"<=");
                else
                    return new Token(TipoToken.OpRelMenor,"<");
            }
            else if(c == '>') {
                c = (char)leitorDeArquivos.lerProximoCaractere();
                if(c == '=')
                    return new Token(TipoToken.OpRelMaiorIgual,"<=");
                else
                    return new Token(TipoToken.OpRelMaior,"<");
            }

        }

        return null;
    }
}
