/**
 * The nestedsums package collects summation methods for large nested series
 * to support the batch calculation of quantum electrodynamics model statistics,
 * such as linear entropy.
 */
package nestedsums;

/**
 * Generates coefficients for projected field mode states, given: 1. An initial
 * two-mode (modes a,b) coherent light field in an isolated cavity 2. A Rydberg
 * atom heat selected such that the valence electron can resonate between energy
 * levels at the frequency of the coherent light in the cavity in a 3 level /\
 * configuration. 3. A state-reductive measurement on the atom after it has
 * passed through the cavity
 *
 * Under these conditions, the atom and light fields are potentially entangled,
 * and the state-reductive measurement projects a state onto the coherent light
 * fields in the cavity after interaction. The linear entropy value indicates
 * the level of entanglement.
 *
 * two independent indices, time dependent, uses C0, Q-tilda, and N0 = 2D table
 * of all possible terms given 2 indices for each time t
 *
 * @author forest
 */
public class B_ab implements Sequence {

    double time; //time at which the state-reductive measurement is made
    Q_ab qt; //QTilda sequence for system under examination
    C_0 ct; //C_0 sequence for the time (see above) examined
    N_0 nt; //N_0 sequence for the time (see above) examined
    double[][] terms;

    /**
     * "raw" B_ab constructor 
     */
    public B_ab(double time, Q_ab qt, C_0 ct, N_0 nt) {
        this.time = time;
        this.qt = qt;
        this.ct = ct;
        this.nt = nt;
    }

    /*
     * Encapsulated B coefficient constructor
     */
    public B_ab(double time, EntropyParameters ep) {
        this.time = time;
        this.qt = new Q_ab(ep);
        this.ct = new C_0(time, ep);
        this.nt = new N_0(time, ep);
    }

    /**
     * Optimized with memoization
     *
     * @author forest
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
        return 1 / nt.calculate() * qt.getTerm(indices) * ct.getTerm(indices);
    }

}
