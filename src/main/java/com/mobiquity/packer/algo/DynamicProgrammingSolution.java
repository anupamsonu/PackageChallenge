package com.mobiquity.packer.algo;

import com.google.common.annotations.VisibleForTesting;
import com.mobiquity.packer.model.Item;
import org.apache.log4j.Logger;

import java.util.Arrays;

public final class DynamicProgrammingSolution implements ISolution {
    final static Logger log = Logger.getLogger(DynamicProgrammingSolution.class);

    private Item[] totalItemsToPickFrom;
    private final double weightLimit;

    public Item[] getTotalItemsToPickFrom() {
        return totalItemsToPickFrom;
    }

    public double getWeightLimit() {
        return weightLimit;
    }

    public int getMultiplyFactor() {
        return multiplyFactor;
    }

    private final int multiplyFactor;

    /**
     * Initialize the Solution class for DP
     *
     * @param totalItemsToPickFrom Items array to select from
     * @param weightLimit Weight Limit for each package
     * @param multiplyFactor multiplication factor for weight
     */
    public DynamicProgrammingSolution(Item[] totalItemsToPickFrom, double weightLimit, int multiplyFactor) {
        log.info("Constructor:weightLimit: "+ weightLimit + " multiplyFactor : "+multiplyFactor + " totalItemsToPickFrom : "+Arrays.deepToString(totalItemsToPickFrom));
        this.multiplyFactor = multiplyFactor;
        this.weightLimit = weightLimit * multiplyFactor;
        setTotalItemsToPickFrom(totalItemsToPickFrom);
    }

    /**
     * The item matrix might vary from the original depending
     * on the passed multiplyFactor in the constructor
     *
     * @param totalItemsToPickFrom Items array to pick from
     */
    @VisibleForTesting
     void setTotalItemsToPickFrom(Item[] totalItemsToPickFrom) {
        log.info("setTotalItemsToPickFrom:Entry:totalItemsToPickFrom : "
                + Arrays.deepToString(totalItemsToPickFrom));

        if (multiplyFactor == 1)
            this.totalItemsToPickFrom = totalItemsToPickFrom;
        else {
            this.totalItemsToPickFrom = Arrays.stream(totalItemsToPickFrom)
                    .map(source -> new Item(source.getIndex(),
                            source.getWeight() * multiplyFactor,
                            source.getCost()))
                    .toArray(Item[]::new);

        }
        log.info("setTotalItemsToPickFrom:Exit ");

    }

    /**
     * 1. Create matrix
     * 2. backtrack to pick the items selected in the package
     * 3. remove beginning ","
     *
     * @return String containing index position of items in each package
     */
    @Override
    public String cherryPickItems() {
        log.info("cherryPickItems:Entry");

        int n = this.totalItemsToPickFrom.length;
        int wl = (int) this.weightLimit;
        int[][] matrix = new int[n + 1][wl + 1];
        createMatrix(n, wl, matrix);
        String itemsToPick = backtrackToSelectItems(matrix);
        log.info("cherryPickItems:Exit:itemsToPick="+itemsToPick);
        return itemsToPick;
    }

    /**
     * create the matrix for dynamic programming -
     * X axis - weight limit incrementally set
     * Y axis - number of items sorted by weight increasing, then cost decreasing
     * for noOfItems = 0 and weightLimit = 0 , set cost = 0 in the matrix
     * as there is no item to be selected when either is 0
     *
     * @param noOfItems items to be picked in the package
     * @param weightLimit weight limit for the package
     * @param matrix matrix to fill with weights as per algorithm
     */
    @VisibleForTesting
    void createMatrix(int noOfItems, int weightLimit, int[][] matrix) {
        log.info("createMatrix:Entry:noOfItems : "+noOfItems +" weightLimit: "+ weightLimit );

        for (int y = 0; y <= noOfItems; y++) {
            for (int x = 0; x <= weightLimit; x++) {
                if (y == 0 || x == 0)
                    continue;
                matrix[y][x] = matrix[y - 1][x];
                int prevItemWeight = (int) totalItemsToPickFrom[y - 1].getWeight();
                int prevItemValue = (int) totalItemsToPickFrom[y - 1].getCost();
                if ((x >= prevItemWeight)
                        &&
                        (matrix[y][x] < matrix[y - 1][x - prevItemWeight] + prevItemValue))
                    matrix[y][x] = matrix[y - 1][x - prevItemWeight] + prevItemValue;
            }
        }
        log.info("createMatrix:Exit");
    }

    /**
     * Logic is to traverse back from the last cell in the matrix .
     * Check if that was calculated when the last item was selected.
     *
     * @param matrix backtrack on matrix filled with weights as per algorithm
     * @return String indicating the final selected items
     */
    @VisibleForTesting
    String backtrackToSelectItems(int[][] matrix) {
        log.info("backtrackToSelectItems:Entry");
        String itemsToPick = "-";
        int itemCount = matrix.length - 1;
        int WL = (int) weightLimit;
        StringBuilder indexes = new StringBuilder();
        while (itemCount != 0) {
            // if its not same it means current item was picked
            if (matrix[itemCount][WL] != matrix[itemCount - 1][WL]) {
                indexes.append(",").append(totalItemsToPickFrom[itemCount - 1].getIndex());
                // rest of the weight is total weight - current item weight
                WL = WL - (int) totalItemsToPickFrom[itemCount - 1].getWeight();
            }
            itemCount--;
        }
        if (!"".equals(indexes.toString())) {
            itemsToPick = indexes.toString();
            if (itemsToPick.startsWith(","))
                itemsToPick = itemsToPick.substring(1);
        }
        log.info("backtrackToSelectItems:Exit : itemsToPick = "+itemsToPick);
        return itemsToPick;
    }


}
