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

import java.util.concurrent.ForkJoinPool;

/**
 * Class defining the product of F_ab terms with one common index
 * which comprise the trace(ro_squared) terms
 */
class TrRoSquared implements Sequence {

    double[][] terms; //terms of the trace of (ro) squared
    F_ab fl; //F coefficient
    int max;

    /**
     * "raw" constructor that accepts F factors 
     */
    TrRoSquared(int max, F_ab fl) {
        this.max = max;
        this.fl = fl;
        this.terms = new double[max][max];
    }

    /**
     * Encapsulated Tr(ro_squared) coefficient constructor
     */
    public TrRoSquared(double time, EntropyParameters ep) {
        this.fl = new F_ab(time, ep);
        this.max = ep.alpha2sq * ep.alpha1sq;
        if (this.max < 16) {
            this.max = 16;
        }
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
        return fl.getTerm(new int[]{indices[0], indices[1]}) * fl.getTerm(new int[]{indices[0], indices[1]});
    }

    /**
     * Fills in Trace of Ro_Squared terms table of a given size.
     * This function grows as O(n^2) for max n terms, so a ConcurrentSeries 
     * calculates the sum in parallel if processors are available.
     */
    public double calculate() {
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                terms[i][j] = this.getTerm(new int[]{i, j});
            }
        }
        int[] indices = {0,0,max,max};
        ConcurrentSeries sum = new ConcurrentSeries(this, indices);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(sum);
        return sum.value;
    }
}
