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
 * EntropyParameters represent various conditions that exist in two mode field 
 * state model and provides a common object referenced by sequence elements in 
 * field statistics calculations. EntropyExpressions for the same set of initial
 * conditions can share these parameters:
 *  delta: an arbitrary detuning constant
 *  alpha1sq: mean average photons in the first coherent field
 *  alpha1: the root mean photons in the first coherent field
 *  alpha2sq: mean average photons in the second coherent field
 *  alpha2: the root mean photons in the second coherent field
 *         statistics
 *  g12: the atom-field coupling constant for energy interval 1-2
 *  g23: the atom-field coupling constant for energy interval 2-3
 *  detectedstate: the state detected in the state reductive measurement. Only 
 *                 states 0 and 2 are possible, and only 0 is considered because
 *                 symmetry suggests that field resonant with any transition 
 *                 jump can have the identical statistics as any other field
 * 
 * @author forest
 */
public class EntropyParameters{
    
    static final double ROOT2PI = Math.sqrt(2.0*Math.PI);
    
    // From Knight-Buzek-Lai paper (PRA 44, 6043 (1991))
    double delta = 0; //arbitrary detuning 
    double g12 = 1; //atom-field coupling constant for interval 1-2
    double g23 = 1; //atom-field coupling constant for interval 2-3
    int alpha1sq; //average initial photonx in field 1
    double alpha1; //root of the average initial photons in field 1
    int alpha2sq; //average initial photons in field 1
    double alpha2; //root of the avearage initial photons in field 2
    int detectedstate; //the detected state of the atom
    int maxtime = 10; //range of time for calculations
    double interval= 0.1; //time interval of calculations
    
    /**
     * Entropy parameters for a two-mode coherent light cavity interacting with 
     * a resonant gamma configuration Rydberg atom
     * @param   SharedParams    Entropy field parameters: {delta, g12, g23, 
     *                          alpha1sq, alpha2sq, detectedstate} 
     */
    public EntropyParameters(double[] SharedParams){
        this.delta = SharedParams[0];
        this.g12 = SharedParams[1];
        this.g23 = SharedParams[2];
        this.alpha1sq = (int)SharedParams[3];
        this.alpha1 = Math.sqrt(alpha1sq);
        this.alpha2sq = (int)SharedParams[4];
        this.alpha2 = Math.sqrt(alpha2sq);
        this.detectedstate = (int)SharedParams[5];
        this.maxtime = (int)SharedParams[6];
        this.interval = SharedParams[7];
    }
}
