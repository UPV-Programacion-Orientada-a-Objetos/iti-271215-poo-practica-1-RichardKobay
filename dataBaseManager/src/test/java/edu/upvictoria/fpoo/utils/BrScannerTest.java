package edu.upvictoria.fpoo.utils;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class BrScannerTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    @Test
    public void testReadLine_1() {
        String input = "Hello World";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        String result = BrScanner.readLine();
        assertEquals(input, result);
    }

    @Test
    public void testReadLine_2() {
        String input = "Textito de entrada";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        String result = BrScanner.readLine();
        assertEquals(input, result);
    }

    @Test
    public void testReadLine_3() {
        String input = "otro texto";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        String result = BrScanner.readLine();
        assertEquals(input, result);
    }

    @Test
    public void testReadLine_4() {
        String input = "Texto con números 22";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        String result = BrScanner.readLine();
        assertEquals(input, result);
    }

    @Test
    public void testReadLine_5() {
        String input = "Mas texto con números negativos y toda la cosa -22, --333 html";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        String result = BrScanner.readLine();
        assertEquals(input, result);
    }

    @Test
    public void testReadInt_1() throws IOException {
        String input = "123";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        int result = BrScanner.readInt();
        assertEquals(123, result);
    }

    @Test
    public void testReadInt_2() {
        String input = "-123";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        int result = BrScanner.readInt();
        assertEquals(-123, result);
    }

    @Test
    public void testReadInt_3() {
        String input = "-123-22";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        int result = BrScanner.readInt();
        assertEquals(0, result);
    }

    @Test
    public void testReadInt_4() {
        String input = "-123.22";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        int result = BrScanner.readInt();
        assertEquals(0, result);
    }

    @Test
    public void testReadInt_5() {
        String input = "123456789";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        int result = BrScanner.readInt();
        assertEquals(123456789, result);
    }

    @Test
    public void testReadDouble() {
        String input = "texto";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        int result = BrScanner.readInt();
        assertEquals(0, result);
    }

    @Test
    public void testReadMultipleLines_1() {
        String input = "texto;";
        String output = "texto; ";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        String result = BrScanner.readMultipleLines();
        assertEquals(output, result);
    }

    @Test
    public void testReadMultipleLines_2() {
        String input = "select * from table where id = 22;";
        String output = "select * from table where id = 22; ";
        System.setIn(new java.io.ByteArrayInputStream(input.getBytes()));
        String result = BrScanner.readMultipleLines();
        assertEquals(output, result);
    }
}