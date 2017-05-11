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
 */
package nestedsums;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.HashMap;

/**
 * The singleton class calculates factorial approximations once for factorials.
 * The efficiencies are cumulative to retain high accuracy, and for linear 
 * entropy, all factorial values for a given range are used, so all factorials
 * are calculated cumulatively. However, no factorial is calculated more than 
 * once and the singleton can be referenced repeatedly with the efficiency 
 * of the HashTable storing the values.
 */
public class FactorialSingleton {
    //Eagerly create the FactorialSingleton object when the class is loaded so
    //access does not need to be synchronized.
    private static final FactorialSingleton uniqueInstance = new FactorialSingleton();
    static HashMap<Integer,BigInteger> cache = new HashMap<>();
    final static MathContext mc = new MathContext(1000);
    final static BigDecimal TWO = new BigDecimal(2, mc);
    
    private FactorialSingleton() {
    }
    
    public static FactorialSingleton getInstance() {
        cache.put(0, BigInteger.ONE);
        return uniqueInstance;
    }
    
    /**
     * Returns factorial precisely if cached.
     * @param   n   the integer to find the factorial of.
     * @return  BigInteger  BigInteger result
     */
    public BigInteger factorial(int n) {
        if(cache.containsKey(n))
            return cache.get(n);
        else
            return null; //TODO: calculate Stirling's approximation (Bauer)
    }
    
    /*
     * Calculates and populates the table of factorial values up to n. Because 
     * most of the present applications use all factorial values up to n for
     * calculations, it is more efficient to calculate them all.
     */
    void calculate(int n) {
        for(int j = 1; j<= n;j++){
            if(!cache.containsKey(j))
                cache.put(j,cache.get(j-1).multiply(BigInteger.valueOf(j)));
        }
    }
}
