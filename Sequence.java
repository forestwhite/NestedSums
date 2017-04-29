/**
 * The nestedsums package collects summation methods for large nested series
 * to support the batch calculation of quantum electrodynamics model statistics,
 * such as linear entropy.
 */
package nestedsums;

/**
 * Generates generic sequence terms, as defined by an index position.
 *
 * @author forest
 */
public interface Sequence {

    /**
     * @param indices Index numbers of a term in the sequence. The number of
     * indeces indicates the number of dimensions in the sequence
     * @return the value of the sequence term
     */
    public double getTerm(int[] indices) throws IndexOutOfBoundsException;
}
