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

            if (command.equals("DROP TABLE")) {
                dropTable(value);
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
            int i =  0;
            for (String column : columnsList) {
                writer.write(column);

                if (i < columnsList.size() - 1)
                    writer.write(",");
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropTable (String tableName) throws SQLSyntaxException {
        if (tableName.split(" ").length > 1)
            throw new SQLSyntaxException("Not a valid table name");

        String csvPath = folder + File.separator + tableName + ".csv";

        try {
            FileUtils.deleteFile(csvPath);
        } catch (FileNotFoundException e) {
            throw new SQLSyntaxException("The table does not exist");
        }

        System.out.println("Table dropped successfully");
    }
}

