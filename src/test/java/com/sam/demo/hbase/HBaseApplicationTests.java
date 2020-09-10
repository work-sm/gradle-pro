package com.sam.demo.hbase;

import com.sam.demo.BootApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = BootApplication.class)
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {"com.sam.demo"})
public class HBaseApplicationTests {

    @Autowired
    private HBaseService hbaseService;

    @Test
    public void testTables() {
        List<String> allTableNames = hbaseService.getAllTableNames();
        allTableNames.forEach(System.out::println);
    }

    @Test
    public void testCreateTable() {
        hbaseService.createTable("test_base", "a", "back");
    }

    @Test
    public void testPutData() {
        HCell hCell1 = new HCell("000001", "a", "test1", "12346");
        HCell hCell2 = new HCell("000001", "a", "test2", "12346");
        hbaseService.putData("test_base", hCell1);
        hbaseService.putData("test_base", hCell2);
    }

    @Test
    public void testGetResult() {
        List<HCell> test_base = hbaseService.queryData("test_base");
        System.out.println("-----遍历查询全表内容-----");
        test_base.forEach(System.out::println);
    }

    @Test
    public void testDel() {
        hbaseService.delete("test_base", "000001");
        testGetResult();
    }

}
