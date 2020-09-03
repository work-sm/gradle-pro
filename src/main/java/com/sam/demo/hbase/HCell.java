package com.sam.demo.hbase;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HCell {

    private String rowKey;
    private String family;
    private String qualifier;
    private String value;
    private String tags;

    public HCell(String rowKey, String family, String qualifier, String value) {
        this.rowKey = rowKey;
        this.family = family;
        this.qualifier = qualifier;
        this.value = value;
    }

}
