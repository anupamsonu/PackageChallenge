package com.mobiquity;

import com.mobiquity.exception.APIException;
import com.mobiquity.packer.model.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

class PackagingUtilsTest {
    boolean[] boolean_actual;
    int count = 0;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        boolean_actual = new boolean[4];
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @ParameterizedTest(name = "split String {0} into array of Strings")
    @ValueSource(strings =
            {"(1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)"
            })
    void splitItemArrayStrToItemsStr(String toSplit) {
        String[] actual = PackagingUtils.splitItemsListStrToItemsArray(toSplit);
        final String[] expected = new String[]{"1,53.38,€45", "2,88.62,€98", "3,78.48,€3", "4,72.30,€76", "5,30.18,€9", "6,46.34,€48"};
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Item array size matches the string input")
    void convertStringArrayToItemArray() {
        String[] toSplit = new String[]{"1,53.38,€45", "2,88.62,€98", "3,78.48,€3", "4,72.30,€76", "5,30.18,€9", "6,46.34,€48"};
        Item[] actual = PackagingUtils.convertStringArrayToItemArray(toSplit);
        assertEquals(6, actual.length);
    }

    @ParameterizedTest(name = "remove brackets from input strings")
    @ValueSource(strings = {
            "(1,53.38,€45)",
            "(2,88.62,€98)",
            "(3,78.48,€3)",
            "(4,72.30,€76)",
            "(5,30.18,€9)",
            "(6,46.34,€48)"
    })
    void replaceBrackets(String testString) {
        System.out.println("actual =" + PackagingUtils.replaceBrackets(testString));
        assertAll(
                () -> assertFalse(PackagingUtils.replaceBrackets(testString).contains("\\(")),
                () -> assertFalse(PackagingUtils.replaceBrackets(testString).contains("\\)"))
        );
    }

    @ParameterizedTest(name = "remove brackets from input strings")
    @ValueSource(strings = {"€45", "€98", "€3", "€76", "€9", "€48"})
    void stripSymbolFromCost(String testString) {
        System.out.println("actual =" + PackagingUtils.stripSymbolFromCost(testString));
        assertAll(
                () -> assertFalse(PackagingUtils.stripSymbolFromCost(testString).contains("€"))
        );

    }

    @ParameterizedTest(name = "remove brackets from input strings")
    @ValueSource(strings = {"81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)"
    })
    void createItemsFromLine(String testStr) {
        Item[] actualItems = PackagingUtils.createItemsFromLine(testStr);
        //System.out.println(actualItems[0]);
        assertEquals(6, actualItems.length, "Expected 6 items from the test String");
        assertAll(
                () -> assertEquals(new Item(1, 53.38, 45), actualItems[0], "Item at index 1 is not equal to expected"),
                () -> assertEquals(new Item(2, 88.62, 98), actualItems[1], "Item at index 2 is not equal to expected"),
                () -> assertEquals(new Item(3, 78.48, 3), actualItems[2], "Item at index 3 is not equal to expected"),
                () -> assertEquals(new Item(4, 72.30, 76), actualItems[3], "Item at index 4 is not equal to expected"),
                () -> assertEquals(new Item(5, 30.18, 9), actualItems[4], "Item at index 5 is not equal to expected"),
                () -> assertEquals(new Item(6, 46.34, 48), actualItems[5], "Item at index 6 is not equal to expected")

        );
    }

    @ParameterizedTest(name = "Validate items exceeding weight limitation")
    @ValueSource(strings = {"81 : (1,101.2,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)"
    })
    void validateWeightItems(String testString) {
        Item[] actualItems = PackagingUtils.createItemsFromLine(testString);
        APIException ex =
                assertThrows(APIException.class, () -> PackagingUtils.validateItems(actualItems));
        System.out.println(ex.getMessage());
        assertThat(ex.getMessage(), containsString("Item weight cannot exceed 100"));

    }

    @ParameterizedTest(name = "Validate items exceeding cost limitation")
    @ValueSource(strings = {"81 : (1,34.56,€45) (2,88.62,€102) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)"
    })
    void validateCostForItems(String testString) {
        Item[] actualItems = PackagingUtils.createItemsFromLine(testString);
        APIException ex =
                assertThrows(APIException.class, () -> PackagingUtils.validateItems(actualItems));
        System.out.println(ex.getMessage());
        assertThat(ex.getMessage(), containsString("Item cost cannot exceed 100"));

    }

    @ParameterizedTest(name = "Validate items exceeding expected index")
    @ValueSource(strings = {"81 : (1,34.56,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (16,46.34,€48)"
    })
    void validateIndexForItems(String testString) {
        Item[] actualItems = PackagingUtils.createItemsFromLine(testString);
        APIException ex =
                assertThrows(APIException.class, () -> PackagingUtils.validateItems(actualItems));
        System.out.println(ex.getMessage());
        assertThat(ex.getMessage(), containsString("Items index exceeds 15 (there can only be 15 items))"));

    }

    @ParameterizedTest(name = "Weight limit should not be exceeded")
    @ValueSource(doubles = {34.1, 10.3, 54.344, 67.789})
    void validateWeightLimit(double dble) throws APIException {
        assertTrue(PackagingUtils.validateWeightLimit(dble));
    }

    @ParameterizedTest(name = "Weight is exceeded")
    @ValueSource(doubles = {134.1, 110.3, 154.344, 167.789})
    void validateWeightLimitExceeded(double dble) throws APIException {
        APIException ex =
                assertThrows(APIException.class, () -> PackagingUtils.validateWeightLimit(dble));
        assertThat(ex.getMessage(), containsString("weight limit for row number: should be less than 100"));

    }


    @ParameterizedTest(name = "Scale is incorrect")
    @ValueSource(doubles = {34.2, 10.3, 54.30, 100.1})
    void getScaleOfOne(double dble) {
        int scale = PackagingUtils.getScale(dble);
        assertEquals(1, scale);
    }

    @ParameterizedTest(name = "Scale is incorrect")
    @ValueSource(doubles = {34.12, 10.34, 54.63, 100.120})
    void getScaleOfTwo(double dble) {
        int scale = PackagingUtils.getScale(dble);
        assertEquals(2, scale);
    }

    @ParameterizedTest(name = "Scale is incorrect")
    @ValueSource(doubles = {34.00, 10.0, 54, 100.000})
    void getScaleOfZero(double dble) {
        int scale = PackagingUtils.getScale(dble);
        assertEquals(0, scale);
    }

}