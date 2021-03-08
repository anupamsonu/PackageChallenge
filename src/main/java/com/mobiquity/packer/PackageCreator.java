package com.mobiquity.packer;

import com.mobiquity.PackagingUtils;
import com.mobiquity.packer.algo.AllSubSetsSolution;
import com.mobiquity.packer.algo.DynamicProgrammingSolution;
import com.mobiquity.packer.algo.ISolution;
import com.mobiquity.packer.model.Item;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;

public class PackageCreator {
    final static Logger log = Logger.getLogger(PackageCreator.class);

    public PackageCreator() {
    }

    /**
     * 1. Decide the MULTIPLY_FACTOR from the highest scale
     * 2. Check the Complexity of Time. DP will have O(W x n). If there are decimals, it will become
     *    O(W x n X MULTIPLY_FACTOR)
     *    For subset algorithm it will be O(2 power n)
     * 3. Decide the appropriate algorithm and call.
     * @param items array Of Items
     * @param weightLimit  weight limit for package
     * @return solution String for current package
     */
    public static String selectItemsForCurrentPackage(Item[] items, double weightLimit){
        int multiplyFactor = evaluateMultiplyFactor(items,weightLimit);

        boolean useDPAlgorithm = useDPAlgorithm(weightLimit, items.length, multiplyFactor);

        ISolution solution;
        if(useDPAlgorithm)
            solution = new DynamicProgrammingSolution(items, weightLimit, multiplyFactor);
        else
            solution = new AllSubSetsSolution(items , weightLimit);
        return solution.cherryPickItems();
    }

    /**
     * Decide which alogorithm to use on the basis of time complexity
     * @param weightLimit weight limit for package
     * @param length number of items
     * @param multiplyFactor multiply factor for weights in decimals
     * @return return the decision to use DP algorithm
     */
    public static boolean useDPAlgorithm(double weightLimit, int length, int multiplyFactor) {
        double timeComplexityForDynamic = weightLimit * length * multiplyFactor;
        double timeComplexityForSubsets = Math.pow(2,length);
        return !(timeComplexityForDynamic >= timeComplexityForSubsets);
    }

    /**
     * This method will determine the MULTIPLY FACTORY by checking the highest scale
     * from the array of items and the weight limit
     * @param items items in one package
     * @param weightLimit weight limit for package
     * @return MULTIPLY_FACTOR multiply factor for weights in decimals
     */
    public static int evaluateMultiplyFactor(Item[] items, double weightLimit){
        int initialScale = PackagingUtils.getScale(weightLimit);
        int biggestScaleItems = 0;
        Optional<Integer> biggestScaleItemsOpt = Arrays.stream(items).map(Item::getScale)
                                      .max(Integer::compareTo);
        if (biggestScaleItemsOpt.isPresent())
            biggestScaleItems = biggestScaleItemsOpt.get();
        return (int)Math.pow(10,Math.max(initialScale, biggestScaleItems));
        }
}