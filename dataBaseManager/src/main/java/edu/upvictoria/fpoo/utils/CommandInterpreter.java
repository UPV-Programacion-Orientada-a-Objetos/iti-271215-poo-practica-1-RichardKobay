package edu.upvictoria.fpoo.utils;

import edu.upvictoria.fpoo.exceptions.EmptySelectException;
import edu.upvictoria.fpoo.exceptions.NotADBException;
import edu.upvictoria.fpoo.exceptions.PleaseDoNotEreaseEverithinException;
import edu.upvictoria.fpoo.exceptions.SQLSyntaxException;

import javax.naming.NoPermissionException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandInterpreter {

    private File folder;

    public CommandInterpreter(String folderPath) throws NotDirectoryException, FileNotFoundException, NoPermissionException, NotADBException {
        if (!folderPath.endsWith("_db")) {
            this.readFolder();
        }
        this.folder = new IOUtils().openFolder(folderPath);
    }

    public void readFolder() {
        System.out.println("Give me the folder path");
        String folderPath = BrScanner.readLine();
        if (!folderPath.endsWith("_db")) {
            System.err.println("The folder is not a known database");
            this.readFolder();
        }
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

        try {
            if (sentence.trim().toLowerCase().startsWith("create table")) {
                createTable(sentence.trim().replace("create table", "").replace(";", ""));
                return;
            }

            if (sentence.trim().toLowerCase().startsWith("drop table")) {
                dropTable(sentence.trim().replace("drop table", "").replace(";", "").trim());
            }

            if (sentence.trim().toLowerCase().startsWith("use")) {
                try {
                    use(sentence.trim().replace("use", "").replace(";", "").trim());
                } catch (NotADBException e) {
                    System.err.println(e.getMessage());
                }
                return;
            }

            if (sentence.trim().toLowerCase().startsWith("show tables")) {
                showTables();
                return;
            }

            if (sentence.trim().toLowerCase().startsWith("insert into")) {
                insert(sentence.trim().replace("insert into", "").replace(";", "").trim());
            }

            if (sentence.trim().toLowerCase().startsWith("select")) {
                Map<String, String> parsedQuery = parseSQLQuery(sentence);
                try {
                    select(parsedQuery);
                } catch (SQLSyntaxException e) {
                    System.err.println(e.getMessage());
                } catch (EmptySelectException e) {
                    System.out.println(ConsoleColors.ANSI_CYAN_BACKGROUND + ConsoleColors.ANSI_BLACK + e.getMessage() + ConsoleColors.ANSI_RESET);
                }
            }

            if (sentence.trim().toLowerCase().startsWith("delete")) {
                Map<String, String> parsedQuery = parseSQLQuery(sentence);
                try {
                    delete(parsedQuery);
                } catch (SQLSyntaxException e) {
                    System.err.println(e.getMessage());
                } catch (EmptySelectException e) {
                    System.out.println(ConsoleColors.ANSI_CYAN_BACKGROUND + ConsoleColors.ANSI_BLACK + e.getMessage() + ConsoleColors.ANSI_RESET);
                } catch (PleaseDoNotEreaseEverithinException e) {
                    System.out.println(ConsoleColors.ANSI_RED_BACKGROUND + ConsoleColors.ANSI_BLACK + e.getMessage() + ConsoleColors.ANSI_RESET);
                }
            }

        } catch (SQLSyntaxException e) {
            System.err.println(e.getMessage());
        }
    }


//    for (Map.Entry<String, String> entry : query.entrySet()) {
//        String command = entry.getKey();
//        String value = entry.getValue();
//    }

    /**
     * Check an SQL query, and, after that, will categorize the string in the different sql sentences
     *
     * @return {@code Map<String, String>} The map with the key, values of the sql query
     */
    public Map<String, String> parseSQLQuery(String sqlQuery) {
        Map<String, String> resultMap = new HashMap<>();
        if (sqlQuery.trim().toLowerCase().startsWith("use")) {
            String key = "USE";
            String value = sqlQuery.trim().split(" ")[1].replace(";", "");
            resultMap.put(key, value);
            return resultMap;
        }

        if (sqlQuery.trim().toLowerCase().startsWith("show tables")) {
            String key = "SHOW TABLES";
            String value = "";
            resultMap.put(key, value);
            return resultMap;
        }

        if (sqlQuery.trim().toLowerCase().startsWith("select")) {
            Pattern pattern = Pattern.compile("(?i)(SELECT|FROM|WHERE|ORDER BY|CREATE TABLE|INSERT INTO|UPDATE|DELETE|DROP TABLE|SHOW TABLES)\\s+(.*?)(?=\\s+(?i)(?:SELECT|FROM|WHERE|ORDER BY|CREATE TABLE|INSERT INTO|UPDATE|DELETE|DROP TABLE|SHOW TABLES)|;|$)");
            Matcher matcher = pattern.matcher(sqlQuery);

            while (matcher.find()) {
                String key = matcher.group(1).toUpperCase();
                String value = matcher.group(2).trim();
                resultMap.put(key, value);
            }
        }

        Pattern pattern = Pattern.compile("(?i)(SELECT|FROM|WHERE|ORDER BY|CREATE TABLE|INSERT INTO|UPDATE|DELETE|DROP TABLE|SHOW TABLES)\\s+(.*?)(?=\\s+(?i)(?:SELECT|FROM|WHERE|ORDER BY|CREATE TABLE|INSERT INTO|UPDATE|DELETE|DROP TABLE|SHOW TABLES)|;|$)");
        Matcher matcher = pattern.matcher(sqlQuery);

        while (matcher.find()) {
            String key = matcher.group(1).toUpperCase();
            String value = matcher.group(2).trim();
            resultMap.put(key, value);
        }

        return resultMap;
    }

    public void delete(Map<String, String> query) throws SQLSyntaxException, EmptySelectException, PleaseDoNotEreaseEverithinException {
        String delete = "";
        String tableName = "";
        String where = "";

        for (Map.Entry<String, String> entry : query.entrySet()) {
            String command = entry.getKey();
            String value = entry.getValue();

            if (command.equalsIgnoreCase("delete"))
                delete = value;

            if (command.equalsIgnoreCase("from"))
                tableName = value;

            if (command.equalsIgnoreCase("where"))
                where = value;
        }

        String tablePath = folder + File.separator + tableName + ".csv";
        List<String> initialTableColumns = getTableColumns(tablePath);

        Table table = new Table(tableName, initialTableColumns, tablePath);

        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < table.values.size(); i++) {
            indexes.add(i);
        }

        List<List<String>> newValues = table.values;

        if (where.isEmpty())
            throw new PleaseDoNotEreaseEverithinException("Not a where");

        indexes = table.searchRows(where);
        newValues = table.returnNewValues(initialTableColumns, delete);

        for (List<String> list : newValues) {
            for (int i = list.size() - 1; i >= 0; i--) {
                if (indexes.contains(i)) {
                    list.remove(i);
                }
            }
        }

        if (newValues.isEmpty())
            throw new SQLSyntaxException("There are not columns with that name");

        if (indexes.isEmpty())
            throw new EmptySelectException("There are not rows to show");

        table.writeTableToAFile(newValues, folder + File.separator + tableName + ".csv");
        System.out.println(ConsoleColors.ANSI_BLUE + "Table updated successfully" + ConsoleColors.ANSI_RESET);
    }

    public void select(Map<String, String> query) throws SQLSyntaxException, EmptySelectException {
        String select = "";
        String tableName = "";
        String where = "";
        String order = "";

        for (Map.Entry<String, String> entry : query.entrySet()) {
            String command = entry.getKey();
            String value = entry.getValue();

            if (command.equalsIgnoreCase("select"))
                select = value;

            if (command.equalsIgnoreCase("from"))
                tableName = value;

            if (command.equalsIgnoreCase("where"))
                where = value;

            if (command.equalsIgnoreCase("order by"))
                order = value;
        }

        String tablePath = folder + File.separator + tableName + ".csv";
        List<String> tableColumns = getTableColumns(tablePath);

        Table table = new Table(tableName, tableColumns, tablePath);

        List<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < table.values.size(); i++) {
            indexes.add(i);
        }

        List<List<String>> newValues = table.values;

        if (!where.isEmpty()) {
            indexes = table.searchRows(where);
            newValues = table.returnNewValues(tableColumns, select);

            for (List<String> list : newValues) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    if (!indexes.contains(i)) {
                        list.remove(i);
                    }
                }
            }

            if (newValues.isEmpty())
                throw new SQLSyntaxException("There are not columns with that name");
        }

        if (indexes.isEmpty())
            throw new EmptySelectException("There are not rows to show");

        if (!order.isEmpty()) {
            String[] orderSplit = order.split(" ");
            String orderBy = "asc";

            if (orderSplit.length > 1)
                orderBy = orderSplit[1].trim();

            if (orderBy.equalsIgnoreCase("asc")) {
                for (List<String> innerList : newValues) {
                    Collections.sort(innerList);
                }
            } else {
                for (List<String> innerList : newValues) {
                    innerList.sort(Collections.reverseOrder());
                }
            }
        }

        List<Integer> columnIndexes = table.getColumnIndexes(tableColumns, select);
        table.showValues(newValues, tableColumns, columnIndexes);
    }

    public void showTables() throws SQLSyntaxException {
        List<String> tables = new ArrayList<>();
        try {
            Files.walk(Paths.get(this.folder.toURI()))
                    .filter(Files::isRegularFile)
                    .forEach(path -> tables.add(folder + "/" + path.getFileName()));
        } catch (IOException e) {
            System.err.println("There was an error while reading the file");
        }

        for (String table : tables) {
            List<String> tableColumns = getTableColumns(table);
            System.out.println("---------------TABLE---------------");
            System.out.printf("Name: %29s\n", getTableName(table));
            for (String column : tableColumns) {
                System.out.printf("Column: %27s\n", column);
            }
            System.out.println();
        }
    }

    public String getTableName(String path) {
        List<String> tableName = Arrays.asList(path.trim().replace(".csv", "").split("/"));
        return tableName.get(tableName.size() - 1);
    }

    public void use(String folderPath) throws NotADBException {
        if (!folderPath.endsWith("_db"))
            throw new NotADBException("The folder is not a known database");
        try {
            this.folder = new IOUtils().openFolder(folderPath);
            System.out.println(ConsoleColors.ANSI_BLUE + "Using " + folderPath + ConsoleColors.ANSI_RESET);
        } catch (NotDirectoryException | FileNotFoundException | NoPermissionException e) {
            System.err.println(e.getMessage());
        }
    }

    public void createTable(String tableParams) throws SQLSyntaxException {
        Pattern pattern = Pattern.compile("(.*?)\\((.*?)\\)");
        Matcher matcher = pattern.matcher(tableParams);

        if (!matcher.find())
            throw new SQLSyntaxException("Not a valid sql syntax");

        String tableName = matcher.group(1).trim();
        String columnsText = matcher.group(2);

        String[] columns = columnsText.toLowerCase().split(",");
        List<String> columnsList = new ArrayList<>();

        for (String column : columns) {
            Pattern patternColumn = Pattern.compile("\\s*(\\w+)\\s+");
            Matcher matcherColumn = patternColumn.matcher(column);

            if (!matcherColumn.find())
                throw new SQLSyntaxException("Not a valid sql syntax");

            String columnName = matcherColumn.group(1);
            columnsList.add(columnName);
        }

        String csvPath = folder + File.separator + tableName + ".csv";
        File file = new File(csvPath);

        try {
            new IOUtils().createNewFile(file);
        } catch (IOException e) {
            System.err.println("The table already exists");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath))) {
            int i = 0;
            for (String column : columnsList) {
                writer.write(column);

                if (i < columnsList.size() - 1)
                    writer.write(",");
                i++;
            }
            writer.newLine();
        } catch (IOException e) {
            System.err.println("There was an error creating the table");
        }
        System.out.println(ConsoleColors.ANSI_CYAN + "Table " + tableName + " successfully created" + ConsoleColors.ANSI_RESET);
    }

    public void dropTable(String tableName) throws SQLSyntaxException {
        if (tableName.split(" ").length > 1)
            throw new SQLSyntaxException("Not a valid table name");

        String csvPath = folder + File.separator + tableName + ".csv";

        System.out.println("Are you sure you want to delete this database?");
        System.out.println("1. Yes");
        System.out.println("2. No");

        if (BrScanner.readInt() == 2)
            return;


        try {
            FileUtils.deleteFile(csvPath);
        } catch (FileNotFoundException e) {
            throw new SQLSyntaxException("The table does not exist");
        }

        System.out.println("Table dropped successfully");
    }

    public void insert(String commandParams) throws SQLSyntaxException {
        commandParams = commandParams.toLowerCase();

        Pattern pattern = Pattern.compile("(?i)\\b(\\w+)\\b\\s*\\((.*?)\\)\\s*values\\s*\\((.*?)\\)");
        Matcher matcher = pattern.matcher(commandParams);

        if (!matcher.find())
            throw new SQLSyntaxException("Not a valid sql syntax");

        String tableName = matcher.group(1);
        String columnNames = matcher.group(2);
        String values = matcher.group(3);

        if (columnNames.split(",").length != values.split(",").length)
            throw new SQLSyntaxException("The number of columns and values does not match");


        String tablePath = folder + File.separator + tableName + ".csv";
        List<String> columnsList = getTableColumns(tablePath);
        List<String> columns = new ArrayList<>(List.of(columnNames.split(",")));
        columns.replaceAll(String::trim);

        List<String> valuesList = new ArrayList<>(List.of(values.split(",")));
        valuesList.replaceAll(String::trim);

        if (!ListUtils.listEqualsIgnoreOrder(columns, columnsList))
            throw new SQLSyntaxException("Columns do not match");

        List<String> rightValues = new ArrayList<>();

        for (String column : columnsList) {
            for (String column2 : columns) {
                if (column.equals(column2)) {
                    int index = columns.indexOf(column2);
                    rightValues.add(valuesList.get(index));
                }
            }
        }

        StringBuilder lineContent = new StringBuilder();

        int i = 0;
        for (String value : rightValues) {
            lineContent.append(value);
            if (i < rightValues.size() - 1)
                lineContent.append(",");
            i++;
        }

        String csvPath = folder + File.separator + tableName + ".csv";
        FileUtils.addToFile(csvPath, String.valueOf(lineContent));
    }

    public List<String> getTableColumns(String tablePath) throws SQLSyntaxException {
        try (BufferedReader br = new BufferedReader(new FileReader(tablePath))) {
            String line = br.readLine();
            return Arrays.asList(line.split(","));
        } catch (FileNotFoundException e) {
            throw new SQLSyntaxException("The table does not exist");
        } catch (IOException e) {
            System.err.println("There was an error reading the table file");
        }

        throw new SQLSyntaxException("The table does not match with a known format");
    }

    private static class Table {
        private String tableName;
        private List<String> columns;
        private List<List<String>> values;

        private Table(String tableName, List<String> columns, String tablePath) {
            this.tableName = tableName;
            this.columns = columns;
            this.values = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(tablePath))) {
                String line;
                boolean firstLine = true;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (firstLine) {
                        for (int i = 0; i < values.length; i++) {
                            this.values.add(new ArrayList<>());
                        }
                        firstLine = false;
                    } else {
                        for (int i = 0; i < values.length; i++) {
                            this.values.get(i).add(values[i]);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("There was an error reading the table file");
            }
        }

        private List<Integer> searchRows(String input) throws SQLSyntaxException {
            List<Integer> indexes = new ArrayList<>();
            String[] conditions = input.split("(?i)\\s+(OR|AND)\\s+");

            for (String condition : conditions) {
                Pattern pattern = Pattern.compile("\\s*(\\w+)\\s*([<>]=?|=)\\s*('([^']*)'|\\w+)\\s*");
                Matcher matcher = pattern.matcher(condition);

                if (!matcher.find())
                    throw new SQLSyntaxException("Not a valid SQL Syntax");

                String columnName = matcher.group(1);
                String operator = matcher.group(2);
                String value = matcher.group(4) != null ? matcher.group(4) : matcher.group(3);

                int columnIndex = this.columns.indexOf(columnName);

                if (columnIndex == -1)
                    throw new SQLSyntaxException("Could not get a column named " + columnName);

                for (int i = 0; i < values.get(columnIndex).size(); i++) {
                    String actualValue = values.get(columnIndex).get(i);

                    if (operator.equalsIgnoreCase("=")) {
                        if (actualValue.equals(value)) {
                            indexes.add(i);
                        }
                    } else if (operator.equalsIgnoreCase("!=")) {
                        if (!actualValue.equals(value)) {
                            indexes.add(i);
                        }
                    } else if (operator.equals("<")) {
                        if (isNumeric(actualValue) && isNumeric(value)) {
                            double numActual = Double.parseDouble(actualValue);
                            double numValor = Double.parseDouble(value);
                            if (numActual < numValor) {
                                indexes.add(i);
                            }
                        }
                    } else if (operator.equals(">")) {
                        if (isNumeric(actualValue) && isNumeric(value)) {
                            double numActual = Double.parseDouble(actualValue);
                            double numValor = Double.parseDouble(value);
                            if (numActual > numValor) {
                                indexes.add(i);
                            }
                        }
                    } else if (operator.equalsIgnoreCase("<=")) {
                        if (isNumeric(actualValue) && isNumeric(value)) {
                            double numActual = Double.parseDouble(actualValue);
                            double numValor = Double.parseDouble(value);
                            if (numActual <= numValor) {
                                indexes.add(i);
                            }
                        }
                    } else if (operator.equalsIgnoreCase(">=")) {
                        if (isNumeric(actualValue) && isNumeric(value)) {
                            double numActual = Double.parseDouble(actualValue);
                            double numValor = Double.parseDouble(value);
                            if (numActual >= numValor) {
                                indexes.add(i);
                            }
                        }
                    }
                }
            }

            return indexes;
        }

        public boolean isNumeric(String str) {
            if (str == null || str.isEmpty()) {
                return false;
            }
            for (char c : str.toCharArray()) {
                if (!Character.isDigit(c) && c != '.' && c != '-') {
                    return false;
                }
            }
            return true;
        }

        List<Integer> getColumnIndexes(List<String> columns, String select) {
            List<Integer> columnIndexes = new ArrayList<>();

            if (select.equalsIgnoreCase("*")) {
                for (int i = 0; i < columns.size(); i++) {
                    columnIndexes.add(i);
                }
            } else {
                List<String> columnNames = Arrays.asList(select.trim().split(","));
                for (String columnName : columnNames) {
                    columnName.trim();
                }
                for (int i = 0; i < columns.size(); i++) {
                    for (String columnName : columnNames) {
                        if (columnName.equalsIgnoreCase(columns.get(i))) {
                            columnIndexes.add(i);
                        }
                    }
                }
            }

            return columnIndexes;
        }

        private List<List<String>> returnNewValues(List<String> columns, String select) {
            List<Integer> columnIndexes = getColumnIndexes(columns, select);


            List<List<String>> newValues = new ArrayList<>();
            for (int column : columnIndexes) {
                newValues.add(values.get(column));
            }

            return newValues;
        }

        public void showValues(List<List<String>> newValues, List<String> columns, List<Integer> columnIndexes) {
            for (int columnIndex : columnIndexes) {
                System.out.printf("%20s", columns.get(columnIndex));
            }

            System.out.println();

            for (int column = 0; column < newValues.get(0).size(); column++) {
                for (List<String> value : newValues) {
                    System.out.printf("%20s", value.get(column));
                }
                System.out.println();
            }
        }

        private void writeTableToAFile (List<List<String>> newValues, String csvPath) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath))) {
                for (int i = 0; i < columns.size(); i++) {
                    writer.write(columns.get(i));
                    if (i < columns.size() - 1)
                        writer.write(",");
                    else
                        writer.newLine();
                }

                for (int i = 0; i < newValues.get(0).size(); i++) {
                    for (int j = 0; j < newValues.size(); j++) {
                        writer.write(newValues.get(j).get(i));
                        // writer.write(newValues.get(j).get(i));
                        if (j < newValues.size() - 1) {
                            writer.write(",");
                        } else {
                            writer.newLine();
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error printing file: " + e.getMessage());
            }
        }

        private String getTableName() {
            return tableName;
        }

        private void setTableName(String tableName) {
            this.tableName = tableName;
        }

        private List<String> getColumns() {
            return columns;
        }

        private void setColumns(List<String> columns) {
            this.columns = columns;
        }

        private List<List<String>> getValues() {
            return values;
        }

        private void setValues(List<List<String>> values) {
            this.values = values;
        }
    }
}

