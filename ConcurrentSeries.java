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

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

/**
 * Defines a generic series, which can also be a series term, supporting nested
 * summation.
 *
 * @author forest
 */
public class ConcurrentSeries extends RecursiveAction implements Sequence {

    private final Sequence terms; //the definition for a term of the series
    private final int dimensions; //number of indices
    private final int[] indices; //Two sets of indices indicating the first and  
                                 //last cells of a series of terms to sum
    double value; //Result value for the series

    /**
     * Constructor for nested series calculation
     *
     * @param terms the series term definition, to generate series member values
     * @param indices   outer indices
     */
    public ConcurrentSeries(Sequence terms, int[] indices) {
        this.terms = terms;
        this.dimensions = indices.length >> 1;
        this.indices = indices;
    }

    @Override
    protected void compute() {
        for(int i = 0; i < dimensions ; i++){
            // If this index level is the same for start and finish indices,
            // move down to the next inner index
            if (indices[i] == indices[dimensions+i])
                continue;
            // split on innermost non-matching indices
            int[] indices1 = indices;
            int[] indices2 = indices;
            indices1[dimensions + i] = indices[(dimensions +i) >> 1];
            indices2[i] = indices[(dimensions +i) >> 1+1];
            invokeAll(new ConcurrentSeries(terms, indices1),
                      new ConcurrentSeries(terms, indices2));
        }
        value += getTerm(Arrays.copyOfRange(indices,0,dimensions)).doubleValue();
    }
    
    /**
     * @param indices   Index numbers of the term in the series. The number of
     *                  indices indicates the level of summation nesting.
     * @return the value of the series term
     */
    @Override
    public Number getTerm(int[] indices) throws IndexOutOfBoundsException {
        //check that number of indices is less than depth
        if (indices.length > dimensions << 2) {
            throw new IndexOutOfBoundsException();
        }
        return terms.getTerm(indices);
    }
}
