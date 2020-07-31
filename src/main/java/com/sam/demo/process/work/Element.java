package com.sam.demo.process.work;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;

@Getter
@Setter
@ToString
public class Element {
    private String key;

    private String params;
    private File paramsFile;
    private String result;
    private File[] resultFiles;
    private String reason;
}
