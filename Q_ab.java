/**
 * The nestedsums package collects summation methods for large nested series
 * to support the batch calculation of quantum electrodynamics model statistics,
 * such as linear entropy.
 * 
 * In general, quantum probabilities and quasi-probability values are between 0 
 * and 1, so double precision arithmetic is favored for speed with sufficient 
 * accuracy - 4 or 5 significant figures in the worst case. Some calculations
 * require very large number calculations in their constituent parts, which 
 * represents a performance bottleneck if applied arbitrarily.  
 * In general, quantum probabilities and quasi-probability values are near 0 and 
 * 1, so double precision arithmetic is favored for speed with sufficient 
 * accuracy - 4 or 5 significant figures in the worst case. Some calculations
 * require very large number calculations in their constituent parts, which 
 * represent a performance bottleneck if applied arbitrarily.  
 */
package nestedsums;

import java.math.BigDecimal;
import static java.math.BigDecimal.ROUND_HALF_UP;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * Calculations for Q coefficient generated and referenced repeatedly in the
 * final calculation Q-tilda: two dependent indeces, time independent = one 2D
 * table of all possible terms given 2 indices
 * 

 * Q_ab coefficients are not time dependant, so should be calculated once and 
 * repeatedly referenced. Q_ab values depend on a custom square-root calculation 
 * of BigIntegers, which is a significant performance bottleneck for larger 
 * maximum values.
 * TODO: Check symmetry of Q_ab values to reduce the number of calculations
 * TODO: Calculate Q_ab values/square roots in parallel, because they do not 
 *       depend on each other.
 *
 * @author forest
 */
public class Q_ab implements Sequence{
    private static  Q_ab instance;
    EntropyParameters params; //Experimental conditions
    double[][] terms;
    final static MathContext mc = new MathContext(1000);
    final static BigDecimal TWO = new BigDecimal(2, mc);

    /**
     * Private Q parameter constructor
     */
    private Q_ab() {
    }
    
    public static Q_ab getInstance(){
        if(instance == null) {
            instance = new Q_ab();
        }
        return instance;
    }
    
    public void init(EntropyParameters ep){
        if (this.params != ep){
            this.params = ep;
            FactorialSingleton fs = FactorialSingleton.getInstance();
            if (ep.alpha2sq * ep.alpha1sq < 16){
                fs.init(16);
                terms = new double[16][16];
           }
            else{
               fs.init(ep.alpha1sq*ep.alpha2sq);
               terms = new double[ep.alpha2sq * ep.alpha1sq ][ep.alpha2sq * ep.alpha1sq ];
            }
            System.out.println("Calculating Q coefficients ...");
            calculate();
            System.out.println("Q coefficients finished.");
        }
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
        FactorialSingleton fs = FactorialSingleton.getInstance();
        BigInteger a = fs.getFactorial(indices[0]);
        BigInteger b = fs.getFactorial(indices[1]);
        BigDecimal root = sqrt(a.multiply(b),400);
        if(root.doubleValue() != Double.POSITIVE_INFINITY)
            return Math.pow(params.alpha1, indices[0])
                * Math.pow(params.alpha2, indices[1])
                / root.doubleValue()
                / Math.exp(params.alpha1sq / 2.0)
                / Math.exp(params.alpha2sq / 2.0);
        else
            //Here, we assume the root denominator is so big, that the
            // total expression is 0 
            return Double.MIN_VALUE;
    }

    /**
     * Square Root calculator for BigDecimal using the Babylonian method.
     * @param   A       The base from which to calculate a square root. To get 
     *                  a good initial estimate, this must be an integer 
     * @param   SCALE   The minimum number binary digits of precision, as the 
     *                  Babylonian method converges quadratically
     * @return  
     */
    public static BigDecimal sqrt(BigInteger A, final int SCALE) {
        int exponent = A.toString().length()/2; //start with a good estimate
        BigDecimal guess = BigDecimal.TEN.pow(exponent);
        BigDecimal nextguess;
        for(int i =0; i < SCALE; i++){
            nextguess = guess;
            guess = new BigDecimal(A).divide(nextguess, SCALE, ROUND_HALF_UP);
            guess = guess.add(nextguess);
            guess = guess.divide(TWO, SCALE, ROUND_HALF_UP);
        }
        return guess;
    }
    
    /**
     * Fills in Q-Tilda terms table of a given size.
     * This has linear efficiency for the number of terms to calculate.
     */
    private void calculate() {
        for (int i = 0; i < terms.length; i++) {
            for (int j = 0; j <= i; j++) {
                terms[i][j] = getTerm(new int[]{i, j});
                //Q-Tilda values are diagonally symetric
                if(i != j)
                    terms[j][i] = terms [i][j];
            }
        }
    } /*
    
    public void calculate(){
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(threads);

        List<Future<double[]>> futures = new ArrayList<>();
        //Future<Double>[][] futures = new Future<Double>[terms.length][terms.length];
        for (int i = 0; i < terms.length; i++) {
                Q_abCalcThread callable = new Q_abCalcThread(i, this);
                futures.add(service.submit(callable));
        }
        service.shutdown();
        System.out.println("Calculating Q_ab coefficients");
        for (int n = 0; n < terms.length; n++) {
            try {
                System.out.println(Arrays.toString(futures.get(n).get()));
                for (int m = 0; m <= n; m++) {
                    terms[n][m] = futures.get(n).get()[m];
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Q_ab.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            }
                /*Q-Tilda values are diagonally symetric
                if (n != m)
                    terms[m][n] = terms[n][m];
                
        }
    }
    */
}
