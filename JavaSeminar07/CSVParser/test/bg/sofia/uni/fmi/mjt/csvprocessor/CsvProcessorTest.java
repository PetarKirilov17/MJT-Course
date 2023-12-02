package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvProcessorTest {
    @Test
    void testReadCsv() throws CsvDataNotCorrectException {
        String multiline = """
            Header1,LongHeader2,H3,Header4,
            dataLong1.1,data1.2,d1,data1.4
            data2.1,data2.2,d2,dataLong2.4
            """;
        StringReader reader = new StringReader(multiline);
        CsvProcessorAPI processor = new CsvProcessor();
        processor.readCsv(reader, ",");
    }

    @Test
    void testWriteTableThrowsException() {
        Writer writer = new StringWriter();
        CsvProcessorAPI processor = new CsvProcessor();
        assertThrows(IllegalArgumentException.class, () -> processor.writeTable(writer, null),
            "Write table should throw exception when alignments is null!");
    }

    @Test
    void testWriterTableSuccessfully() {
        Writer writer = new StringWriter();
        ColumnAlignment[] alignments = new ColumnAlignment[]{ColumnAlignment.NOALIGNMENT, ColumnAlignment.LEFT, ColumnAlignment.RIGHT,
            ColumnAlignment.CENTER};
        CsvProcessorAPI processor = new CsvProcessor();
        processor.writeTable(writer, alignments);
    }
}
