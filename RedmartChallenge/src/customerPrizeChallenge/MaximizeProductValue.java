package customerPrizeChallenge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * Problem statement: http://geeks.redmart.com/2015/10/26/1000000th-customer-prize-another-programming-challenge/
 * @author Mrigendra
 * 
 * Compute the products from the input list of 20000 items with id, length, width, height, Weight, price such that:
 * 1. Any item you take must fit completely in the bag
 * 2. you can assume that if the products fit into the bag both individually and together by total volume, there is a way to pack them in
 * 3. Price value must be maximized
 * 4. Weight must be minimized without compromising on dollar value
 *
 */

class Prod implements Comparable<Prod>{
	String productId;
	int price, weight, volume;
	public Prod(String productId, String price, int volume, String weight) {
		super();
		this.productId = productId;
		this.price = Integer.parseInt(price);
		this.weight = Integer.parseInt(weight);
		this.volume = volume;
	}
	@Override
	public int compareTo(Prod ob) {
		if( volume == ob.volume ) {
			return this.weight - ob.weight;
		} else {
			return this.volume - ob.volume;
		}
	}
}

public class MaximizeProductValue {
	
	static final String filePath;
	static final int toteVol, prodCount, length, width, height;
	static {
		// Pass the path to the file to be read for input Map matrix
		filePath = "src/customerPrizeChallenge/productLog1";
		length = 45;
		width = 30;
		height = 35;
		toteVol = length*width*height;
		prodCount = 20000;
	}

	public static void main(String[] args) {
		// To compute the computation time.
		long startTime = System.nanoTime();
		
		try( BufferedReader file = new BufferedReader(new FileReader(filePath)); ){
			
			String line = file.readLine();
			String[] colValues;
			ArrayList<Prod> prodList = new ArrayList<>(prodCount);
			ArrayList<Prod> selectedList = new ArrayList<>();
			
			// Read ProductId, price, length, width, height, weight
			while( line != null ) {
				colValues = line.trim().split(",");
				
				// If product volume 
				int prodVol = productFits( colValues[2], colValues[3], colValues[4] );
				if( prodVol != 0 ) {
					prodList.add( new Prod(colValues[0], colValues[1], prodVol, colValues[5]) );
				}
				line = file.readLine();
			}
			
			System.out.println( "Size: " + prodList.size() );
//			Collections.sort( prodList );
			fillTote( prodList );
			
		} catch (Exception e) {
			System.out.println("Exception when opening/reading File: " + e.getMessage());
		}
		
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("Execution Time: " + totalTime/(1000000000*60) + "min");
	}
	

	static class MatrixCell {
		int totVal;				int weight;
		int sumProductId;		int count;
		MatrixCell() {
			totVal = 0;			weight = 0;
			sumProductId = 0;		count = 0;
		}
		public void update(Prod p, int newValue, int newWeight) {
			this.sumProductId += Integer.parseInt( p.productId.trim() );
			this.weight = newValue;
			this.totVal = newWeight;
			this.count++;
		}
	}
	
	/**
	 * Apply optimized knapsack Algorithm and fill the tote. 
	 * just Store sum of selected products on each cell so that backtracking is not required.
	 * @param prodList
	 * @param selectedList
	 */
	private static void fillTote(ArrayList<Prod> prodList ) {
		
		Prod p;
		List<MatrixCell> mat = Stream.generate( MatrixCell::new )
									.limit( toteVol+1 )
									.collect( Collectors.toList() );
		// Optimized Knapsack algorithm with 1-D array
		System.out.println(" Starting algorithm...");
		for( int i=0; i < prodList.size(); i++) {
			p = prodList.get(i);
			if( i%1000 == 0 )
				System.out.println(i);
			for( int j=toteVol; j >= p.volume; j-- ) {
				updateCell( mat, j, p );
			}
		}
		System.out.println( "Number of elements: " + mat.get(toteVol).count );
		System.out.println( "Total of product Ids: " + mat.get(toteVol).sumProductId );
	}

	private static void updateCell(List<MatrixCell> mat, int inx, Prod p) {
		int prevValue = mat.get(inx).totVal;
		int prevWeight = mat.get(inx).weight;
		
		// Compute effective price if this product will be added to knapsack
		int newValue = p.price + mat.get(inx-p.volume).totVal;
		// Weight computation if effective total price of products comes out to be equal
		int newWeight = p.weight + mat.get(inx-p.volume).weight;
		
		if( prevValue == newValue && prevWeight > newWeight ) {
			MatrixCell cell = mat.get(inx);
			cell.update(p, newValue, newWeight );
		} else if (prevValue < newValue ) {
			MatrixCell cell = mat.get(inx);
			cell.update(p, newValue, newWeight );
		}
	}


	/**
	 * Method to check if the product fits in the box dimension wise and volume wise.
	 * This method returns volume of the product. If the product does not fits, method returns 0
	 * @param l
	 * @param w
	 * @param h
	 * @return
	 */
	private static int productFits(String l, String w, String h) {
		int d1 = Integer.parseInt(l);
		int d2 = Integer.parseInt(w);
		int d3 = Integer.parseInt(h);
		if( d1 <= length && d2 <= width && d3 <= height && d1*d2*d3 <= toteVol )
			return d1*d2*d3;
		else
			return 0;
	}

	private static int computeVolume(String l, String w, String h) {
		// compute volume = l*w*h
		return ( Integer.parseInt(l) * Integer.parseInt(w) * Integer.parseInt(h) );
	}

	
//============================Unused Methods with classic knapsack============================================
	

	/**
	 * Print result of selected products to fill the tote and print the sum of product ids
	 * @param selectedList
	 */
	private static void printResult(ArrayList<Prod> selectedList) {
		System.out.println(" Number of selected items: " + selectedList.size() );
		int productIdSum = 0;
		for( Prod p : selectedList ) {
			System.out.println(p.productId);
			productIdSum += Integer.parseInt( p.productId.trim() );
		}
		System.out.println( "\n\nSum of selected Product Id's:  " + productIdSum );
	}

	/**
	 * Apply classic knapsack Algorithm and fill the tote. 
	 * Store list of selected products in the selected list.
	 * @param prodList
	 * @param selectedList
	 */
	private static void fillTote(ArrayList<Prod> prodList, ArrayList<Prod> selectedList) {
		int[][] mat = new int[prodList.size()+1][toteVol+1];
		Prod p;
		
		// Knapsack algorithm
		for( int prod=0; prod < prodList.size()+1; prod++) {
			for( int vol=0; vol < toteVol+1; vol++ ) {
				if( prod==0 || vol==0 ) {
					mat[prod][vol] = 0;
				} else if (prodList.get(prod-1).volume <= vol) {
					p = prodList.get(prod-1);
					mat[prod][vol] = Math.max(mat[prod-1][vol], 
											p.price + mat[prod-1][vol - p.volume] );
				} else {
					mat[prod][vol] = mat[prod-1][vol];
				}
			}
		}
		
		// Backtracking to get product ID's
		int totalValue = mat[prodList.size()][toteVol];
		int vol=toteVol;
		for( int prod=prodList.size(); prod >= 0 && totalValue > 0; prod-- ) {
			if( mat[prod][vol] == mat[prod-1][vol] ) {
				continue;
			} else {
				// Add selected product and update the value and volume of remaining items.
				p = prodList.get(prod-1);
				selectedList.add( p );
				totalValue = totalValue - p.price;
				vol = vol - p.volume;
			}
		}
	}

}
