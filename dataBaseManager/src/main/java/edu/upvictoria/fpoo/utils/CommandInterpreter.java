package edu.upvictoria.fpoo.utils;

import edu.upvictoria.fpoo.exceptions.NotADBException;
import edu.upvictoria.fpoo.exceptions.SQLSyntaxException;

import javax.naming.NoPermissionException;
import java.io.*;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandInterpreter {

    private File folder;

    public CommandInterpreter(String folderPath) throws NotDirectoryException, FileNotFoundException, NoPermissionException, NotADBException {
        if (!folderPath.endsWith("_db"))
            throw new NotADBException("The folder is not a known database");
        this.folder = new IOUtils().openFolder(folderPath);
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }

    public void readFolder() {
        System.out.println("Give me the folder path");
        String folderPath = BrScanner.readLine();
        boolean directoryWorks = false;
        do {
            try {
                this.folder = new IOUtils().openFolder(folderPath);
                directoryWorks = true;
            } catch (NotDirectoryException | FileNotFoundException | NoPermissionException e) {
                System.err.println(e.getMessage());
            }
        } while (!directoryWorks);
    }

    public void readCommand() {
        String sentence = BrScanner.readMultipleLines();
        Map<String, String> parsedQuery = parseSQLQuery(sentence);
        try {
            actByQuery(parsedQuery);
        } catch (SQLSyntaxException e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * Check an SQL query, and, after that, will categorize the string in the different sql sentences
     *
     * @return {@code Map<String, String>} The map with the key, values of the sql query
     */
    public Map<String, String> parseSQLQuery(String sqlQuery) {
        Map<String, String> resultMap = new HashMap<>();
        Pattern pattern = Pattern.compile("(?i)(SELECT|FROM|WHERE|ORDER BY|CREATE TABLE|INSERT|UPDATE|DELETE|DROP TABLE|SHOW TABLES)\\s+(.*?)(?=\\s+(?i)(?:SELECT|FROM|WHERE|ORDER BY|CREATE TABLE|INSERT|UPDATE|DELETE|DROP TABLE|SHOW TABLES)|;|$)");
        Matcher matcher = pattern.matcher(sqlQuery);

        while (matcher.find()) {
            String key = matcher.group(1).toUpperCase();
            String value = matcher.group(2).trim();
            resultMap.put(key, value);
        }

        return resultMap;
    }

    public void actByQuery(Map<String, String> query) throws SQLSyntaxException {
        for (Map.Entry<String, String> entry : query.entrySet()) {
            String command = entry.getKey();
            String value = entry.getValue();

            if (command.equals("CREATE TABLE")) {
                try {
                    createTable(value);
                } catch (SQLSyntaxException e) {
                    System.err.println("SQLSyntaxException: " + e.getMessage());
                }
                return;
            }

            throw new SQLSyntaxException("Not a valid SQL syntax");
        }
    }

    public void createTable(String tableParams) throws SQLSyntaxException {
        Pattern pattern = Pattern.compile("(.*?)\\((.*?)\\)");
        Matcher matcher = pattern.matcher(tableParams);

        if (!matcher.find())
            throw new SQLSyntaxException("Not a valid sql syntax");

        String tableName = matcher.group(1);
        String columnsText = matcher.group(2);

        String[] columns = columnsText.toLowerCase().split(",");

        List<TableColumn> columnList = new ArrayList<>();

        for (String column : columns) {
            Pattern patternColumn = Pattern.compile("\\s*(\\w+)\\s+");
            Matcher matcherColumn = patternColumn.matcher(column);

            if (!matcherColumn.find())
                throw new SQLSyntaxException("Not a valid sql syntax");

            String columnName = matcherColumn.group(1);
            String columnSettings = column.substring(matcherColumn.end()).trim();
            String dataType = getDataType(columnName, columnSettings);
            boolean nullable = !columnSettings.contains("not null");
            boolean isPrimaryKey = columnSettings.contains("primary key");

            columnList.add(new TableColumn(columnName, dataType, nullable, isPrimaryKey));
        }

        String csvPath = folder + File.separator + tableName + ".csv";
        File file = new File(csvPath);

        try {
            new IOUtils().createNewFile(file);
        } catch (IOException e) {
            System.err.println("The table already exists");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath))) {
            for (TableColumn column : columnList) {
                writer.write(column.getColumnName() + ",");
            }
            writer.newLine();

            for (TableColumn column : columnList) {
                writer.write(column.getDataType() + ",");
            }
            writer.newLine();

            for (TableColumn column : columnList) {
                writer.write(column.isNullable() + ",");
            }
            writer.newLine();

            for (TableColumn column : columnList) {
                writer.write(String.valueOf(column.isPrimaryKey()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDataType (String columnName, String columnSettings) throws SQLSyntaxException {
        // String data types
        if (columnSettings.contains("char"))
            return "char";
        if (columnSettings.contains("varchar"))
            return "varchar";
        if (columnSettings.contains("tinytext"))
            return "tinytext";
        if (columnSettings.contains("text"))
            return "text";
        if (columnSettings.contains("longtext"))
            return "longtext";

        // Numeric data types
        if (columnSettings.contains("bit"))
            return "bit";
        if (columnSettings.contains("tinyint"))
            return "tinyint";
        if (columnSettings.contains("bool"))
            return "bool";
        if (columnSettings.contains("boolean"))
            return "boolean";
        if (columnSettings.contains("int"))
            return "int";
        if (columnSettings.contains("integer"))
            return "integer";
        if (columnSettings.contains("float"))
            return "float";
        if (columnSettings.contains("double"))
            return "double";
        if (columnSettings.contains("double precision"))
            return "double precision";
        if (columnSettings.contains("decimal"))
            return "decimal";
        if (columnSettings.contains("dec"))
            return "dec";

        // Date data types
        if (columnSettings.contains("date"))
            return "date";
        if (columnSettings.contains("datetime"))
            return "datetime";
        if (columnSettings.contains("time"))
            return "time";
        if (columnSettings.contains("timestamp"))
            return "timestamp";
        if (columnSettings.contains("year"))
            return "year";

        throw new SQLSyntaxException("Not a datatype provided for column " + columnName);
    }

    private static class NewTable {
        private final String tableName;
        private final List<String> columns;

        public NewTable(String tableName, List<String> columns) {
            this.tableName = tableName;
            this.columns = columns;
        }

        public String getTableName() {
            return tableName;
        }

        public List<String> getColumns() {
            return columns;
        }
    }

    private static class TableColumn {
        private final String columnName;
        private final String dataType;
        private final boolean nullable;
        private final boolean isPrimaryKey;

        public TableColumn(String columnName, String dataType, boolean nullable, boolean isPrimaryKey) {
            this.columnName = columnName;
            this.dataType = dataType;
            this.nullable = nullable;
            this.isPrimaryKey = isPrimaryKey;
        }

        public String getColumnName() {
            return columnName;
        }

        public String getDataType() {
            return dataType;
        }

        public boolean isNullable() {
            return nullable;
        }

        public boolean isPrimaryKey() {
            return isPrimaryKey;
        }
    }

    public void dropTable (String tableName) {
        
    }
}

