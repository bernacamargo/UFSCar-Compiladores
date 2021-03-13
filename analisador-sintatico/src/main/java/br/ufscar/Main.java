/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufscar;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 *
 * @author paulo
 */
public class Main {

    /**
     * @param args the command line arguments
     */
     public static void main(String args[]) {
        CharStream cs;
        try {
            cs = CharStreams.fromFileName(args[0]);
            AlLexer lex = new AlLexer(cs);

            CommonTokenStream tokens = new CommonTokenStream(lex);
            AlParser parser = new AlParser(tokens);

            CustomErrorListener customErrorListener = new CustomErrorListener(args[1], lex);

            parser.addErrorListener(customErrorListener);
            parser.progr();
        } 
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
