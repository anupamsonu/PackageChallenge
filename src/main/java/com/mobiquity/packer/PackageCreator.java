package com.mobiquity.packer;

import com.mobiquity.PackagingUtils;
import com.mobiquity.packer.algo.AllSubSetsSolution;
import com.mobiquity.packer.algo.DynamicProgammingSolution;
import com.mobiquity.packer.algo.ISolution;
import com.mobiquity.packer.model.Item;

import java.util.Arrays;

public class PackageCreator {



    public PackageCreator() {
    }

    /**
     * 1. Decide the MULTIPLY_FACTOR from the highest scale
     * 2. Check the Complexity of Time. DP will have O(W x n). If there are decimals, it will become
     *    O(W x n X MULTIPLY_FACTOR)
     *    For subset algorithm it will be O(2 power n)
     * 3. Decide the appropriate algorithm and call.
     * @param items array Of Items
     * @param weightLimit
     * @return
     */
    public static String selectTheItems(Item[] items,double weightLimit){

        int multiplyFactor = evaluateMultiplyFactor(items,weightLimit);

        boolean useDPAlgorithm = useDPALgorithm(weightLimit, items.length, multiplyFactor);

        ISolution solution;
        System.out.println(" useDPAlgorithm "+useDPAlgorithm);
        if(useDPAlgorithm)
            solution = new DynamicProgammingSolution(items, weightLimit, multiplyFactor);
        else
            solution = new AllSubSetsSolution(items , weightLimit);
        // use backtracking to find all the subsets.
        String solutionForRow = solution.cherryPickItems();
        return solutionForRow;
    }

    private static boolean useDPALgorithm(double weightLimit, int length, int multiplyFactor) {
        double timeComplexityForDynamic = weightLimit * length * multiplyFactor;
        double timeComplexityForSubsets = Math.pow(2,length);
        if ( timeComplexityForDynamic >= timeComplexityForSubsets)
            return false;
        else
            return true;
    }

    /**
     * This method will determine the MULTIPLY FACTORY by checking the highest scale
     * from the array of items and the weight limit
     * @param items
     * @param weightLimit
     * @return MULTIPLY_FACTOR
     */
    private static int evaluateMultiplyFactor(Item[] items, double weightLimit){
        int initialScale = PackagingUtils.getScale(weightLimit);
        int biggestScaleItems = Arrays.stream(items).map(item -> item.getScale())
                                      .max(Integer::compareTo)
                                      .get();
        return (int)Math.pow(10,Math.max(initialScale, biggestScaleItems));
        }
}