package com.sam.demo.process2;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DesktopTest {
    public static void main(String[] args) throws IOException {
        Desktop.getDesktop().open(new File("C:\\Users\\Administrator\\Desktop\\software\\bin\\ascNodeCompt.exe"));
    }
}
