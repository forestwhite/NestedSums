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
 * Linear entropy is equal to 1 minus the trace of the square of the reduced
 * density operator, Tr(Ro_Squared). S = 1- Trace[RoSquared]=1-Sum[Sum[F[q,
 * m]*F[q, m']
 *
 * @author forest
 */
public class LinearEntropy {

    double time; //time at which the state-reductive measurement is made
    int max; //the number of terms to sum
    F_ab fl; //F coefficient
    TrRoSquared tTerms; //Trace of ro_squared terms
    Double result; //Result of 1 - Trace(Ro_Squared)

    /**
     * "raw" Linear Entropy constructor 
     */
    LinearEntropy(double time, int max, F_ab fl) {
        this.time = time;
        this.max = max;
        this.fl = fl;
        this.tTerms = new TrRoSquared(max, fl);
    }

    /**
     * Encapsulated Linear Entropy constructor
     */
    LinearEntropy(double time, EntropyParameters ep) {
        this.time = time;
        this.max = ep.alpha2sq * ep.alpha1sq;
        if (this.max < 16) {
            this.max = 16;
        }
        this.fl = new F_ab(time, ep);
        this.tTerms = new TrRoSquared(max, fl);
    }

    /**
     * Calculates the linear entropy for a set time and maximum number of terms
     * Optimized with memoization
     * @return the value of linear entropy for a specific time
     */
    public double calculate() {
        if (result != null && result != 0.0) {
            return result;
        }
        this.tTerms.calculate();
        Series sum = new Series(tTerms, max, 2);
        result = 1.0 - sum.calculate();
        return result;
    }
}
