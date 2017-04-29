/**
 * The nestedsums package collects summation methods for large nested series
 * to support the batch calculation of quantum electrodynamics model statistics,
 * such as linear entropy.
 */
package nestedsums;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
        //Entropy field parameters: {delta, g12, g23, alpha1sq, alpha2sq, detectedstate}
        double[] params = {0.0, 1.0, 1.0, 1.0, 1.0, 0.0};
        EntropyParameters ep = new EntropyParameters(params);

        HashMap<Double, LinearEntropy> emap = new HashMap<>();
        System.out.println("Calculating Linear Entropy for each increment 0.1 of scaled time");
        double temp;
        for (int t = 0; t < 300; t++) {
            emap.put((double) t / 10.0, new LinearEntropy((double) t / 10.0, ep));
            temp = emap.get((double) t / 10.0).calculate();
            System.out.printf("%-3s %20s", (double) t / 10.0, temp);
            System.out.println();
        }
        writeLEDataFile(ep, emap);
    }

    /**
     * Creates a file containing the linear entopy calculations as a list for 
     * state-reductive mesurement times in sequence.
     * @param   ep  the entropy parameters of the system under investigation
     * @param   emap    the calculated linear entropy map with time(double) keys 
     */
    public static void writeLEDataFile(EntropyParameters ep, HashMap<Double, LinearEntropy> emap) throws IOException {
        FileWriter fw = new FileWriter("lentropy_fieldA_nbar" + ep.alpha1sq
                + "-" + ep.alpha1sq + "_0detected.txt");
        PrintWriter w = new PrintWriter(new BufferedWriter(fw));
        for (int t = 0; t < 300; t++) {
            w.println((double) t / 10.0 + " " + emap.get((double) t / 10.0).calculate());
        }
        w.close();
    }
}
