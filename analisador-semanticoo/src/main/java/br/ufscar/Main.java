/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufscar;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bernardo, paulo, renata
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        CharStream cs;
        try {
            cs = CharStreams.fromFileName(args[0]);
            AlLexer lex = new AlLexer(cs);  // iniciar o objeto lex com cs

            CommonTokenStream tokens = new CommonTokenStream(lex); //gerador do token
            AlParser parser = new AlParser(tokens);
            AlParser.ProgrContext arvore = parser.progr();

            // Gerador de código
            GeradorCodigoC gerador = new GeradorCodigoC();
            gerador.visitProgr(arvore);
            try(PrintWriter pw = new PrintWriter(args[1])){
                pw.print(gerador.saida.toString());
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
