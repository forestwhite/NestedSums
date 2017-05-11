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
 * Defines a generic series, which can also be a series term, supporting nested
 * summation.
 *
 * @author forest
 */
public class Series implements Sequence {

    private Sequence seriesterm; //the definition for a term of the series
    private int max; //max number of terms to sum
    private int depth; //number of inner sums nested within this sum
    private int[] indices; //indices to track nested terms under evaluation, in 
    // reverse order, meaning index of outermost sum is 
    // last and index of innermost sum is first
    double value; //Result value for the series

    /**
     * Constructor for 1D series, for which a definition of terms and the number
     * of terms are required
     *
     * @param terms the series term definition, to generate series member values
     * @param max the number of terms to sum
     */
    public Series(Sequence terms, int max) {
        this(terms, max, 1);
    }

    /**
     * Constructor for series calculation with one or more outer series
     *
     * @param terms the series term definition, to generate series member values
     * @param max the maximum index permitted at the current depth
     * @param depth
     * @param outerindices
     */
    public Series(Sequence terms, int max, int depth, int[] outerindices) {
        this.seriesterm = terms;
        this.max = max;
        this.depth = depth;
        this.indices = outerindices;
    }

    /**
     * Constructor for series calculation with one or more nested inner series
     *
     * @param terms the series term definition, to generate series member values
     * @param max the maximum index permitted at the current depth
     * @param depth
     */
    public Series(Sequence terms, int max, int depth) {
        this.seriesterm = terms;
        this.max = max;
        this.depth = depth;
        this.indices = new int[depth];
    }

    /**
     * @param indices Index numbers of the term in the series. The number of
     * indeces indicates the level of summation nesting.
     * @return the value of the series term
     */
    @Override
    public double getTerm(int[] indices) throws IndexOutOfBoundsException {
        //check that number of indeces is less than depth
        if (indices.length > depth) {
            throw new IndexOutOfBoundsException();
        }
        return seriesterm.getTerm(indices);
    }

    /**
     * Calculates the series total. The efficiency of this solution is
     * O(n^depth) TODO: improve upon efficiency for any depth > 1 with a
     * recursive divide-and-conquer approach
     *
     * @return the value of the series
     */
    public double calculate() {
        for (int i = 0; i < max; i++) {
            //set index for the next term at current depth
            this.indices[this.depth - 1] = i;
            //if this is an outer series, calculate the inner series value
            //for all inner values and add to the current value
            if (depth != 1) {
                Series inner = new Series(seriesterm, max, depth - 1, indices);
                value += inner.calculate();
            } // when the innermost sum is reached, sum the terms from 1 to max
            else {
                value += seriesterm.getTerm(indices);
            }
        }
        return value;
    }
}
