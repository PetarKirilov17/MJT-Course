package bg.sofia.uni.fmi.mjt.csvprocessor.table.column;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

public class BaseColumnTest {

    private static Set<String> values;

    @BeforeAll
    static void setUp() {
        values = new LinkedHashSet<>();
        values.add("Data1");
        values.add("Data2");
    }

    @Test
    void testAddDataThrowsIllegalExceptionWhenDataIsNull() {
        Column column = new BaseColumn();
        assertThrows(IllegalArgumentException.class, () -> column.addData(null),
            "Add data method should throw exception when data is null!");
    }

    @Test
    void testAddDataThrowsIllegalExceptionWhenDataIsEmpty() {
        Column column = new BaseColumn();
        assertThrows(IllegalArgumentException.class, () -> column.addData("  "),
            "Add data method should throw exception when data is blank!");
    }

    @Test
    void testAddDataSuccessfully(){
        Column column = new BaseColumn(values);
        column.addData("Data3");
        assertTrue(column.getData().contains("Data3"),
            "Add Data should add data successfully!");
    }
}
