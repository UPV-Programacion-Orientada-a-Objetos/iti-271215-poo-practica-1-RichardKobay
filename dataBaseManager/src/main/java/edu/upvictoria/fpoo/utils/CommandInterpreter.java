package edu.upvictoria.fpoo.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandInterpreter {
    public void readCommand() {
        String sentence = BrScanner.readMultipleLines();
        List<Map<String, String>> commandList = parseSQLQuery(sentence);

        for (Map<String, String> map : commandList) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
        }
    }

    public static List<Map<String, String>> parseSQLQuery(String sqlQuery) {
        List<Map<String, String>> resultList = new ArrayList<>();
        String[] keywords = {"SELECT", "FROM", "WHERE", "CREATE TABLE", "DELETE", ""};

        String[] sqlQuerySplit = sqlQuery.split(" ");

        Map<String, String> resultMap = new HashMap<>();

        return resultList;
    }
}

