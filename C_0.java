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
 * The C0 coefficients based on two indices for a given time. In the simplest
 * case, the detuning parameter delta is 0 which does not involve any complex
 * algebra to evaluate. When the detuining is not zero, complex algebra changes 
 * the resulting coefficients, and for squared trace eventually resolves to a 
 * real number.
 *
 * The Lai - Buzek - Knight paper describes C_0(na,nb,t), where Subscript[n, a]
 * and Subscript[n, b] are the summation indices. See [1] W.K. Lai, V. Buzek,
 * and P.L. Knight, Phys. Rev. A 44, 6043 (1991)
 *
 * @author forest
 */
public class C_0 implements Sequence {

    EntropyParameters params; //Experimental conditions
    double time; //time at which the state-reductive measurement is made
    Complex[][] terms;

    /**
     * C_0 constructor 
     * @param time
     * @param params
     */
    public C_0(double time, EntropyParameters params) {
        this.params = params;
        this.time = time;
    }

    /**
     * Optimized with memoization
     *
     * @author forest
     */
    @Override
    public Complex getTerm(int[] indices) throws IndexOutOfBoundsException {
        if (indices.length > 2) {
            throw new IndexOutOfBoundsException();
        }
        //if the term is already calculated, fetch stored value
        if (terms != null && terms[indices[0]][indices[1]] != null) {
            if (indices[1] < this.terms[0].length && indices[0] < this.terms.length) {
                return terms[indices[0]][indices[1]];
            }
        }
        //if the term is not already calculated, calculate it
        double realC;
        double imagC;
        //With non-zero detuning
        if(params.delta != 0.0){
            realC = oSquared2(indices[1]) / o2Squared(indices[0], indices[1])
                + oSquared2(indices[0] - 1) / o2Squared(indices[0], indices[1])
                * (Math.cos(time * ot(indices[0], indices[1]))*Math.sin((-1)*time*params.delta)
                    + params.delta*(Math.sin(time * ot(indices[0], indices[1])))
                     *(Math.cos(time*params.delta)*ot(indices[0], indices[1])));
            imagC = oSquared2(indices[0] - 1) / o2Squared(indices[0], indices[1])
                * (Math.cos(time * ot(indices[0], indices[1]))*Math.cos(time*params.delta)
                   + params.delta*(Math.sin(time * ot(indices[0], indices[1])))
                     *(Math.sin((-1)*params.delta)*ot(indices[0], indices[1])));
        } 
        //with zero detuning -> avoid dividing by zero, because coefficient is 0
        else {
            realC = oSquared2(indices[1]) / o2Squared(indices[0], indices[1])
                    + oSquared2(indices[0] - 1) / o2Squared(indices[0], indices[1])
                      * (Math.cos(time * ot(indices[0], indices[1])));
            imagC = 0.0;
        }
        return new Complex(realC,imagC);
    }

    /**
     * Fills in C0 terms table of a given size.
     * This has linear efficiency for the number of terms to calculate.
     * @param   indices Coordinates of the last cell of the table/matrix that 
     *                  defines the range of terms to calculate. The first cell
     *                  is assumed to be {0,0}
     */
    public void calculate(int[] indices) {
        terms = new Complex[indices[0]][indices[1]];
        for (int i = 0; i < indices[0]; i++) {
            for (int j = 0; j < indices[1]; j++) {
                terms[i][j] = getTerm(new int[]{i, j});
            }
        }
    }

    /**
     * Omega factor calculated for first field at containing function index
     * @param   n   index position in the sequence of the containing function 
     * @return  
     */
    public double oSquared1(int n) {
        return params.g12 * params.g12 * (n + 1);
    }

    /*
     * Omega factor calculated for second field at containing function index
     * @param index position in the sequence of the containing function 
     */
    public double oSquared2(int n) {
        return params.g23 * params.g23 * (n + 1);
    }

    /*
     * Omega Squared factor calculated several times by higher order functions
     * @param n index position in the sequence of the containing function
     * @param m second index position in the sequence of the containing function 
     */
    public double o2Squared(int n, int m) {
        return oSquared1(n - 1) + oSquared2(m);
    }

    /*
     * Omega Tilda factor calculated for higher order functions
     * @param n index position in the sequence of the containing function
     * @param m second index position in the sequence of the containing function 
     */
    public double ot(int n, int m) {
        return Math.sqrt(o2Squared(n, m) + Math.pow(params.delta / 2.0, 2));
    }
}
