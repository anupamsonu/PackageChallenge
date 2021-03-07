package com.mobiquity.packer.algo;

import com.mobiquity.packer.model.Item;

public class AllSubSetsSolution implements ISolution{
    private Item[] totalItemsToPickFrom;
    private double weightLimit;
    private double maxValue;
    private String itemsToPick;

    public AllSubSetsSolution(Item[] totalItemsToPickFrom, double weightLimit){
        this.totalItemsToPickFrom = totalItemsToPickFrom;
        this.weightLimit = weightLimit;
        this.itemsToPick = "-";
        this.maxValue = 0;
    }

    public void process()
    {
        findSubsets(null,-1);

    }
    public String cherryPickItems(){
        findSubsets(null,-1);
        return  getItemsToPick();
    }
    private void findSubsets(Item[] prefix, int newItemIndex){
        if(newItemIndex==totalItemsToPickFrom.length)
            return;
        evaluateSubset(prefix);
        for(int i=newItemIndex+1;i<totalItemsToPickFrom.length;i++){
            prefix = addToPrefix(prefix,totalItemsToPickFrom[i]);
            findSubsets(prefix,i);      // new element added to prefix
            //remove last element from prefix to make different subset
            prefix = curtailPrefix(prefix);
        }
    }

    private void evaluateSubset(Item[] prefix){
        double subsetWeight = 0;
        String indexes = "";
        double maxValue = 0;
        if(prefix!=null){
            for(int i=0;i<prefix.length;i++){
                subsetWeight=subsetWeight+prefix[i].getWeight();
                if(subsetWeight>this.weightLimit)
                    return;
                indexes=indexes+","+prefix[i].getIndex();
                maxValue = maxValue + prefix[i].getCost();
            }
            if(maxValue>this.maxValue) {
                this.itemsToPick = indexes;
                this.maxValue = maxValue;
            }
        }
    }

    private Item [] addToPrefix(Item[] prefix,Item newItem){
        Item[] newPrefix = null;
        if(prefix==null){
            newPrefix = new Item []{newItem};
        }
        if(prefix!=null){
            newPrefix = new Item [prefix.length+1];
            for(int i=0;i<prefix.length;i++){
                newPrefix[i]=prefix[i];
            }
            newPrefix[prefix.length]=newItem;
        }
        return newPrefix;
    }

    private Item[] curtailPrefix(Item [] prefix){
        if(prefix.length==1)
            return null;
        Item[] newPrefix = new Item[prefix.length-1];
        for(int i=0;i<prefix.length-1;i++){
            newPrefix[i]=prefix[i];
        }
        return newPrefix;
    }

    public String getItemsToPick() {
        if(itemsToPick.startsWith(","))
            return itemsToPick.substring(1);
        return itemsToPick;
    }
}
