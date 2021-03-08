package com.mobiquity.packer;

import com.mobiquity.PackagingUtils;
import com.mobiquity.exception.APIException;
import com.mobiquity.packer.model.Item;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public final class Packer {
    final static Logger log = Logger.getLogger(Packer.class);

    private Packer() {
    }

    private static String finalPackagesStr = "";

    /**
     * This is the main packaging method that will take the file path,
     * read each line, parse them into input variables for the algorithm,
     * and return each processed package represented as a String, separated
     * by lines.
     * @param filePath Input file to process for packaging
     * @return Result : packages as a String
     * @throws APIException if validation fails for the items
     */
    public static String pack(String filePath) throws APIException {
        log.info("pack:Entry "+ filePath);

        AtomicReference<APIException> ex = new AtomicReference<>();
        boolean validPackage ;
        try (var stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            validPackage = stream.allMatch(line -> {
                String packagedAsAString ;
                try {
                    packagedAsAString = processAPackage( line);
                } catch (APIException e) {
                    ex.set(e);
                    return false;
                }
                finalPackagesStr = finalPackagesStr + packagedAsAString + "\n";
                return true;
            });
       } catch (IOException e) {
            log.error("pack:Exception "+ e);
            throw new APIException("Exception during the file read", e);
        }
        if (!validPackage)
        {
            log.error("pack:Exception thrown ", ex.get());
            throw new APIException("Encountered exception in parsing line ->", ex.get());
        }
        log.info("pack:Return : " + finalPackagesStr);
        return finalPackagesStr;
    }

    /**
     * This method takes each package as an input, parses, validates, applies
     * algorithm to select the right items, then returns the selected items as
     * a String.
     * @param line one line from each file that represents a package details
     *            - weight limit and items
     * @return a String that represents each processed package.
     * @throws APIException Exception thrown on validation
     */
    public static String processAPackage(String line) throws APIException {
        log.info("processAPackage:Entry : line" + line);
        double weightLimit;
        try {
            weightLimit = Double.parseDouble(PackagingUtils.beforeWeightLimitIndex.apply(line));
        } catch (NumberFormatException ex) {
            log.error("processAPackage:Exception "+ ex);
            throw new APIException("Weight Limit is not a Number ", ex);
        }
        PackagingUtils.validateWeightLimit(weightLimit);

        String itemsListStr = PackagingUtils.afterWeightLimitIndex.apply(line);
        Item[] items = PackagingUtils.createItemsFromLine(itemsListStr);

        PackagingUtils.validateItems(items);

        Arrays.sort(items);
        log.debug("processAPackage:After sorting "+ Arrays.deepToString(items));

        String chosenOnes = PackageCreator.selectItemsForCurrentPackage(items, weightLimit);
        log.info("processAPackage:Return : chosenOnes" + chosenOnes);
        return chosenOnes;
    }

    /**
     * Method used for testing only
     * @param args default
     * @throws Exception default
     */

    public static void main(String[] args) throws Exception {
        String finalPackagesStr = pack("C:\\Users\\ZREHMAN\\IdeaProjects\\PackageChallenge\\src\\main\\resources\\example_input");
        System.out.print(finalPackagesStr);

    }
}
