package com.mobiquity.packer;

import com.mobiquity.PackagingConstants;
import com.mobiquity.packer.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class PackageCreatorTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Select Items for current package")
    void selectItemsForCurrentPackage() {
        Item[] testItems = new Item[]{new Item(1, 34.5, 45), new Item(2, 88.62, 98), new Item(3, 78.48, 3), new Item(4, 72.30, 76), new Item(5, 30.18, 9), new Item(6, 46.34, 48)};
        double weightLimit = 81.1;
        String actualResult = PackageCreator.selectItemsForCurrentPackage(testItems, weightLimit);
        assertNotNull(actualResult);
        assertEquals(",1,6", actualResult);
    }

    @Test
    @DisplayName("Evaluate Multiplication factor")
    void evaluateMultiplyFactor() {
        Item[] actualItems = new Item[]{new Item(1, 34.5, 45),
                new Item(2, 88.62, 98),
                new Item(3, 78.48, 3),
                new Item(4, 72.30, 76),
                new Item(5, 30.18, 9),
                new Item(16, 46.34, 48)};
        assertEquals(100, PackageCreator.evaluateMultiplyFactor(actualItems, PackagingConstants.PACKAGE_WEIGHT_LIMIT));
    }

    @ParameterizedTest(name = "Test with weightLimit,length,multiplyFactor = {0}, {1}, {2} ")
    @CsvSource({
            "9.6, 7, 1",
            "10.0,  8, 1"
    })
    void useDPAlgorithmTrue(double weightLimit, int length, int multiplyFactor) {
        assertTrue(PackageCreator.useDPAlgorithm(weightLimit, length, multiplyFactor));
    }

    @ParameterizedTest(name = "Test with weightLimit,length,multiplyFactor = {0}, {1}, {2}")
    @CsvSource({
            "100.0, 6, 10",
            "10.0,  8, 100"
    })
    void useDPAlgorithmFalse(double weightLimit, int length, int multiplyFactor) {
        assertFalse(PackageCreator.useDPAlgorithm(weightLimit, length, multiplyFactor));
    }

}