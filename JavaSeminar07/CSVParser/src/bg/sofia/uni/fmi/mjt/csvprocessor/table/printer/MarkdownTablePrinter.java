package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MarkdownTablePrinter implements TablePrinter {
    private static final int MIN_LENGTH = 3;
    private Map<String, Integer> colsWidths;

    public MarkdownTablePrinter() {
        colsWidths = new LinkedHashMap<>();
    }

    private void fillColsWidths(Table table) {
        var names = table.getColumnNames();
        for (var n : names) {
            var colData = table.getColumnData(n);
            int maxWidth = n.length();
            for (var s : colData) {
                if (s.length() > maxWidth) {
                    maxWidth = s.length();
                }
            }
            if (maxWidth < MIN_LENGTH) {
                maxWidth = MIN_LENGTH;
            }
            colsWidths.put(n, maxWidth);
        }
    }

    private String printHeader(Table table) {
        StringBuilder builder = new StringBuilder();
        builder.append('|');
        var names = table.getColumnNames();
        for (var n : names) {
            builder.append(' ');
            builder.append(n);
            int colLength = colsWidths.get(n);
            for (int i = 0; i < colLength - n.length(); i++) {
                builder.append(' ');
            }
            builder.append(" |");
        }
        System.out.println(builder);
        return builder.toString();
    }

    private String printLeftAlignment(int length) {
        StringBuilder builder = new StringBuilder();
        builder.append(':');
        for (int i = 0; i < length - 1; i++) {
            builder.append('-');
        }
        return builder.toString();
    }

    private String printRightAlignment(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length - 1; i++) {
            builder.append('-');
        }
        builder.append(':');
        return builder.toString();
    }

    private String printCenterAlignment(int length) {
        StringBuilder builder = new StringBuilder();
        builder.append(':');
        for (int i = 0; i < length - 2; i++) {
            builder.append('-');
        }
        builder.append(':');
        return builder.toString();
    }

    private String printNoAlignment(int length) {
        return "-".repeat(length);
    }

    private String printAlignments(Table table, ColumnAlignment... alignments) {
        StringBuilder builder = new StringBuilder();
        builder.append('|');
        var names = table.getColumnNames();
        int alIndex = 0;
        for (var n : names) {
            builder.append(' ');
            int colLength = colsWidths.get(n);
            var currAlign = ColumnAlignment.NOALIGNMENT;
            if (alIndex < alignments.length) {
                currAlign = alignments[alIndex];
            }
            switch (currAlign) {
                case LEFT -> builder.append(printLeftAlignment(colLength));
                case RIGHT -> builder.append(printRightAlignment(colLength));
                case CENTER -> builder.append(printCenterAlignment(colLength));
                case NOALIGNMENT -> builder.append(printNoAlignment(colLength));
            }
            builder.append(" |");
            alIndex++;
        }
        System.out.println(builder);
        return builder.toString();
    }

    private String printRow(Table table, int rowIndex) {
        StringBuilder builder = new StringBuilder();
        builder.append('|');
        var names = table.getColumnNames();
        for (var n : names) {
            builder.append(' ');
            var col = table.getColumnData(n);
            Iterator<String> iterator = col.iterator();
            for (int i = 0; i < rowIndex; i++) {
                iterator.next();
            }
            var data = iterator.next();
            builder.append(data);
            int colLength = colsWidths.get(n);
            for (int i = 0; i < colLength - data.length(); i++) {
                builder.append(' ');
            }
            builder.append(" |");
        }
        System.out.println(builder);
        return builder.toString();
    }

    private Collection<String> printRows(Table table) {
        var rowsCount = table.getRowsCount();
        Collection<String> rows = new ArrayList<>();
        for (int i = 0; i < rowsCount - 1; i++) {
            rows.add(printRow(table, i));
        }
        return rows;
    }

    @Override
    public Collection<String> printTable(Table table, ColumnAlignment... alignments) {
        fillColsWidths(table);
        Collection<String> rows = new ArrayList<>();
        rows.add(printHeader(table));
        rows.add(printAlignments(table, alignments));
        rows.addAll(printRows(table));
        return Collections.unmodifiableCollection(rows);
    }
}
