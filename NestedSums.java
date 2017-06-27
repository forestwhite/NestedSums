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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;

/**
 * A short demonstration which intentionally implements the calculations with 
 * the least complicated parameter set. Creates a file with a list of 
 * linear entropy values for field a in the two-mode gamma cavity interacting 
 * with a resonant gamma-configuration Rydberg atom based on the trance of a 
 * reduced density operator matrix.
 * TODO: at this time, if the file is already present, the results are not
 * recorded although they appear in the console output. A better implementation
 * is needed, such as including a timestamp on the output file so that no two 
 * files have the same name.
 * @author forest
 */
public class NestedSums {

    public static void main(String[] args) throws IOException {
        //Entropy field parameters: {delta, g12, g23, alpha1sq, alpha2sq, detectedstate, maxtime, interval}
        double[] params = {50.0, 1.0, 1.0, 25.0, 25.0, 0.0, 50, 0.1};
        EntropyParameters ep = new EntropyParameters(params);
        BSingleton bs = BSingleton.getInstance();
        bs.init(ep); //Build B coefficient table
        B_0 bcheck = new B_0(0.0,ep);
        System.out.println("Normalization check: |B| squared is " + bcheck.calculate());
        HashMap<Double, LinearEntropy> emap = new HashMap<>();
        System.out.println("Calculating Linear Entropy for each increment " + ep.interval + " of scaled time");
        double temp;
        for (int t = 0; t < ep.maxtime/ep.interval; t++) {
            emap.put(t * ep.interval, new LinearEntropy(t * ep.interval, ep));
            temp = emap.get(t * ep.interval).calculate();
            System.out.printf("%-3s %16s", round(t * ep.interval,1), temp);
            System.out.println();
        }
        try{
            writeLEDataFile(ep, emap);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates a file containing the linear entropy calculations as a list for 
     * state-reductive measurement times in sequence.
     * @param   ep  the entropy parameters of the system under investigation
     * @param   emap    the calculated linear entropy map with time(double) keys 
     * @throws java.io.IOException 
     */
    public static void writeLEDataFile(EntropyParameters ep, HashMap<Double, LinearEntropy> emap) throws IOException {
        FileWriter fw = new FileWriter("lentropy_fieldA_nbar" + ep.alpha1sq
                + "-" + ep.alpha2sq + "_0detected" + LocalTime.now().toString().replace(":","") + ".txt");
        try (BufferedWriter w = new BufferedWriter(fw)) {
            for (int t = 0; t < ep.maxtime/ep.interval; t++) {
                w.write(round((double) t * ep.interval,1) + " " + emap.get((double) t * ep.interval).calculate());
                w.newLine();
            }
        }
    }
    
    public static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
