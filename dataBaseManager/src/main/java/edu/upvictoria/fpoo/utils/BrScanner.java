package edu.upvictoria.fpoo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class will help with a user inputs
 * */
public class BrScanner {
    /**
     * Will read a String line and return to the user
     * @return String: The user input
     * */
    public static String readLine() {
        String line = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            line = br.readLine();
        } catch (IOException e) {
            System.err.println("There was an error reading the input");
        }

        return line;
    }

    /**
     * Will read an integer from a user input
     * If the user enters a non integer input, will be showed by the console output
     * @return int: The integer user input
     * */
    public static int readInt() {
        int line = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            line = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            System.err.println("There was an error reading the input");
        } catch (NumberFormatException e) {
            System.err.println("La entrada ingresada no es un número");
        }

        return line;
    }

    /**
     * Will read a double from a user input
     * If the user enters a non-double input, will be showed by the console output
     * @return double: The double user input
     * */
    public static double readDouble() {
        double line = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            line = Double.parseDouble(br.readLine());
        } catch (IOException e) {
            System.err.println("There was an error reading the input");
        } catch (NumberFormatException e) {
            System.err.println("La entrada ingresada no es un número");
        }
        return line;
    }

}
