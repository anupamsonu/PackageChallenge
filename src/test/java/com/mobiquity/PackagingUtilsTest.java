package com.mobiquity;

import com.mobiquity.packer.model.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PackagingUtilsTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @ParameterizedTest(name = "split String {0} into array of Strings")
    @ValueSource(strings =
            {"(1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)"
            })
    void splitItemArrayStrtoItemsStr(String toSplit) {
        String[] actual = PackagingUtils.splitItemsListStrToItemsArray(toSplit);
        final String[] expected = new String[]{"1,53.38,€45", "2,88.62,€98", "3,78.48,€3", "4,72.30,€76", "5,30.18,€9", "6,46.34,€48"};
        assertArrayEquals(expected, actual);
    }

    @Test
    void convertStringArrayToItemArray() {
        String[] toSplit = new String[]{"1,53.38,€45", "2,88.62,€98", "3,78.48,€3", "4,72.30,€76", "5,30.18,€9", "6,46.34,€48"};
        Item[] actual = PackagingUtils.convertStringArrayToItemArray(toSplit);
        assertEquals(6, actual.length);
    }

/*    @org.junit.jupiter.api.Test
    void convertItemStrToItem() {
    }*/
}