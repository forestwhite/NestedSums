/**
 * The nestedsums package collects summation methods for large nested series
 * to support the batch calculation of quantum electrodynamics model statistics,
 * such as linear entropy.
 * 
 * In general, quantum probabilities and quasi-probability values are near 0 and 
 * 1, so double precision arithmetic is favored for speed with sufficient 
 * accuracy - 4 or 5 significant figures in the worst case. Some calculations
 * require very large number calculations in their constituent parts, which 
 * represents a performance bottleneck if applied arbitrarily.  
 */
package nestedsums;

/**
 * B_0 is the sums of all the B_ab coefficients squared for a given time By
 * normalization, B_0 should always be 1 and provides a check for the accuracy
 * of the B_ab coefficient calculations
 *
 * @author forest
 */
public class B_0 implements Sequence {

    double time; //time at which the state-reductive measurement is made
    int max; //the maximum number of terms in the series to calculate any value
    B_ab bt; //B_ab sequence for the time (see above) examined
    double[][] terms; //the individual series terms, not the final value
    Double result; //the final value of the series

    /*
     * "raw" B_0 constructor 
     */
    public B_0(double time, int max, B_ab bt) {
        this.time = time;
        this.max = max;
        this.bt = bt;
    }

    /**
     * Encapsulated B_0 constructor
     */
    public B_0(double time, EntropyParameters ep) {
        this.time = time;
        this.max = ep.alpha2sq * ep.alpha1sq;
        if (this.max < 16) {
            this.max = 16;
        }
        this.bt = new B_ab(time, ep);
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
        return Math.pow(Math.abs(bt.getTerm(indices)), 2);
    }

    /**
     * Calculates the B_0 factor for a preset time and preset maximum 
     * number of sum terms. 
     * @return the value of B_0 for preset time
     */
    public double calculate() {
        if (result == null) {
            Series sum = new Series(this, max, 2);
            return result = Math.sqrt(sum.calculate());
        } else {
            return result;
        }
    }
}
