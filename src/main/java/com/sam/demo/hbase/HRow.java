package com.sam.demo.hbase;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HRow {
    private String rowKey;
    private List<HCell> cells = new ArrayList<>();

    public HRow(String rowKey) {
        this.rowKey = rowKey;
    }

    public void add(HCell cell){
        cells.add(cell);
    }
}
