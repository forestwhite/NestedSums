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

import java.math.BigInteger;
import java.util.HashMap;

/**
 * The singleton class calculates factorials once for reference.
 * The efficiencies are cumulative to retain high accuracy, and for linear 
 * entropy, all getFactorial values for a given range are used, so all 
 * factorials  are calculated cumulatively. The singleton can be referenced 
 * repeatedly with the lookup efficiency of the HashTable storing the values.
 */
public class FactorialSingleton {
    //Eagerly create the FactorialSingleton object when the class is loaded so
    //access does not need to be synchronized.
    private static final FactorialSingleton uniqueInstance = new FactorialSingleton();
    static HashMap<Integer,BigInteger> cache = new HashMap<>();
    
    private FactorialSingleton() {
    }
    
    public static FactorialSingleton getInstance() {
        cache.put(0, BigInteger.ONE);
        return uniqueInstance;
    }
    
    /**
     * Returns getFactorial precisely if cached.
     * @param   n   the integer to find the getFactorial of.
     * @return  BigInteger  BigInteger result
     */
    public BigInteger getFactorial(int n) {
        if(cache.containsKey(n))
            return cache.get(n);
        else
            return null; //TODO: calculate Stirling's approximation (Bauer)
                         //      or init to new larger range
    }
    
    /*
     * Calculates and populates the table of getFactorial values up to n.  
     * Because most of the package applications use all getFactorial values up 
     * to n for calculations, it is as efficient to calculate them all as it is 
     * to calculate estimates for them all
     */
    void init(int n) {
        if(!cache.containsKey(n)){
            System.out.println("Calculating Factorials...");
            for(int j = 1; j<= n;j++){
                if(!cache.containsKey(j))
                    cache.put(j,cache.get(j-1).multiply(BigInteger.valueOf(j)));
            }
            System.out.println("Factorials finished. e.g. " + n + "! = " + getFactorial(n));
        }
    }
}
