package com.mobiquity;

import com.mobiquity.exception.APIException;
import com.mobiquity.packer.model.Item;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Pattern;

public class PackagingUtils {

    /**
     * Parses a line from input and returns an array of String
     * each string representing each item
     *
     * @param itemArrayStr eg." (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9)
     *                     (6,46.34,€48)"
     * @return String[] {"1,53.38,€45", "2,88.62,€98", "3,78.48,€3", "4,72.30,€76", "5,30.18,€9",
     * "6,46.34,€48" }
     */
    public static String[] splitItemsListStrToItemsArray(String itemArrayStr) {
        Pattern pattern = Pattern.compile(" ");
        String[] str_array = Arrays.stream(pattern.split(itemArrayStr.trim()))
                .map(s -> replaceBrackets(s))
                .toArray(String[]::new);
        return str_array;
    }

    /**
     * replace the brackets for each parsed Item String
     *
     * @param str (1,53.38,€45)
     * @return 1, 53.38, €45
     */
    public static String replaceBrackets(String str) {
        return str.replaceAll("\\)|\\(", "");
    }

    /**
     * Assumption is there is no need of conversion on the cost of the items
     * for each package as they are uniform, simply strip the symbol to
     * process further
     *
     * @param str €45
     * @return 45
     */

    public static String stripSymbolFromCost(String str) {
        //String replacedStr = str.replaceAll("€", "");
        //return str.replaceAll("€", "");
        return str.substring(1);
    }

    /**
     * This method calls 2 other methods to convert a line to an array of items.
     *
     * @param line eg." (1,53.38,€45) (2,88.62,€98) (3,78.48,€3)
     *             (4,72.30,€76) (5,30.18,€9)  (6,46.34,€48)"
     * @return Item[] first Item : index = 1, Weight = 53.38, cost = 45
     */
    public static Item[] createItemsFromLine(String line) {
        String itemsStr = afterWeightLimitIndex.apply(line);
        String[] itemStrArray = PackagingUtils.splitItemsListStrToItemsArray(itemsStr);
        Item[] items = PackagingUtils.convertStringArrayToItemArray(itemStrArray);
        return items;
    }

    /**
     * This method will validate the item constraints
     *
     * @param items
     * @return validation result
     * @throws APIException
     */
    public static boolean validateItems(Item[] items) throws APIException {
        AtomicReference<APIException> ex = new AtomicReference<>();
        boolean validated = Arrays.stream(items).allMatch(elem -> {
                    try {
                        elem.validateItem();
                    } catch (APIException e) {
                        ex.set(e);
                        return false;
                    }
                    return true;
                }
        );
        if (!validated) throw new APIException("Item validation failed", ex.get());
        return true;
    }

    /**
     * This method will validate weight constraints
     *
     * @param weightLimit
     * @return validation result
     * @throws APIException
     */
    public static boolean validateWeightLimit(double weightLimit) throws APIException {
        if (weightLimit > PackagingConstants.PACKAGE_WEIGHT_LIMIT)
            throw new APIException("weight limit for row number: should be less than 100");
        return true;
    }

    /**
     * This method parses items from a String representing items
     * and returns them as an array
     *
     * @param itemArrayStr String[]{"1,53.38,€45", "2,88.62,€98", "3,78.48,€3",
     *                     "4,72.30,€76", "5,30.18,€9",  "6,46.34,€48" }
     * @return Item[] first Item : index = 1, Weight = 53.38, cost = 45
     */
    public static Item[] convertStringArrayToItemArray(String[] itemArrayStr) {
        Pattern pattern = Pattern.compile(",");
        Item[] item_array = Arrays.stream(itemArrayStr).sequential()
                .map(s -> pattern.split(s)).map(array ->
                        new Item(Integer.parseInt(array[0]),
                                Double.parseDouble(array[1]),
                                Double.parseDouble(PackagingUtils.stripSymbolFromCost(array[2])))

                ).toArray(Item[]::new);

        return item_array;
    }

    public static int getScale(double doubleValue)
    {
        int scale = 0;
        int doubleAsInt = (int) doubleValue;
        if( doubleAsInt < doubleValue)
            scale = new BigDecimal(doubleValue).scale();
        return scale;
    }
    /**
     * Function will extract the items part of the line representing
     * each package
     */
    public static Function<String, String> afterWeightLimitIndex = (line) -> line.substring(line.indexOf(":") + 1);
    /**
     * Function will extract the weight limit part of the line representing
     * each package
     */
    public static Function<String, String> beforeWeightLimitIndex = (line) -> line.substring(0, line.indexOf(":"));
}
