package com.app.learning.utils;

import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ProgressCalculatorTest {

    @Test
    public void calculateProgress_zeroTotal_returnsZero() {
        assertEquals(0, ProgressCalculator.calculateProgress(0, 0));
        assertEquals(0, ProgressCalculator.calculateProgress(5, 0));
        assertEquals(0, ProgressCalculator.calculateProgress(-1, 10));
    }

    @Test
    public void calculateProgress_partialAndComplete_scenarios() {
        assertEquals(0, ProgressCalculator.calculateProgress(0, 10));
        assertEquals(50, ProgressCalculator.calculateProgress(5, 10));
        assertEquals(33, ProgressCalculator.calculateProgress(1, 3));
        assertEquals(100, ProgressCalculator.calculateProgress(10, 10));
        assertEquals(100, ProgressCalculator.calculateProgress(12, 10)); // overflow capped at 100
    }

    @Test
    public void calculateProgressFromList_scenarios() {
        assertEquals(0, ProgressCalculator.calculateProgressFromList(null));
        assertEquals(0, ProgressCalculator.calculateProgressFromList(Collections.emptyList()));

        List<Boolean> list = Arrays.asList(true, true, false, false);
        assertEquals(50, ProgressCalculator.calculateProgressFromList(list));

        List<Boolean> allTrue = Arrays.asList(true, true, true);
        assertEquals(100, ProgressCalculator.calculateProgressFromList(allTrue));
    }
}
