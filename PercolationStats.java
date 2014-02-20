
public class PercolationStats {
    
    private final int N;
    private final int T;
    private double confidenceLow = -1;
    private double confidenceHigh = -1;
    
    /**
     * perform T independent computational experiments on an N-by-N grid
     * @param N
     * @param T 
     */
    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) {
            throw new IndexOutOfBoundsException("Positive integers expected.");
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
     * sample standard deviation of percolation threshold
     * @return 
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
     * returns lower bound of the 95% confidence interval
     * @return 
     */
    public double confidenceLo() {
        if (confidenceLow < 0) {
            stddev();
        } 
        return confidenceLow;
    } 
    
    /**
     * returns upper bound of the 95% confidence interval
     * @return 
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
    public static void main(String[] args){
        PercolationStats ps = new PercolationStats(200, 100);
            StdOut.println("mean = " + ps.mean());
            StdOut.println("stddev = " + ps.stddev());
            StdOut.println("conLo = " + ps.confidenceLo());
            StdOut.println("conHi = " + ps.confidenceHi());
    } 
}
