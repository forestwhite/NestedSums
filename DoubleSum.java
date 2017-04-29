/**
 * The nestedsums package collects summation methods for large nested series
 * to support the batch calculation of quantum electrodynamics model statistics,
 * such as linear entropy.
 */
package nestedsums;

import java.util.Arrays;

/**
 * Basic 1-dimensional recursive summation of an array of doubles, overloaded to
 * accommodate any generic number type if necessary
 *
 * @author forest
 */
public class DoubleSum {

    /**
     * Recursive summation of an array of doubles
     *
     * @param input Array of doubles to sum
     * @author forest
     */
    public static double calculate(double[] input) {
        if (input.length <= 1) {
            return input[0];
        } else {
            return calculate(Arrays.copyOfRange(input,
                    0,
                    input.length / 2))
                    + calculate(Arrays.copyOfRange(input,
                                    input.length / 2,
                                    input.length));
        }
    }

    /**
     * Recursive summation of an array of ints
     *
     * @param input Array of ints to sum
     * @author forest
     */
    public static int calculate(int[] input) {
        if (input.length <= 1) {
            return input[0];
        } else {
            return calculate(Arrays.copyOfRange(input,
                    0,
                    input.length / 2))
                    + calculate(Arrays.copyOfRange(input,
                                    input.length / 2,
                                    input.length));
        }
    }

    /**
     * Overloaded recursive summation of any generic number type
     *
     * @param input Array of numbers to sum
     * @author forest
     */
    public static double calculate(Number[] input) {
        if (input.length <= 1) {
            return input[0].doubleValue();
        } else {
            return calculate(Arrays.copyOfRange(input,
                    0,
                    input.length / 2))
                    + calculate(Arrays.copyOfRange(input,
                                    input.length / 2,
                                    input.length));
        }
    }

    /*
     * not correct
     *
     public static double nestedCalculate(double[] input, int levels){
     if(levels <= 1) {
     return calculate(input);
     } else {
     return nestedCalculate(input,levels/2)
     +
     nestedCalculate(input,levels/2 + levels%2);
     }
     }
     */
}
