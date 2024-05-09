package edu.upvictoria.fpoo.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    protected InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    protected BufferedReader fromInputStreamToBufferedReader (InputStream is) {
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }

    public InputStream getFileAsInputStream(String fileName) {
        FileUtils fileUtils = new FileUtils();
        return fileUtils.getFileFromResourceAsStream(fileName);
    }

    public static boolean deleteFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);

        if (file.delete())
            return true;

        throw new FileNotFoundException("File not found");
    }

    public static void addToFile(String filePath, String content) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true))) {
            bufferedWriter.write(content);
            bufferedWriter.newLine();
        } catch (IOException e) {
            System.err.println("There was an error writing to the file: " + filePath);
        }
    }
}
