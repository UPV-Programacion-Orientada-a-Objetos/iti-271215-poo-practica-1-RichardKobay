package edu.upvictoria.fpoo.utils;

import edu.upvictoria.fpoo.exceptions.NotADBException;
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
        if (!folderPath.endsWith("_db"))
            throw new NotADBException("The folder is not a known database");
        this.folder = new IOUtils().openFolder(folderPath);
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

        Pattern pattern = Pattern.compile("(?i)(SELECT|FROM|WHERE|ORDER BY|CREATE TABLE|INSERT INTO|UPDATE|DELETE|DROP TABLE|SHOW TABLES)\\s+(.*?)(?=\\s+(?i)(?:SELECT|FROM|WHERE|ORDER BY|CREATE TABLE|INSERT INTO|UPDATE|DELETE|DROP TABLE|SHOW TABLES)|;|$)");
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

            if (command.equals("DROP TABLE")) {
                try {
                    dropTable(value);
                } catch (SQLSyntaxException e) {
                    System.err.println("SQLSyntaxException: " + e.getMessage());
                }
                return;
            }

            if (command.equals("INSERT INTO")) {
                try {
                    insert(value);
                } catch (SQLSyntaxException e) {
                    System.err.println("SQLSyntaxException: " + e.getMessage());
                }
                return;
            }

            if (command.equalsIgnoreCase("USE")) {
                try {
                    use(value);
                } catch (NotDirectoryException | FileNotFoundException | NoPermissionException | NotADBException e) {
                    System.err.println(e.getMessage());
                }
                return;
            }

            if (command.equalsIgnoreCase("SHOW TABLES")) {
                try {
                    showTables();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                return;
            }
            throw new SQLSyntaxException("Not a valid SQL syntax");
        }
    }

    public void showTables() {
        List<String> tables = new ArrayList<>();
        try {
            Files.walk(Paths.get(this.folder.toURI()))
                    .filter(Files::isRegularFile)
                    .forEach(path -> tables.add(folder + "/" + path.getFileName()));
        } catch (IOException e) {
            System.err.println("There was an error while reading the file");
        }

        for (String table : tables) {
            try {
                List<String> tableColumns =  getTableColumns(table);
                System.out.println("---------------TABLE---------------");
                System.out.printf("Name: %29s\n", getTableName(table));
                for (String column : tableColumns) {
                    System.out.printf("Column: %27s\n", column);
                }
                System.out.println();
            } catch (SQLSyntaxException e) {
                System.err.println("SQLSyntaxException: " + e.getMessage());
            }
        }
    }

    public String getTableName (String path) {
        List<String> tableName = Arrays.asList(path.trim().replace(".csv", "").split("/"));
        return tableName.get(tableName.size() - 1);
    }

    public void use(String folderPath) throws NotADBException, NotDirectoryException, FileNotFoundException, NoPermissionException {
        if (!folderPath.endsWith("_db"))
            throw new NotADBException("The folder is not a known database");
        this.folder = new IOUtils().openFolder(folderPath);
        System.out.println(ConsoleColors.ANSI_BLUE + "Using " + folderPath + ConsoleColors.ANSI_RESET);
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

        public Table(String tableName, List<String> columns) {
            this.tableName = tableName;
            this.columns = columns;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public List<String> getColumns() {
            return columns;
        }

        public void setColumns(List<String> columns) {
            this.columns = columns;
        }
    }
}

