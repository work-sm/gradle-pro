package com.sam.demo.hbase.controller;

import com.sam.demo.hbase.HBaseService;
import com.sam.demo.hbase.HCell;
import com.sam.demo.hbase.HRow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class IndexController {

    @Autowired(required = false)
    private HBaseService hbaseService;

    @GetMapping("/ws")
    public String index() {
        return "ws";
    }

    @GetMapping({"/", "/index"})
    public String index(ModelMap map) {
        List<String> allTableNames = hbaseService.getAllTableNames();
        map.addAttribute("tables", allTableNames);
        return "index";
    }

    @GetMapping("/table/{name}")
    @ResponseBody
    public Collection<HRow> select(@PathVariable String name) {
        List<HCell> hCells = hbaseService.queryData(name);
        Map<String, HRow> rows = new HashMap<>();
        hCells.forEach(cell -> {
            String rowKey = cell.getRowKey();
            if (rows.containsKey(rowKey)) {
                HRow hRow = rows.get(rowKey);
                hRow.add(cell);
            } else {
                HRow hRow = new HRow(rowKey);
                hRow.add(cell);
                rows.put(rowKey, hRow);
            }
        });
        return rows.values();
    }

    @GetMapping("/del/{name}/{key}")
    public String del(@PathVariable String name, @PathVariable String key, ModelMap map) {
        hbaseService.delete(name, key);
        log.info("table {} rowKey {} deleted", name, key);
        return index(map);
    }

    @PostMapping("/save/{name}")
    @ResponseBody
    public Collection<HRow> save(@PathVariable String name, HRow hRow) {
        List<HCell> cells = hRow.getCells();
        hbaseService.putData(name, cells);
        return select(name);
    }

}
