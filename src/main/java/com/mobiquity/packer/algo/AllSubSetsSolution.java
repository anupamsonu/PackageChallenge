package com.mobiquity.packer.algo;

import com.mobiquity.packer.model.Item;
import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;

import java.util.Arrays;

public final class AllSubSetsSolution implements ISolution {
    final static Logger log = Logger.getLogger(AllSubSetsSolution.class);

    final private Item[] totalItemsToPickFrom;
    final private double weightLimit;
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
        log.info("Constructor:totalItemsToPickFrom: "+ Arrays.deepToString(totalItemsToPickFrom) + " weightLimit :"+weightLimit);
        this.totalItemsToPickFrom = totalItemsToPickFrom;
        this.weightLimit = weightLimit;
    }

    /**
     * Compose all subsets, evaluate costs
     *
     * @return Final ouput for this solution
     */
    public String cherryPickItems() {
        log.info("cherryPickItems :Entry ");

        findSubsets(null, -1);
        log.info("cherryPickItems :Exit");
        return itemsToPick;
    }

    /**
     * Algorithm to find subsets recursively - start by adding first item,
     * evaluate costs and select items if constraints and conditions met.
     * @param itemSubset current subset
     * @param newItemIndex index of item to be added
     */
    @VisibleForTesting
    void findSubsets(Item[] itemSubset, int newItemIndex) {
        log.info("findSubsets :Entry : itemSubset : "+
                Arrays.deepToString(itemSubset) + " newItemIndex: "+newItemIndex);
        if (newItemIndex == totalItemsToPickFrom.length)
            return;
        evaluateCurrentSubset(itemSubset);
        for (int i = newItemIndex + 1; i < totalItemsToPickFrom.length; i++) {
            itemSubset = addToSubset(itemSubset, totalItemsToPickFrom[i]);
            findSubsets(itemSubset, i);
            itemSubset = eliminateItemFromSubsetEnd(itemSubset);
        }
        log.info("findSubsets :Exit");
    }

    /**
     * Evaluate cost for current subset of items and replace the previous one
     * if total weight is within limit and the package cost is higher than the
     * previous package cost
     *
     * @param itemsSubset items subset being evaluated
     */
    @VisibleForTesting
    void evaluateCurrentSubset(Item[] itemsSubset) {
        log.info("evaluateCurrentSubset :Entry :"+Arrays.deepToString(itemsSubset));
        if(itemsSubset == null) return;
        double subsetWeight = Arrays.stream(itemsSubset).reduce(0.0,
                (runningWeight, item) -> runningWeight+item.getWeight(),
                Double::sum);
        if (subsetWeight > this.weightLimit) return;
        double runningCost = Arrays.stream(itemsSubset).reduce(0.0,
                (addedCost, item) -> addedCost + item.getCost(), Double::sum);
        String itemIndexes = Arrays.stream(itemsSubset).reduce("",
                (appendedIndex, item) -> appendedIndex.concat("," +
                        (String.valueOf(item.getIndex()))), String::concat);

        compareAndSetNewValues(runningCost, itemIndexes);
        log.info("evaluateCurrentSubset :Exit");
    }

    /**
     * Check against the constraints and set the higher cost
     * and if cost is higher, replace the global items index
     * @param currentCost running Cost
     * @param index items index separted by comma
     */
    @VisibleForTesting
    void compareAndSetNewValues(double currentCost, String index) {
       log.info("compareAndSetNewValues :Entry :currentCost : "+currentCost + " : index :"+index);
       if (currentCost > this.maxCost) {
            this.itemsToPick = index;
            this.maxCost = currentCost;
        }
        log.info("compareAndSetNewValues :Exit");
    }

    /**
     * Add the item and create a new array
     * @param subset  items subset being evaluated
     * @param newItem new item to be added
     * @return Item array after addition of new item
     */
    @VisibleForTesting
    Item[] addToSubset(Item[] subset, Item newItem) {
        log.info("addToSubset :Entry :subset : "+Arrays.deepToString(subset) + " : newItem :"+newItem);
        Item[]  newSubset ;
        if (subset == null) {
            newSubset = new Item[]{newItem};
        }
        else{
            newSubset = new Item[subset.length + 1];
            System.arraycopy(subset, 0, newSubset, 0, subset.length);
            newSubset[subset.length] = newItem;
        }
        log.info("addToSubset :Exit");
        return newSubset;
    }

    /**
     * Remove last element in order to form next subset of items
     * @param subset items subset being evaluated
     * @return items subset after removing last item
     */
    @VisibleForTesting
    Item[] eliminateItemFromSubsetEnd(Item[] subset) {
        log.info("eliminateItemFromSubsetEnd :Entry :subset : "+Arrays.deepToString(subset));
        if (subset.length == 1)
            return null;
        Item[] newSubset = new Item[subset.length - 1];
        System.arraycopy(subset, 0, newSubset, 0, subset.length-1);
        log.info("eliminateItemFromSubsetEnd :Exit");
        return newSubset;
    }
}
