/**
 * @author Gabriel Grigore
 * <p>
 * gabriel.grigore@yahoo.ca
 * </p>
 * <p>
 * February 21th, 2014
 * </p>
 * Class model a percolation system. Uses the weighted union find algorithm.
 * There is no main method here, create one in order to use this class.
 */
public class Percolation {
    
    //size of the grid
    private final int N;
    //block site state in the grid
    private final int blocked;
    //open site state in the grid
    private final int open;
    //the array that emulate the grid
    private int[][] id;
    //
    private int[] starters;
    private int startersIdx;
    //weighted union find algorithm implementation
    private WeightedQuickUnionUF unionFind;
    
    /**
     * create N-by-N grid, with all sites blocked
     * @param N the size of the grid
     */
   public Percolation(int N) {
       if (N <= 0) {
           throw new IndexOutOfBoundsException(
                   "The input N must be a positive integer.");
       }
       blocked = 0;
       open = 1;
       this.N = N;
       id = new int[N][N];
       for (int i = 0; i < N; i++) {
           for (int j = 0; j < N; j++) {
               id[i][j] = blocked;
           }
       }
       starters = new int[N];
       startersIdx = 0;
       unionFind = new WeightedQuickUnionUF(N * N);
       //prepare the virtual top site
       for (int i = 1; i < N; i++) {
           unionFind.union(0, i);
       }
       //prepare the virtual bottom site
//       int lastRowFirstElem = N * (N - 1);
//       int lastRowLastElem = N * N - 1;
//       for (int i = 0; i < N - 1; i++) {
//           unionFind.union(lastRowFirstElem + i, lastRowLastElem);
//       }
   }
   
   /**
    * open site (row i, column j) if it is not already
    * @param i
    * @param j 
    * @throws java.lang.IndexOutOfBoundsException if at least one is not in the range
    */
   public void open(int i, int j) {
       int row = i;
       int col = j;
      if (isIndexInRange(row, col)) {
           row = row - 1;
           col = col - 1;
           if (id[row][col] == blocked) {
                id[row][col] = open;
           } else {
               return;
           }
      }
      //conect to all adjacent open sites  
      int currentIdx =  ufIndex(row, col);
      //top site
      int topIdx = ufIndex(row - 1, col);
      if (topIdx >= 0 && id[row - 1][col] == open) {
          unionFind.union(currentIdx, topIdx);
      }
      int rightIdx = ufIndex(row, col + 1);
      if (rightIdx >= 0 && id[row][col + 1] == open) {
          unionFind.union(currentIdx, rightIdx);
      }
      int bottomIdx = ufIndex(row + 1, col);
      if (bottomIdx >= 0 && id[row + 1][col] == open) {
          unionFind.union(currentIdx, bottomIdx);
      }
      int leftIdx = ufIndex(row, col - 1);
      if (leftIdx >= 0 && id[row][col - 1] == open) {
          unionFind.union(currentIdx, leftIdx);
      }
      if (N == i) {
          starters[startersIdx++] = j;
      }
   }
   
   /**
    * is site (row i, column j) open?
    * @param i grid row index
    * @param j grid column index
    * @return
    * @throws java.lang.IndexOutOfBoundsException if at least one is not in the range
    */
   public boolean isOpen(int i, int j) {
       if (isIndexInRange(i, j)) {
           return id[i-1][j-1] != 0;
       } else {
            return false;
       }
  }
   
   /**
    * is site (row i, column j) full?
    * @param i grid row index
    * @param j grid column index
    * @return true if the site is connected to the top row
    * @throws java.lang.IndexOutOfBoundsException if at least one is not in the range
    */
   public boolean isFull(int i, int j) {
       if (isIndexInRange(i, j)) {
           //index zero is used as the virtual top site
           int wufIdx = ufIndex(i-1, j-1);
           if (isOpen(i, j)) {
                if (wufIdx < N) {
                    return true;
                }
                return unionFind.connected(0, wufIdx);
           }
           else {
               return false;
           }
       } else {
           return false;           
       }
   }
   
   /**
    * does the system percolate?
    * @return true if it does
    */
   public boolean percolates()  {
       if (N == 1) {
           return isOpen(N, N);
       } else {
           //this can be improved by iterating only the bottom open sites
           //store them in a separate array?
           for (int j = 0; j < startersIdx; j++) {
                if (true == unionFind.connected(0, ufIndex(N-1, starters[j]-1))) {
                    return true;
                }
           }
           return false;
       }
    }
   
   /**
    * Check if integer is in the range 1 to N.
    * @param i integer to be checked
    * @return true if parameter is in the range
    * @throws java.lang.IndexOutOfBoundsException if not in the range
    */
   private boolean isIndexInRange(int i) {
       if (i >= 1 && i <= N) {
           return true;
       } else {
           throw new IndexOutOfBoundsException("Site index out of bound: " + i);
       }
   }
   
   /**
    * Check if integers are in the range 1 to N.
    * @param i integer to be checked
    * @param j integer to be checked
    * @return true if both integers are in the range 
    * @throws java.lang.IndexOutOfBoundsException if at least one is not in the range
    */
   private boolean isIndexInRange(int i, int j) {
       return isIndexInRange(i) && isIndexInRange(j);
   }
   
   /**
    * @param i line index in the grid, 0 to N-1
    * @param j column index in the grid, 0 to N-1
    * @return the index in union find representation or -1 if input is incorrect
    */
   private int ufIndex(int i, int j) {
       if ((i >= 0 && i < N) && (j >= 0 && j < N)) {
           return i * N + j;
       } else {
           return -1;
        }
   }
   
}
