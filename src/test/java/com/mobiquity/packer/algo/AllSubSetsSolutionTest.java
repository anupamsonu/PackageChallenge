package com.mobiquity.packer.algo;

import com.mobiquity.packer.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import static org.junit.jupiter.api.Assertions.*;

class AllSubSetsSolutionTest {

    AllSubSetsSolution solution;
    Item[] testItems;
    double weightLimit;

    @BeforeEach
    void setUp() {
        testItems = new Item[]{new Item(1, 34.5, 45), new Item(2, 88.62, 98), new Item(3, 78.48, 3), new Item(4, 72.30, 76), new Item(5, 30.18, 9), new Item(6, 46.34, 48)};
        weightLimit = 81.1;
        solution = new AllSubSetsSolution(testItems, weightLimit);
    }

    @AfterEach
    void tearDown() {
        testItems = null;
        weightLimit = 0.0;

    }

    @Test
    void evaluateCurrentSubset3Items() {
        Item[] subSet = new Item[]{new Item(1, 34.5, 45),
                new Item(3, 78.48, 3),
                new Item(4, 72.30, 76)};

        solution.evaluateCurrentSubset(subSet);
        assertAll(
                () -> assertEquals(0.0, solution.getMaxCost()),
                () -> assertEquals("-", solution.getItemsToPick())
        );
    }
    @Test
    void evaluateCurrentSubset2Items() {
        Item[] subSet = new Item[]{new Item(1, 34.5, 45),
                new Item(5, 30.18, 9)};

        solution.evaluateCurrentSubset(subSet);
        assertAll(
                () -> assertEquals(54.0, solution.getMaxCost()),
                () -> assertEquals(",1,5", solution.getItemsToPick())
        );
    }

    @Test
    void evaluateCurrentSubset1Item() {
        Item[] subSet = new Item[]{new Item(1, 34.5, 45)};

        solution.evaluateCurrentSubset(subSet);
        assertAll(
                () -> assertEquals(45.0, solution.getMaxCost()),
                () -> assertEquals(",1", solution.getItemsToPick())
        );
    }

    @Test
    void addToSubset() {
        Item item = new Item(1, 34.5, 45);
        assumeTrue(testItems.length == 6);
        Item[] actual = solution.addToSubset(testItems, item);
        assertEquals(7, actual.length);
    }

    @Test
    void eliminateItemFromSubsetEnd() {
        assumeTrue(testItems.length == 6);
        Item[] actual = solution.eliminateItemFromSubsetEnd(testItems);
        assertEquals(5, actual.length);
    }
}