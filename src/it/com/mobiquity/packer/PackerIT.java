package com.mobiquity.packer;

import com.mobiquity.exception.APIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PackerIT {

    @ParameterizedTest(name = "processAPackage with String {0}")
    @ValueSource(strings = {"81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)",
    })
    void processAPackage(String testLine) throws APIException {
        String actual = Packer.processAPackage(testLine);
        assertNotNull(actual);
        assertEquals(",4", actual);
    }


    @ParameterizedTest(name = "processAPackage with String {0}")
    @ValueSource(strings = {"8 : (1,15.3,€34)",
    })
    void processAPackage2(String testLine) throws APIException {
        String actual = Packer.processAPackage(testLine);
        assertNotNull(actual);
        assertEquals("-", actual);
    }

    @ParameterizedTest(name = "processAPackage with String {0}")
    @ValueSource(strings = {"75 : (1,85.31,€29) (2,14.55,€74) (3,3.98,€16) (4,26.24,€55) (5,63.69,€52) (6,76.25,€75) (7,60.02,€74) (8,93.18,€35) (9,89.95,€78)"
    })
    void processAPackage3(String testLine) throws APIException {
        String actual = Packer.processAPackage(testLine);
        assertNotNull(actual);
        assertEquals(",2,7", actual);
    }


}
