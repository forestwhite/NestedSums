/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nestedsums;

import java.util.HashMap;

/**
 *
 * @author forest
 */
public class BSingleton {
    static HashMap<Double,B_ab> cache = new HashMap<>();
    private static BSingleton instance;
    EntropyParameters params; //Experimental conditions

    
    private BSingleton() {
    }
    
    public static BSingleton getInstance(){
        if(instance == null) {
            instance = new BSingleton();
        }
        return instance;
    }
    
    /*
     * Calculates and populates the b coefficient cache. init() can be safely 
     * called even if the object has already been initialized
     */
    public void init (EntropyParameters ep) {
        if (this.params != ep){
            this.params = ep;
            Q_ab.getInstance().init(ep);
            System.out.println("Building B coefficient matrix ...");
            for (int t = 0; t < ep.maxtime/ep.interval; t++) {
                cache.put((double) t * ep.interval, new B_ab((double) t * ep.interval, params));
                cache.get((double) t * ep.interval).calculate();
            }
            System.out.println("B coefficient matrix complete");
        }
    }
    
    /**
     * Returns B coefficient precisely if cached.
     * @param   time
     * @param   indices
     * @return  double  result
     */
    public double getB(double time, int[] indices) {
        if(cache.containsKey(time))
            return cache.get(time).getTerm(indices);
        else
            return Double.MIN_VALUE; 
    }
}
