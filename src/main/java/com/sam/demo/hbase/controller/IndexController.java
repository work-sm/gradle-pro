package com.sam.demo.hbase.controller;

import com.sam.demo.hbase.HBaseService;
import com.sam.demo.hbase.HCell;
import com.sam.demo.hbase.HRow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Slf4j
@Controller
public class IndexController {

    @Autowired
    private HBaseService hbaseService;

    @GetMapping({"/", "/index"})
    public String index(ModelMap map) {
        List<String> allTableNames = hbaseService.getAllTableNames();
        map.addAttribute("tables", allTableNames);
        return "index";
    }

    @GetMapping("/table/{name}")
    @ResponseBody
    public Collection<HRow> index(@PathVariable String name) {
        List<HCell> hCells = hbaseService.queryData(name);
        Map<String, HRow> rows = new HashMap<>();
        hCells.forEach(cell -> {
            String rowKey = cell.getRowKey();
            if(rows.containsKey(rowKey)){
                HRow hRow = rows.get(rowKey);
                hRow.add(cell);
            }else{
                HRow hRow = new HRow(rowKey);
                hRow.add(cell);
                rows.put(rowKey, hRow);
            }
        });
        return rows.values();
    }

    @GetMapping("/del/{name}/{key}")
    public String index(@PathVariable String name, @PathVariable String key, ModelMap map) {
        hbaseService.delete(name, key);
        log.info("table {} rowKey {} deleted");
        return index(map);
    }

}
