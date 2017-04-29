/**
 * The nestedsums package collects summation methods for large nested series
 * to support the batch calculation of quantum electrodynamics model statistics,
 * such as linear entropy.
 */
package nestedsums;

/**
 * two independent indices, time dependant, uses C0, Q-tilda = one t-dependant
 * value
 *
 * @author forest
 */
public class N_0 implements Sequence {

    double time; //time at which the state-reductive measurement is made
    int max; //the maximum number of terms in the series to calculate any value
    Q_ab qt; //QTilda sequence for system under examination
    C_0 ct; //C_0 sequence for the time (see above) examined
    double[][] terms; //the individual series term terms, not the final value
    Double result; //the final value of the series

    /**
     * "raw" N_0 constructor 
     */
    public N_0(double time, int max, Q_ab qt, C_0 ct) {
        this.time = time;
        this.max = max;
        this.qt = qt;
        this.ct = ct;
    }

    /**
     * Encapsulated N_0 coefficient constructor
     */
    public N_0(double time, EntropyParameters ep) {
        this.time = time;
        this.max = ep.alpha2sq * ep.alpha1sq;
        if (this.max < 16) {
            this.max = 16;
        }
        this.qt = new Q_ab(ep);
        this.ct = new C_0(time, ep);
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
        return Math.pow(Math.abs(qt.getTerm(indices)), 2)
                * Math.pow(Math.abs(ct.getTerm(indices)), 2);
    }

    /**
     * Calculates the N_0 coefficient for a preset time and preset maximum 
     * number of sum terms. Unlike previous table-based coefficients, N_0 is a 
     * series so can benefit from recursive divide-and-conquer efficiency.
     * @return the value of N_0 for preset time
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