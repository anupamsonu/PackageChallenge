package com.mobiquity.packer.algo;

import com.mobiquity.packer.model.Item;
import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;

public final class AllSubSetsSolution implements ISolution {

    private Item[] totalItemsToPickFrom;
    private double weightLimit;
    private double maxCost = 0;
    private String itemsToPick = "-";

    public double getMaxCost() {
        return maxCost;
    }

    public String getItemsToPick() {
        return itemsToPick;
    }

    /**
     * Initialise the solution with Items array and weight limit
     *
     * @param totalItemsToPickFrom Arrau of items given
     * @param weightLimit          Weight limit for the package
     */
    public AllSubSetsSolution(Item[] totalItemsToPickFrom, double weightLimit) {
        this.totalItemsToPickFrom = totalItemsToPickFrom;
        this.weightLimit = weightLimit;
    }

    /**
     * Compose all subsets, evaluate costs
     *
     * @return Final ouput for this solution
     */
    public String cherryPickItems() {
        findSubsets(null, -1);
        return itemsToPick;
    }

    /**
     * Algorithm to find subsets recursively - start by adding first item,
     * evaluate costs and select items if constraints and conditions met.
     * @param itemSubset
     * @param newItemIndex
     */
    @VisibleForTesting
    void findSubsets(Item[] itemSubset, int newItemIndex) {
        if (newItemIndex == totalItemsToPickFrom.length)
            return;
        evaluateCurrentSubset(itemSubset);
        for (int i = newItemIndex + 1; i < totalItemsToPickFrom.length; i++) {
            itemSubset = addToSubset(itemSubset, totalItemsToPickFrom[i]);
            findSubsets(itemSubset, i);
            itemSubset = eliminateItemFromSubsetEnd(itemSubset);
        }
    }

    /**
     * Evaluate cost for current subset of items and replace the previous one
     * if total weight is within limit and the package cost is higher than the
     * previous package cost
     *
     * @param itemsSubset items subset
     */
    @VisibleForTesting
    void evaluateCurrentSubset(Item[] itemsSubset) {
        if(itemsSubset == null) return;
        double subsetWeight = Arrays.stream(itemsSubset).reduce(0.0,
                (runningWeight, item) -> runningWeight+item.getWeight(),
                Double::sum);
        if (subsetWeight > this.weightLimit) return;
        double runningCost = Arrays.stream(itemsSubset).reduce(0.0,
                (addedCost, item) -> addedCost + item.getCost(), Double::sum);
        String itemIndexes = Arrays.stream(itemsSubset).reduce("",
                (appendedIndex, item) -> appendedIndex.concat("," + (String.valueOf(item.getIndex()))), String::concat);

        compareAndSetNewValues(runningCost, itemIndexes);
    }

    /**
     * Check against the constraints and set the higher cost
     * and if cost is higher, replace the global items index
     * @param currentCost running Cost
     * @param index items index separted by comma
     */
    @VisibleForTesting
    void compareAndSetNewValues(double currentCost, String index) {
       if (currentCost > this.maxCost) {
            this.itemsToPick = index;
            this.maxCost = currentCost;
        }
    }

    /**
     * Add the item and create a new array
     * @param subset
     * @param newItem
     * @return
     */
    @VisibleForTesting
    Item[] addToSubset(Item[] subset, Item newItem) {
        Item[]  newSubset = null;
        if (subset == null) {
            newSubset = new Item[]{newItem};
        }
        else{
            newSubset = new Item[subset.length + 1];
            System.arraycopy(subset, 0, newSubset, 0, subset.length);
            newSubset[subset.length] = newItem;
        }
        return newSubset;
    }

    /**
     * Remove last element in order to form next subset of items
     * @param subset
     * @return
     */
    @VisibleForTesting
    Item[] eliminateItemFromSubsetEnd(Item[] subset) {
        if (subset.length == 1)
            return null;
        Item[] newSubset = new Item[subset.length - 1];
        System.arraycopy(subset, 0, newSubset, 0, subset.length-1);
        return newSubset;
    }
}
