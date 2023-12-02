package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.MarkdownTablePrinter;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.TablePrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;

public class CsvProcessor implements CsvProcessorAPI {
    private Table table;

    public CsvProcessor() {
        this(new BaseTable());
    }

    public CsvProcessor(Table table) {
        this.table = table;
    }

    @Override
    public void readCsv(Reader reader, String delimiter) throws CsvDataNotCorrectException {
        try (var bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                var splitted = line.split("\\Q" + delimiter + "\\E");
                this.table.addData(splitted);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void writeTable(Writer writer, ColumnAlignment... alignments) {
        if (alignments == null) {
            throw new IllegalArgumentException("Alignments cannot be null");
        }
        TablePrinter printer = new MarkdownTablePrinter();
        Collection<String> tableToPrint = printer.printTable(this.table, alignments);
        int rowsToPrint = tableToPrint.size();
        int lineNum = 1;
        try {
            for (var r : tableToPrint) {
                writer.write(r);
                if (lineNum < rowsToPrint) {
                    writer.write(System.lineSeparator());
                }
                lineNum++;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
