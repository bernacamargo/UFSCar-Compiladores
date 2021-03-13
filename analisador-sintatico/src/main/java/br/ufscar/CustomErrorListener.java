package br.ufscar;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
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
    public void syntaxError(Recognizer<?, ?> recognizer, Object o, int line, int i1, String s, RecognitionException e) {

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
