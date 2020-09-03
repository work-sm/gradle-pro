package com.sam.demo.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HBaseService {

    private Connection connection;

    public HBaseService(Configuration conf) {
        try {
            connection = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean namespaceExists(String namespace) {
        boolean flag = true;
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            NamespaceDescriptor namespaceDescriptor = admin.getNamespaceDescriptor(namespace);
            String name = namespaceDescriptor.getName();
            if (name == null) {
                flag = false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    public void createNamespace(String namespace) {
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            admin.createNamespace(NamespaceDescriptor.create(namespace).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteNamespace(String namespace) {
        if (namespaceExists(namespace)) {
            try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
                Pattern compile = Pattern.compile(namespace + ":.*");
                admin.disableTables(compile);
                admin.deleteTables(compile);
                admin.deleteNamespace(namespace);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void listTables() throws IOException {
        try (Admin admin = connection.getAdmin()) {
            HTableDescriptor[] tables = admin.listTables();
            printTables(tables);
        }
    }

    private void printTables(HTableDescriptor[] tables) {
        for (HTableDescriptor t : tables) {
            HColumnDescriptor[] columns = t.getColumnFamilies();
            System.out.printf("tables:%s,columns-family:\n", t.getTableName());
            for (HColumnDescriptor column : columns) {
                System.out.printf("\t%s\n", column.getNameAsString());
            }
        }
    }

    public List<String> getAllTableNames() {
        List<String> result = new ArrayList<>();
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            TableName[] tableNames = admin.listTableNames();
            for (TableName tableName : tableNames) {
                result.add(tableName.getNameAsString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private Table getTable(String tableName) throws IOException {
        return connection.getTable(TableName.valueOf(tableName));
    }

    public void createTable(String tableName, String... familyNames) {
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
            if (familyNames != null) {
                for (String familyName : familyNames) {
                    HColumnDescriptor hcd = new HColumnDescriptor(familyName);
                    desc.addFamily(hcd);
                }
            }
            if (admin.tableExists(desc.getTableName())) {
                admin.disableTable(desc.getTableName());
                admin.deleteTable(desc.getTableName());
            }
            admin.createTable(desc);
            System.out.println("create table Success!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<HCell> queryData(String tableName) {
        return this.queryData(tableName, new Scan());
    }

    public List<HCell> queryData(String tableName, Scan scan) {
        List<HCell> result = new ArrayList<>();
        try (Table table = getTable(tableName); ResultScanner rs = table.getScanner(scan)) {
            for (Result r : rs) {
                List<HCell> cells = getCells(r);
                result.addAll(cells);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public List<HCell> getData(String tableName, String rowKey) throws Exception {
        return getData(tableName, rowKey, null, null);
    }

    public List<HCell> getData(String tableName, String rowKey, String colFamily) throws Exception {
        return getData(tableName, rowKey, colFamily, null);
    }

    public List<HCell> getData(String tableName, String rowKey, String colFamily, String col) throws Exception {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Get get = new Get(Bytes.toBytes(rowKey));
            if (colFamily != null && col == null)
                get.addFamily(Bytes.toBytes(colFamily));
            if (colFamily != null && col != null)
                get.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
            Result result = table.get(get);
            return getCells(result);
        }
    }

    private List<HCell> getCells(Result result) {
        List<HCell> cells = new ArrayList<>();
        for (Cell cell : result.listCells()) {
            String rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
            String qualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
            String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
            String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
            String tags = Bytes.toString(cell.getTagsArray(), cell.getTagsOffset(), cell.getTagsLength());
            HCell hCell = new HCell(rowKey, family, qualifier, value, tags);
            cells.add(hCell);
        }
        return cells;
    }

    public void showCell(Result result) {
        Cell[] cells = result.rawCells();
        for (Cell cell : cells) {
            System.out.println("RowName:" + new String(CellUtil.cloneRow(cell)) + " ");
            System.out.println("Timetamp:" + cell.getTimestamp() + " ");
            System.out.println("column Family:" + new String(CellUtil.cloneFamily(cell)) + " ");
            System.out.println("row Name:" + new String(CellUtil.cloneQualifier(cell)) + " ");
            System.out.println("value:" + new String(CellUtil.cloneValue(cell)) + " ");
            System.out.println("---------------");
        }
    }

    public void putData(String tableName, List<HCell> cells) {
        for (HCell cell : cells) {
            putData(tableName, cell);
        }
    }

    public void putData(String tableName, HCell cell) {
        String rowKey = cell.getRowKey();
        String family = cell.getFamily();
        String qualifier = cell.getQualifier();
        String value = cell.getValue();
        putData(tableName, rowKey, family, qualifier, value);
    }

    private void putData(String tableName, String rowKey, String colFamily, String col, String val) {
        try (Table table = getTable(tableName)) {
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col), Bytes.toBytes(val));
            table.put(put);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String tableName, String rowKey) {
        delete(tableName, rowKey, null, null);
    }

    public void delete(String tableName, String rowKey, String colFamily) {
        delete(tableName, rowKey, colFamily, null);
    }

    public void delete(String tableName, String rowKey, String colFamily, String col) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            if (colFamily != null && col == null)
                delete.addFamily(Bytes.toBytes(colFamily));
            if (colFamily != null && col != null)
                delete.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
            table.delete(delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
