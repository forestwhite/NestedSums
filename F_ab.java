/**
 * The nestedsums package collects summation methods for large nested series
 * to support the batch calculation of quantum electrodynamics model statistics,
 * such as linear entropy.
 * 
 * In general, quantum probabilities and quasi-probability values are near 0 and 
 * 1, so double precision arithmetic is favored for speed with sufficient 
 * accuracy - 4 or 5 significant figures in the worst case. Some calculations
 * require very large number calculations in their constituent parts, which 
<<<<<<< HEAD
 * represents a performance bottleneck if applied arbitrarily.  
=======
 * represent a performance bottleneck if applied arbitrarily.  
>>>>>>> origin/master
 */
package nestedsums;

/**
 * F_ab represents the terms in the reduced density operator (ro-a|b) series for
 * a specific field a or b. Each term is the product of B_ab coefficients which
 * requires outer indices to properly evaluate, as they are to be summed again
 * to evaluate ro-a|b proper. That is, F_ab is never evaluated outside of the
 * context of the reduced density operator because it is an arbitrary
 * distinction to enhance the efficiency of calculations.
 *
 * @author forest
 */
public class F_ab implements Sequence {

    double time; //time at which the state-reductive measurement is made
    int max; //the maximum number of terms in the series to calculate any value
    B_ab bt; //B_ab sequence for the time (see above) examined
    B_ab bt_cnj; //B_ab conjugate sequence for the time exmined
    double[][] terms; //the individual series terms, not the final value

    /**
     * "raw" F_ab constructor 
     */
    public F_ab(double time, int max, B_ab bt, B_ab bt_cnj) {
        this.time = time; //scaled time of the state-reductive measurement
        this.max = max; //the number of summation terms to use
        this.bt = bt; //B coefficient of the system
        this.bt_cnj = bt_cnj; //conjugate B coefficient
    }

    /**
     * Encapsulated F coefficient constructor
     * @param time
     * @param ep
     */
    public F_ab(double time, EntropyParameters ep) {
        this.time = time;
        this.max = ep.alpha2sq * ep.alpha1sq;
        if (this.max < 16) {
            this.max = 16;
        }
        this.bt = new B_ab(time, ep);
        this.bt_cnj = this.bt;
    }

    /**
     * Optimized with memoization
     */
    @Override
    public double getTerm(int[] indices) throws IndexOutOfBoundsException {
        if (indices.length > 2) {
            throw new IndexOutOfBoundsException();
        }
        //if the term is already calculated, fetch stored value
        if (terms != null && terms[indices[0]][indices[1]] != 0.0) {
            if (indices[1] < this.terms[0].length && indices[0] < this.terms.length) {
                return terms[indices[0]][indices[1]];
            }
        }
        //if the term is not already calculated, calculate it
        Series sum = new Series(new Bsq(indices), max, 1);
        return sum.calculate();
    }

    /**
     * Inner class defining the product of B_ab terms with one common index, l, 
     * and separate indices, n and m, upon which to evaluate each B_ab 
     * coefficient prior to multiplying.
     */
    public class Bsq implements Sequence {

        int[] outerindices;

        Bsq(int[] outerindices) {
            this.outerindices = outerindices;
        }

        /**
         * Optimized with memoization
         */
        @Override
        public double getTerm(int[] indices) throws IndexOutOfBoundsException {
            return bt.getTerm(new int[]{outerindices[0], indices[0]})
                    * bt_cnj.getTerm(new int[]{outerindices[1], indices[0]});
        }
    }

    /**
     * Fills in F_ab terms table of a given size.
     * This has linear efficiency for the number of terms to calculate.
     * @param indices Coordinates for the final  cells of the 
     *                table/matrix that define the range of terms to calculate 
     */
    public void calculate(int[] indices) {
        terms = new double[indices[0]][indices[1]];
        for (int i = 0; i < indices[0]; i++) {
            for (int j = 0; j < indices[1]; j++) {
                terms[i][j] = getTerm(new int[]{i, j});
            }
        }
    }
}
