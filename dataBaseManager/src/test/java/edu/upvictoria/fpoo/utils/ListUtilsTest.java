package edu.upvictoria.fpoo.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ListUtilsTest {

    @Test
    public void testListEqualsIgnoreOrderSameLists() {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(1, 2, 3);

        assertTrue(ListUtils.listEqualsIgnoreOrder(list1, list2));
    }

    @Test
    public void testListEqualsIgnoreOrderDifferentOrder() {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(3, 2, 1);

        assertTrue(ListUtils.listEqualsIgnoreOrder(list1, list2));
    }

    @Test
    public void testListEqualsIgnoreOrderDifferentElements() {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(1, 2, 4);

        assertFalse(ListUtils.listEqualsIgnoreOrder(list1, list2));
    }

    @Test
    public void testListEqualsIgnoreOrderEmptyLists() {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        assertTrue(ListUtils.listEqualsIgnoreOrder(list1, list2));
    }

    @Test
    public void testListEqualsIgnoreOrderNullLists() {
        List<Integer> list1 = null;
        List<Integer> list2 = null;

        assertThrows(NullPointerException.class, () -> {
            ListUtils.listEqualsIgnoreOrder(list1, list2);
        });
    }
}