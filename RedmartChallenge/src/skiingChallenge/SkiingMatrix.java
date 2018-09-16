package skiingChallenge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Mrigendra
 * REDMART problem statement.
 * Given a Height Map in form of 2-D matrix. Compute the longest and the steepest path and in the downward direction.  
 * Problem statement: http://geeks.redmart.com/2015/01/07/skiing-in-singapore-a-coding-diversion/
 */

/**
 * Path class to create objects to store length and steepness of each path.
 */
class Path implements Cloneable {
	int len = 0;
	int steep = 0;
	
	/**
	 * Constructor to initialize each object
	 * @param len - best length of the path 
	 * @param steep - steep from this point
	 */
	Path(int len, int steep) {
		this.len = len;
		this.steep = steep;
	}
	
	/**
	 * If new Path has more length or (same length and steeper path) store new Path 
	 * object to 'calling' Path object. This makes it the selected path till now.
	 * @param nPath - new Path to be compared to selected path till now
	 */
	public void compare(Path nPath) {
		if( this.len < nPath.len || 
				( this.len == nPath.len && this.steep < nPath.steep ) ) {
			this.len = nPath.len;
			this.steep = nPath.steep;
		}
	}
	
	/**
	 * Update new Path len and steep for current map point by adding 1 and comparing
	 * current steep with max steep in rest of the path. Call compare method to compare
	 * updated path values with best selected path till now for this point.
	 * @param nPath - rest of the path after current map point
	 * @param newSteep - current steep between this map point and next point
	 */
	public void updateAndCompare(Path nPath, int newSteep) {
		nPath.len++;
		nPath.steep = newSteep + nPath.steep;
		compare( nPath );
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	public String toString() {
		return "(" + len + "-" + steep + ")";
	}
}


public class SkiingMatrix {

	/*
	 * filePath to read input data
	 */
	static final String fileName;
	
	static {
		// Pass the path to the file to be read for input Map matrix
		fileName = "src/skiingChallenge/skiingInputData2";
		
	}
	
	/**
	 * Matrix to store input heights in 2-D array.
	 */
	static int[][] ipArr;
	
	/**
	 * Path Matrix to store best path from each Map point.
	 */
	static Path[][] sol;
	
	/**
	 * Variables to store variable dimensions. 
	 */
	static int N, M;
	
	/**
	 * We read the input map matrix and compute best path from each map point 
	 * and print out best Path.
	 * @param args
	 * @throws CloneNotSupportedException
	 */
	public static void main(String[] args) throws CloneNotSupportedException {
		
		String[] line;
		
		try( BufferedReader file = new BufferedReader(new FileReader(fileName)); ){
			String[] matDim = file.readLine().split(" ");
			N = Integer.parseInt(matDim[0]);
			M = Integer.parseInt(matDim[1]);

			// initialize input and solution array sizes and line array 
			ipArr = new int[N][M];
			sol = new Path[N][M];
			
			//Reading the input map matrix into the array
			for( int i=0; i<N; i++ ) {
				line = file.readLine().trim().split(" ");
				for(int j=0; j<M; j++ ) {
					ipArr[i][j] = Integer.parseInt( line[j] );
					
					// Initialize solution array for each map point.
					sol[i][j] = new Path(0, 0);
//					System.out.print(ipArr[i][j] + "   ");
				}
//				System.out.println();
			}
			
			Path bestPath = new Path(0, 0);
			Path tempBestPath;
			for( int i=0; i<N; i++ ) {
				for(int j=0; j<M; j++ ) {
					if( sol[i][j].len == 0 ) {
						tempBestPath = findLongAndSteepPath( i, j );
						bestPath.compare( tempBestPath );
//						printMatrix();
//						System.out.println(i + " " + j + ": " +result);
					}
				}
			}
			System.out.println("Best computed path(length and steep) is as follows: ");
			System.out.println(bestPath.len + "   " + bestPath.steep);
			
			
		} catch(IOException e) {
			System.out.println("Exception occured when reading file: " + fileName);
			System.out.println(e.getMessage());
		}
	}
	
	
	/**
	 * Recursively calls the method to compute the path length and steep value for rest 
	 * of the path and compare it with other length and steep at current map point.
	 * @param i - map location in matrix
	 * @param j - map location in matrix
	 * @return - return best selected path(longest and steepest).
	 * @throws CloneNotSupportedException
	 */
	private static Path findLongAndSteepPath(int i, int j) throws CloneNotSupportedException {
		
		// base case ( return solution[i][i] if we have already computed the path from this map-point
		if( sol[i][j].len != 0 )
			return (Path) sol[i][j].clone();
		
		// otherwise
		Path bestPathTillNow = new Path(1, 0);
		Path tempP;
		
		if( i > 0 && ipArr[i-1][j] < ipArr[i][j] ) {
			tempP = findLongAndSteepPath(i-1, j);
			bestPathTillNow.updateAndCompare( tempP, ipArr[i][j] - ipArr[i-1][j] );
		}
		if( j > 0 && ipArr[i][j-1] < ipArr[i][j] ) {
			tempP = findLongAndSteepPath(i, j-1);
			bestPathTillNow.updateAndCompare( tempP, ipArr[i][j] - ipArr[i][j-1] );
		}
		if( i < N-1 && ipArr[i+1][j] < ipArr[i][j] ) {
			tempP = findLongAndSteepPath(i+1, j);
			bestPathTillNow.updateAndCompare( tempP, ipArr[i][j] - ipArr[i+1][j] );
		}
		if( j < M-1 && ipArr[i][j+1] < ipArr[i][j] ) {
			tempP = findLongAndSteepPath(i, j+1);
			bestPathTillNow.updateAndCompare( tempP, ipArr[i][j] - ipArr[i][j+1] );
		}
		
		sol[i][j] = (Path) bestPathTillNow.clone();
		return bestPathTillNow;
	}
	
	/**
	 * Print the solution matrix
	 */
	private static void printMatrix() {
		for( int i=0; i<N; i++ ) {
			for(int j=0; j<M; j++ ) {
				System.out.print(sol[i][j] + "   ");
			}
			System.out.println();
		}
		System.out.println("\n");
	}

}

