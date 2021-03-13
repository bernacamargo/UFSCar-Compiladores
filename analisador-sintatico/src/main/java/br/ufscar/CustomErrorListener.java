package br.ufscar;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;

public class CustomErrorListener implements ANTLRErrorListener {
    AlLexer lex;
    FileWriter fileWriter;

    public CustomErrorListener(String filePath, AlLexer lex) {
        this.lex = lex;
        try {
            this.fileWriter = new FileWriter(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object t, int line, int i1, String s, RecognitionException e) {
        Token token = (Token) t;
        String ident = token.getText();

        try {
            // Caractere nao encontrado
            if(token.getType() == 68) {
                fileWriter.write("Linha " + line + ": " + ident + " - simbolo nao identificado" +"\nFim da compilacao\n");
                return;
            }

            // Comentario não fechado
            if(ident.contains("{")) {
                fileWriter.write("Linha " + line + ": comentario nao fechado" + "\nFim da compilacao\n" );
                return;
            }

            // Cadeia de literais não fechada
            if(ident.contains("\"") && !ident.endsWith("\"")) {
                fileWriter.write("Linha " + line + ": cadeia literal nao fechada" +"\nFim da compilacao\n" );
                return;
            }

            // Adaptando o identificador EOF para a saida esperada eplo corretor
            if(ident.equals("<EOF>")) {
                ident = "EOF";
            }

            fileWriter.write("Linha " + line +": erro sintatico proximo a " + ident + "\nFim da compilacao\n" );

            fileWriter.close();
        }catch (IOException ex){
            e.printStackTrace();
        }

    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {

    }

    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {

    }

    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {

    }
}
