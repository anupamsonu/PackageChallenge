package com.mobiquity.packer;

import com.mobiquity.PackagingUtils;
import com.mobiquity.exception.APIException;
import com.mobiquity.packer.model.Item;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public final class Packer {

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
     * @throws APIException
     */
    public static String pack(String filePath) throws APIException {
        AtomicReference<APIException> ex = new AtomicReference<>();
        boolean validPackage = false;
        try (var stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            validPackage = stream.allMatch(line -> {
                String packagedAsAString = null;
                try {
                    packagedAsAString = processAPackage((String) line);
                } catch (APIException e) {
                    ex.set(e);
                    return false;
                }
                finalPackagesStr = finalPackagesStr + packagedAsAString + "\n";
                return true;
            });
       } catch (IOException e) {
            throw new APIException("Exception during the file read", e);
        }
        if (!validPackage) throw new APIException("Encountered exception in parsing line ->", ex.get());
        return finalPackagesStr;
    }

    /**
     * This method takes each package as an input, parses, validates, applies
     * algorithm to select the right items, then returns the selected items as
     * a String.
     * @param line one line from each file that represents a package details
     *            - weight limit and items
     * @return a String that represents each processed package.
     * @throws APIException
     */
    public static String processAPackage(String line) throws APIException {
        double weightLimit;
        try {
            weightLimit = Double.parseDouble(PackagingUtils.beforeWeightLimitIndex.apply(line));
        } catch (NumberFormatException ex) {
            throw new APIException("Weight Limit is not a Number ", ex);
        }
        PackagingUtils.validateWeightLimit(weightLimit);

        String itemsListStr = PackagingUtils.afterWeightLimitIndex.apply(line);
        Item[] items = PackagingUtils.createItemsFromLine(itemsListStr);

        PackagingUtils.validateItems(items);

        Arrays.sort(items);

        String chosenOnes = new PackageCreator().selectItemsForCurrentPackage(items, weightLimit);
        return chosenOnes;
    }

    /**
     * Method used for testing only
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {
        String finalPackagesStr = pack("C:\\Users\\ZREHMAN\\IdeaProjects\\PackageChallenge\\src\\main\\resources\\example_input");
        System.out.print(finalPackagesStr);

    }
}
