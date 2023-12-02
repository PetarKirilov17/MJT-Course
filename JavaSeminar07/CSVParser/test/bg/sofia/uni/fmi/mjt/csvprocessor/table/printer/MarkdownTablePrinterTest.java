package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;


public class MarkdownTablePrinterTest {
    private static ColumnAlignment[] alignments;
    private static String[] headers;
    @BeforeAll
    static void setUp() {
        alignments = new ColumnAlignment[] {ColumnAlignment.NOALIGNMENT, ColumnAlignment.LEFT, ColumnAlignment.RIGHT,
            ColumnAlignment.CENTER};
        headers = new String[]{"Header1", "LongHeader2", "H3", "Header4"};
    }

    @Test
    void testPrintTableSuccessfully() throws CsvDataNotCorrectException {
        Table table = new BaseTable();
        table.addData(headers);
        String[] row1 = new String[]{"dataLong1.1", "data1.2", "d1", "data1.4"};
        String[] row2 = new String[]{"data2.1", "data2.2", "d2", "dataLong2.4"};
        table.addData(row1);
        table.addData(row2);
        StringBuilder expectedHeader = new StringBuilder();
        expectedHeader.append("|").append(" Header1").append(" ".repeat(4)).append(" |")
            .append(" LongHeader2").append(" |")
            .append(" H3 ").append(" |")
            .append(" Header4").append(" ".repeat(4)).append(" |");

        StringBuilder expectedAlignments = new StringBuilder();
        expectedAlignments.append("|").append(" ").append("-".repeat(11)).append(" |")
            .append(" ").append(":").append("-".repeat(10)).append(" |")
            .append(" --: |")
            .append(" ").append(":").append("-".repeat(9)).append(": |");

        StringBuilder expectedRow1 = new StringBuilder();
        expectedRow1.append("|").append(" dataLong1.1").append(" |")
            .append(" data1.2").append(" ".repeat(4)).append(" |")
            .append(" d1 ").append(" |")
            .append(" data1.4").append(" ".repeat(4)).append(" |");

        StringBuilder expectedRow2 = new StringBuilder();
        expectedRow2.append("|").append(" data2.1").append(" ".repeat(4)).append(" |")
            .append(" data2.2").append(" ".repeat(4)).append(" |")
            .append(" d2 ").append(" |")
            .append(" dataLong2.4").append(" |");

        Collection<String> expected = new ArrayList<>();
        expected.add(expectedHeader.toString());
        expected.add(expectedAlignments.toString());
        expected.add(expectedRow1.toString());
        expected.add(expectedRow2.toString());
        TablePrinter printer = new MarkdownTablePrinter();
        var result = printer.printTable(table, alignments);
        assertIterableEquals(expected, result, "Resulted table should be the same as the expected!");
    }
}
