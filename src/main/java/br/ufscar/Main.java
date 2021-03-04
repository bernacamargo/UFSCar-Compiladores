package br.ufscar;

public class Main {
    public static void main(String[] args) {
        AnalisadorLexico lex = new AnalisadorLexico(args[0]);
        Token t = null;

        while(true){
            t = lex.proximoToken();
            if (t != null){
                if(t.getNome() != TipoToken.Fim){
                    System.out.println(t);
                }
                else{
                    break;
                }
            }
            else{
                break;
            }
        }

    }
}
