package com.mobiquity.packer.algo;

import com.mobiquity.packer.PackageCreator;
import com.mobiquity.packer.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class DynamicProgrammingSolutionTest {
    DynamicProgrammingSolution solution;
    Item[] testItems;
    double weightLimit;
    int multiplyFactor = 100;
    int[][] expected_matrix;
    int[][] test_matrix;

    @BeforeEach
    void setUp() {
        testItems = new Item[]{new Item(1, 5, 45),
                new Item(2, 2, 98),
                new Item(3, 3, 3),
                new Item(4, 2, 76),
                new Item(5, 3, 9),
                new Item(6, 4, 48)};

        expected_matrix = new int[][]{{0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 45, 45, 45, 45},
                {0, 0, 98, 98, 98, 98, 98, 143, 143},
                {0, 0, 98, 98, 98, 101, 101, 143, 143},
                {0, 0, 98, 98, 174, 174, 174, 177, 177},
                {0, 0, 98, 98, 174, 174, 174, 183, 183},
                {0, 0, 98, 98, 174, 174, 174, 183, 222}};
        test_matrix = expected_matrix;
        weightLimit = 8.0;
        multiplyFactor = PackageCreator.evaluateMultiplyFactor(testItems, weightLimit);
        assumeTrue(1 == multiplyFactor);
        //solution = new DynamicProgrammingSolution(testItems, weightLimit, multiplyFactor);

    }

    @AfterEach
    void tearDown() {
        testItems = null;
        weightLimit = 0.0;

    }

    @Test
    void setTotalItemsToPickFrom1MultiplyFactor() {
        assumeTrue(1 == multiplyFactor);
        solution = new DynamicProgrammingSolution(testItems, weightLimit, multiplyFactor);
        solution.setTotalItemsToPickFrom(testItems);
        assertEquals(testItems, solution.getTotalItemsToPickFrom());

    }

    @Test
    void setTotalItemsToPickFromNon1MultiplyFactor() {
        multiplyFactor = 10;
        assumeTrue(10 == multiplyFactor);
        solution = new DynamicProgrammingSolution(testItems, weightLimit, multiplyFactor);
        solution.setTotalItemsToPickFrom(testItems);
        assertNotEquals(testItems, solution.getTotalItemsToPickFrom());

    }

    @Test
    void createMatrix() {
        assumeTrue(1 == multiplyFactor);
        solution = new DynamicProgrammingSolution(testItems, weightLimit, multiplyFactor);
        int x = testItems.length + 1;
        int y = (int) weightLimit + 1;
        int matrix[][] = new int[x][y];
        solution.createMatrix(x - 1, y - 1, matrix);
        assertArrayEquals(expected_matrix, matrix);

    }

    @Test
    void backtrackToSelectItems() {
        assumeTrue(1 == multiplyFactor);
        solution = new DynamicProgrammingSolution(testItems, weightLimit, multiplyFactor);
        String actual = solution.backtrackToSelectItems(test_matrix);
        assertEquals("6,4,2", actual);
    }
}