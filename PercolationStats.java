/**
 * @author Gabriel Grigore
 * <p>
 * gabriel.grigore@yahoo.ca
 * </p>
 * <p>
 * February 21th, 2014
 * </p>
 * Class helps to gather statistics for a percolation system. Uses the 
 * Percolation class, algs4.jar and stdlib.jar to achieve goals. 
 * Usage: "java PercolationStats 20 20"
 */
public class PercolationStats {
    //size of the grid
    private final int N;
    //number of observations
    private final int T;
    //lower bound of the confidence interval
    private double confidenceLow = -1;
    //upper bound of the confidence interval
    private double confidenceHigh = -1;
    
    /**
     * perform T independent computational experiments on an N-by-N grid
     * @param N size of the grid
     * @param T number of observations
     */
    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException("Positive integers expected.");
        }
        this.N = N;
        this.T = T;
    }
    
    /**
     * @return sample mean value of percolation threshold
     */
    public double mean() {
        double sampleMean = 0;
        double sitesNo = N*N;
        for (int i = 0; i < T; i++) {
            Percolation p = new Percolation(N);
            long openSites = 0;
            while (!p.percolates()) {
                int row = StdRandom.uniform(N) + 1;
                int col = StdRandom.uniform(N) + 1;
                if (!p.isOpen(row, col)) {
                    p.open(row, col);
                    openSites++;
                }
            }
            //StdOut.println("open sites = " + openSites + " total = " + N*N);
            sampleMean += openSites/sitesNo;
        }
         sampleMean = sampleMean/T;
        return sampleMean;
    }
    
    /**
     * @return standard deviation of percolation threshold
     */
    public double stddev() {
        double sampleMean = 0;
        double sitesNo = N*N;
        double[] observations = new double[T];
        for (int i = 0; i < T; i++) {
            Percolation p = new Percolation(N);
            long openSites = 0;
            while (!p.percolates()) {
                int row = StdRandom.uniform(N) + 1;
                int col = StdRandom.uniform(N) + 1;
                if (!p.isOpen(row, col)) {
                    p.open(row, col);
                    openSites++;
                }
            }
            observations[i] = openSites/sitesNo;
            sampleMean += observations[i];
        }
        sampleMean /= T;   
        
        double stddev = 0;
        for (int i = 0; i < T; i++) {
            double temp = observations[i] - sampleMean;
            stddev += temp * temp;
        }
        if (T - 1 > 0) {
            stddev = stddev / (T - 1);
        } else {
            stddev = 0;
        }
        stddev = Math.sqrt(stddev);

        double temp = (1.96*stddev)/Math.sqrt(T);
        confidenceLow = sampleMean - temp;
        confidenceHigh = sampleMean + temp;
        
        return stddev;
    }
    
    /**
     * @return lower bound of the 95% confidence interval
     */
    public double confidenceLo() {
        if (confidenceLow < 0) {
            stddev();
        } 
        return confidenceLow;
    } 
    
    /**
     * @return upper bound of the 95% confidence interval
     */
    public double confidenceHi() {
        if (confidenceHigh < 0) {
            stddev();
        }
        return confidenceHigh;
    }
    
    /**
     * test client, main class
     * @param args 
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Wrong number of arguments. Expected two.");
        } else {
            int N, T;
            try {
                N = Integer.parseInt(args[0]);
                T = Integer.parseInt(args[1]);
                PercolationStats ps = new PercolationStats(N, T);
                StdOut.printf("%-25s %5s %-7f\n", "mean ", "=", ps.mean());
                StdOut.printf("%-25s %5s %-7f\n", "stddev ", "=", ps.stddev());
                StdOut.printf("%-25s %5s %-7f %1s %7f\n", "95% confidence interval ",
                        "=", ps.confidenceLo(), ", ", ps.confidenceHi());            
            } catch (NumberFormatException e) {
                System.err.println("Arguments should be integers. Check and retry.");
            }
        }
    } 
}