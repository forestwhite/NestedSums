/**
 * The nestedsums package collects summation methods for large nested series
 * to support the batch calculation of quantum electrodynamics model statistics,
 * such as linear entropy.
 * 
 * In general, quantum probabilities and quasi-probability values are near 0 and 
 * 1, so double precision arithmetic is favored for speed with sufficient 
 * accuracy - 4 or 5 significant figures in the worst case. Some calculations
 * require very large number calculations in their constituent parts, which 
 * represent a performance bottleneck if applied arbitrarily.  
 */
package nestedsums;

/**
 * Calculations for Q coefficient generated and referenced repeatedly in the
 * final calculation Q-tilda: two dependent indeces, time independent = one 2D
 * table of all possible terms given 2 indices
 * 
 * In gen
 *
 * @author forest
 */
public class Q_ab implements Sequence {

    EntropyParameters params; //Experimental conditions
    double[][] terms;

    /**
     * Encapsulated Q coefficient constructor
     */
    public Q_ab(EntropyParameters params) {
        this.params = params;
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
        return Math.pow(params.alpha1, indices[0])
                * Math.pow(params.alpha2, indices[1])
                / Math.sqrt(EntropyParameters.stirling(indices[0])
                        * EntropyParameters.stirling(indices[1]))
                / Math.exp(params.alpha1sq / 2.0)
                / Math.exp(params.alpha2sq / 2.0);
    }

    /**
     * Fills in Q-Tilda terms table of a given size.
     * This has linear efficiency for the number of terms to calculate.
     * @param   indices Coordinates of the last cell of the table/matrix that 
     *                  defines the range of terms to calculate. The first cell
     *                  is assumed to be {0,0}
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
