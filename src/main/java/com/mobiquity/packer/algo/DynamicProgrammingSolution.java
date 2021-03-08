package com.mobiquity.packer.algo;

import com.google.common.annotations.VisibleForTesting;
import com.mobiquity.packer.model.Item;

import java.util.Arrays;

public final class DynamicProgrammingSolution implements ISolution {
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
        if (multiplyFactor == 1)
            this.totalItemsToPickFrom = totalItemsToPickFrom;
        else {
            this.totalItemsToPickFrom = Arrays.stream(totalItemsToPickFrom)
                    .map(source -> new Item(source.getIndex(),
                            source.getWeight() * multiplyFactor,
                            source.getCost()))
                    .toArray(Item[]::new);

        }
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
        int n = this.totalItemsToPickFrom.length;
        int wl = (int) this.weightLimit;
        int matrix[][] = new int[n + 1][wl + 1];
        createMatrix(n, wl, matrix);
        String itemsToPick = backtrackToSelectItems(matrix);
        return itemsToPick;
    }

    /**
     * create the matrix for dynamic programming -
     * X axis - weight limit incrementally set
     * Y axis - number of items sorted by weight increasing, then cost decreasing
     * for noOfItems = 0 and weightLimit = 0 , set cost = 0 in the matrix
     * as there is no item to be selected when either is 0
     *
     * @param noOfItems
     * @param weightLimit
     * @param matrix
     */
    @VisibleForTesting
    void createMatrix(int noOfItems, int weightLimit, int[][] matrix) {
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
    }

    /**
     * Logic is to traverse back from the last cell in the matrix .
     * Check if that was calculated when the last item was selected.
     *
     * @param matrix
     * @return
     */
    @VisibleForTesting
    String backtrackToSelectItems(int[][] matrix) {

        String itemsToPick = "-";
        int itemCount = matrix.length - 1;
        int WL = (int) weightLimit;
        String indexes = "";
        while (itemCount != 0) {
            // if its not same it means current item was piked
            if (matrix[itemCount][WL] != matrix[itemCount - 1][WL]) {
                indexes = indexes + "," + totalItemsToPickFrom[itemCount - 1].getIndex();
                // rest of the weight is total weight - current item weight
                WL = WL - (int) totalItemsToPickFrom[itemCount - 1].getWeight();
            }
            itemCount--;
        }
        if (!"".equals(indexes)) {
            itemsToPick = indexes;
            if (itemsToPick.startsWith(","))
                itemsToPick = itemsToPick.substring(1);
        }

        return itemsToPick;
    }


}
