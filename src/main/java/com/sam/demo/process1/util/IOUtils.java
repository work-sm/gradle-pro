package com.sam.demo.process1.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.function.Function;

public class IOUtils {

    public static void createFile(String str, String filePath) throws IOException {
        File f = new File(filePath);
        if(!f.exists()){
            f.createNewFile();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))){
            bw.write(str);
        }
    }

    public static void changeFileLines(String filePath, Function<String, String> fun) throws IOException {
        String name = getCopyTempName(filePath);
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(name))){
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(name))){
                while ((line = br.readLine())!=null){
                    bw.write(fun.apply(line));
                    bw.newLine();
                }
            }
        }
        FileUtils.forceDelete(new File(name));
    }

    private static String getCopyTempName(String filePath) throws IOException {
        int i = FilenameUtils.indexOfExtension(filePath);
        String extension = FilenameUtils.getExtension(filePath);
        String substring = filePath.substring(0, i);
        String newName = substring + "_copy."+extension;
        FileUtils.copyFile(new File(filePath), new File(filePath));
        return newName;
    }

}
