package com.sam.demo.process.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TleResult {

    private File kepl;

    public TleResult(File kepl) {
        this.kepl = kepl;
    }

    public String getResult() throws IOException {
        BufferedReader bfr = null;
        try {
            bfr = new BufferedReader(new FileReader(kepl));
            return bfr.readLine();
        } finally {
            if(bfr!=null){
                bfr.close();
            }
        }
    }

}
