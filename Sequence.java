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
 * Generates generic sequence terms, as defined by an index position.
 *
 * @author forest
 */
public interface Sequence{

    /**
     * @param indices Index numbers of a term in the sequence. The number of
     * indeces indicates the number of dimensions in the sequence
     * @return the value of the sequence term
     */
    public double getTerm(int[] indices) throws IndexOutOfBoundsException;
}
