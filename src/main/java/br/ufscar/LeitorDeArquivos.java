package br.ufscar;

import java.io.*;

public class LeitorDeArquivos {
    InputStream is;

    public LeitorDeArquivos(String arquivo) {
        try {
            is = new FileInputStream(new File(arquivo));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int lerProximoCaractere () {
        int ret = 0;
        try {
            ret = is.read();
            System.out.print((char) ret);
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
