package br.ufscar;

public class GeradorUtils {

    public static String verificaTipoVarParam(TipoAl tipoVar) {
        String tipoVarParam;
        switch (tipoVar){
            case INTEIRO:
                tipoVarParam = "%d";
                break;
            case REAL:
                tipoVarParam = "%f";
                break;
            case CADEIA:
                tipoVarParam = "%s";
                break;
            default:
                tipoVarParam = "";
        }
        return tipoVarParam;
    }
}
