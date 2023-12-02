package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTableTest {

    private static List<String> dataCols;

    @BeforeAll
    static void setUp() {
        dataCols = List.of("Col1", "Col2", "Col3");
    }

    @Test
    void testAddDataThrowsExceptionWhenDataIsNull() {
        Table table = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> table.addData(null),
            "Add data should throw Illegal Argument Exception when data is null!");
    }

    @Test
    void testAddDataAddsHeaders() throws CsvDataNotCorrectException {
        Table table = new BaseTable();
        table.addData(dataCols.toArray(new String[0]));
        assertIterableEquals(dataCols, table.getColumnNames(),
            "Headers should be the same as the tested!");
    }

    @Test
    void testAddDataThrowsCSVDataNotCorrectExc() throws CsvDataNotCorrectException {
        Table table = new BaseTable();
        table.addData(dataCols.toArray(new String[0]));
        String[] data = new String[] {"data1", "data2"};
        assertThrows(CsvDataNotCorrectException.class, () -> table.addData(data),
            "Add data should throw CsvDataNotCorrectException when there is a difference " +
                "between the count of columns and the length of data that is passed!");
    }

    @Test
    void testAddDataAddsSuccessfully() throws CsvDataNotCorrectException {
        Table table = new BaseTable();
        table.addData(dataCols.toArray(new String[0]));
        String[] data = new String[] {"data1", "data2", "data3"};
        table.addData(data);
        assertTrue(table.getColumnData("Col1").contains("data1"),
            "Table should contain the first data in the first column!");
        assertTrue(table.getColumnData("Col2").contains("data2"),
            "Table should contain the second data in the first column!");
        assertTrue(table.getColumnData("Col3").contains("data3"),
            "Table should contain the third data in the first column!");
        assertEquals(2, table.getRowsCount(),
            "Table rows count should equal to the sum of rows + 1 for the header!");
    }

    @Test
    void testGetColumnDataThrowsExceptionWhenDataIsNull() {
        Table table = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> table.getColumnData(null),
            "GetColumnData should throw exception when data is null!");
    }

    @Test
    void testGetColumnDataThrowsExceptionWhenDataIsBlank() {
        Table table = new BaseTable();
        assertThrows(IllegalArgumentException.class, () -> table.getColumnData("  "),
            "GetColumnData should throw exception when data is blank!");
    }

    @Test
    void testGetColumnDataThrowsExceptionWhenTableDoesNotContainThisCol() throws CsvDataNotCorrectException{
        Table table = new BaseTable();
        table.addData(dataCols.toArray(new String[0]));
        assertThrows(IllegalArgumentException.class, () -> table.getColumnData("NoSuchCol"),
            "GetColumnData should throw exception when there is no column with that name!");
    }
}
