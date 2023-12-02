package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.Column;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseTable implements Table {
    private Map<String, Column> tableCols;
    private int rowsCount;

    public BaseTable() {
        this.tableCols = new LinkedHashMap<>();
        rowsCount = 0;
    }

    @Override
    public void addData(String[] data) throws CsvDataNotCorrectException {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null!");
        }
        if (tableCols.size() == 0) {
            for (var d : data) {
                tableCols.put(d, new BaseColumn());
            }
            rowsCount++;
            return;
        }
        if (data.length != tableCols.size()) {
            throw new CsvDataNotCorrectException("Wrong format of row values for this table!");
        }
        var entrySet = tableCols.entrySet();
        var it = entrySet.iterator();
        for (var d : data) {
            it.next().getValue().addData(d);
        }
        rowsCount++;
    }

    @Override
    public Collection<String> getColumnNames() {
        return Collections.unmodifiableCollection(this.tableCols.keySet());
    }

    @Override
    public Collection<String> getColumnData(String column) {
        if (column == null) {
            throw new IllegalArgumentException("Column name cannot be null!");
        }
        if (column.isBlank()) {
            throw new IllegalArgumentException("Column name cannot be blank!");
        }
        if (!this.tableCols.containsKey(column)) {
            throw new IllegalArgumentException("There is no column with this name!");
        }
        return Collections.unmodifiableCollection(tableCols.get(column).getData());
    }

    @Override
    public int getRowsCount() {
        return rowsCount;
    }
}
