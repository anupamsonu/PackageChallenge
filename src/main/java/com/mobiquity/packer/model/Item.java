package com.mobiquity.packer.model;

import com.mobiquity.PackagingUtils;
import com.mobiquity.exception.APIException;

import java.math.BigDecimal;
import java.util.Objects;

import static com.mobiquity.PackagingConstants.*;

public class Item implements Comparable<Item>{
    private double weight;
    private double cost;
    private int index;

    public int getScale() {
        return scale;
    }

    private int scale;
    public Item(int index, double weight, double cost ){
        this.index = index;
        this.cost = cost;
        this.weight = weight;
        this.setScale(weight);
    }

    /**
     * Set scale for the weight of the Item to be used
     * during calculation of multiplication factor for
     * dynamic programming
     * @param weight
     */
    private void setScale(double weight) {
        scale = PackagingUtils.getScale(weight);
    }

    public double getWeight() {
        return weight;
    }
    public double getCost() {
        return cost;
    }
    public int getIndex() {
        return index;
    }

    /**
     * This method checks another item with the current and evaluates as below:-
     * 1. if weight and costs are same, they are equal items
     * 2. if weight of current item is greater, then current item is greater
     * 3. if weights are same and current item has lower cost item then it is greater
     * @param compareIem Item to compare with
     * @return int to indicate the comparison result
     */
    public int compareTo(Item compareIem) {
        if(this.weight==compareIem.weight && this.cost ==compareIem.cost){
            return 0;
        }else if(this.weight>compareIem.weight ||
                (this.weight==compareIem.weight && this.cost < compareIem.cost)){
            return 1;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Item{" +
                "weight=" + weight +
                ", cost=" + cost +
                ", index=" + index +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Double.compare(item.weight, weight) == 0 &&
                Double.compare(item.cost, cost) == 0 &&
                index == item.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight, cost, index);
    }

    /**
     * Validate each item
     * @return validation result
     * @throws APIException
     */
    public boolean validateItem() throws APIException
    {
        if(getIndex() > ITEM_INDEX_LIMIT)
            throw new APIException("Items index exceeds 15 (there can only be 15 items))");
        if(getCost() > ITEM_COST_LIMIT)
            throw new APIException("Item cost cannot exceed 100");
        if(getWeight() > ITEM_WEIGHT_LIMIT)
            throw new APIException("Item weight cannot exceed 100");
        return true;
    }
}