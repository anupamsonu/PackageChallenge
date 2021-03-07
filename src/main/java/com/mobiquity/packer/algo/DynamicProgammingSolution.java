package com.mobiquity.packer.algo;

import com.mobiquity.packer.model.Item;

public final class DynamicProgammingSolution implements ISolution {
    private Item[] totalItemsToPickFrom;
    private double weightLimit;
    private String itemsToPick = "-";
    private int multiplyFactor;


    public DynamicProgammingSolution(Item[] totalItemsToPickFrom, double weightLimit, int multiplyFactor) {
        this.multiplyFactor = multiplyFactor;
        this.weightLimit = weightLimit * multiplyFactor;
        setTotalItemsToPickFrom(totalItemsToPickFrom);
    }

    private void setTotalItemsToPickFrom(Item[] totalItemsToPickFrom) {
        if (multiplyFactor == 1)
            this.totalItemsToPickFrom = totalItemsToPickFrom;
        else {
            this.totalItemsToPickFrom = new Item[totalItemsToPickFrom.length];
            for (int i = 0; i < totalItemsToPickFrom.length; i++) {
                totalItemsToPickFrom[i] = totalItemsToPickFrom[i];
                totalItemsToPickFrom[i] = new Item(totalItemsToPickFrom[i].getIndex(),
                        totalItemsToPickFrom[i].getWeight() * multiplyFactor,
                        totalItemsToPickFrom[i].getCost()
                );
            }
        }
    }

    @Override
    public String cherryPickItems() {
        int n = this.totalItemsToPickFrom.length;
        int WL = (int) this.weightLimit;
        int matrix[][] = new int[n + 1][WL + 1];
        for (int i = 0; i <= n; i++) {
            for (int w = 0; w <= WL; w++) {
                if (i == 0 || w == 0)
                    continue; // for indexes i and 0 let them be all set to zeros. (No cost for picking a zero
                // items or no cost when capacity is zero)
                matrix[i][w] = matrix[i - 1][w];
                int thisRowItemWeight = (int) totalItemsToPickFrom[i - 1].getWeight();
                int thisRowItemValue = (int) totalItemsToPickFrom[i - 1].getCost();
                if ((w >= thisRowItemWeight) && (matrix[i][w] < matrix[i - 1][w - thisRowItemWeight] + thisRowItemValue))
                    matrix[i][w] = matrix[i - 1][w - thisRowItemWeight] + thisRowItemValue;
            }
        }
        selectedItems(matrix);
        return getItemsToPick();
    }

    private void selectedItems(int[][] matrix) {
        // go up from the last cell cos thats the max profit. Check if that was calculated when the last
        // item was selected.
        int itemCount = matrix.length - 1;
        int WL = (int) weightLimit;
        String indexes = "";
        while (itemCount != 0) {
            if (matrix[itemCount][WL] != matrix[itemCount - 1][WL]) { // if its not same it means current item was piked
                indexes = indexes + "," + totalItemsToPickFrom[itemCount - 1].getIndex();
                WL = WL - (int) totalItemsToPickFrom[itemCount - 1].getWeight(); // rest of the weight is total weight - current item weight
            }
            itemCount--;
        }
        if (!"".equals(indexes))
            itemsToPick = indexes;
    }

    public String getItemsToPick() {
        if (itemsToPick.startsWith(","))
            return itemsToPick.substring(1);
        return itemsToPick;
    }

}
