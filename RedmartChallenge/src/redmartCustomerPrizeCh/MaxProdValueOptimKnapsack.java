package redmartCustomerPrizeCh;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 
 * @author Mrigendra
 * Problem statement: http://geeks.redmart.com/2015/10/26/1000000th-customer-prize-another-programming-challenge/
 * 
 * Compute the products from the input list of 20000 items with id, length, width, height, Weight, price such that:
 * 1. Any item you take must fit completely in the bag
 * 2. you can assume that if the products fit into the bag both individually and together by total volume, there is a way to pack them in
 * 3. Price value must be maximized
 * 4. Weight must be minimized without compromising on dollar value
 *
 */

class Product implements Comparable<Product>{
	String productId;
	int price, weight, volume;
	public Product(String productId, String price, int volume, String weight) {
		this.productId = productId;
		this.price = Integer.parseInt(price);
		this.weight = Integer.parseInt(weight);
		this.volume = volume;
	}
	@Override
	public int compareTo(Product ob) {
		if( volume == ob.volume ) {
			return this.weight - ob.weight;
		} else {
			return this.volume - ob.volume;
		}
	}
	public String toString() {
		return "(" + productId + ": "  + price+ ", "  + weight+ ", " + volume+ ")";
	}
}

public class MaxProdValueOptimKnapsack {

	
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


	static class MatrixCell {
		int totVal;		int weight;
		int sumProductId;		int count;
		MatrixCell() {
			totVal = 0;			weight = 0;
			sumProductId = 0;		count = 0;
		}
		public void update(Product p, int newValue, int newWeight) {
			this.sumProductId += Integer.parseInt( p.productId.trim() );
			this.totVal = newValue;
			this.weight = newWeight;
			this.count++;
		}
		public String toString() {
			return "(" + sumProductId +"  " + totVal  +"  " + weight +")";
		}
	}
	// Creating empty Matrix Cells
	static List<MatrixCell> matrix = Stream.generate( MatrixCell::new )
										.limit( toteVol+1 )
										.collect( Collectors.toList() );

	public static void main(String[] args) {
		// To compute the computation time. Start time
		long startTime = System.nanoTime();
		
		try( BufferedReader file = new BufferedReader(new FileReader(filePath)); ){
			
			String line = file.readLine();
			String[] colValues;
			int i = 0;
			
			// Read ProductId, price, length, width, height, weight
			while( line != null ) {
				colValues = line.trim().split(",");
				
				// If product doesn't fits in the tote, the method returns 0.
				int prodVol = productFits( colValues[2], colValues[3], colValues[4] );
				if( prodVol != 0 ) {
					Product p = new Product(colValues[0], colValues[1], prodVol, colValues[5]);
					fillTote( p );
				}
				
				line = file.readLine();
			}
			
			System.out.println( "Number of elements in Tote: " + matrix.get(toteVol).count );
			System.out.println( "Total of product Ids: " + matrix.get(toteVol).sumProductId );
			
			
		} catch (Exception e) {
			System.out.println("Exception when opening/reading File: " + e.getMessage());
		}
		
		System.out.println("Execution Time: " + (System.nanoTime() - startTime)/(1000000000L) + "sec");
	}
	

	/**
	 * Apply optimized knapsack Algorithm and fill the tote. 
	 * just Store sum of selected products on each cell so that backtracking is not required.
	 * @param p - product object
	 */
	private static void fillTote(Product p) {
		int j;
		// Space optimized Knapsack Algo
		for( j=toteVol; j >= p.volume; j-- ) {
			updateCell( matrix, j, p );
		}
	}

	/**
	 * Update Matrix cells by comparing Prices of products to be added.
	 * If prices are equal make the weight lesser.
	 * Use MatrixCell class to store cell objects like, total price, total weight and product IDs
	 * @param mat
	 * @param inx
	 * @param p
	 */
	private static void updateCell(List<MatrixCell> mat, int inx, Product p) {
		int prevValue = mat.get(inx).totVal;
		int prevWeight = mat.get(inx).weight;
		
		// Compute effective price if this product will be added to knapsack
		int newValue = p.price + mat.get(inx-p.volume).totVal;
		// Weight computation if effective total price of products comes out to be equal
		int newWeight = p.weight + mat.get(inx-p.volume).weight;
		
		if (prevValue < newValue ) {
			MatrixCell cell = mat.get(inx);
			cell.update(p, newValue, newWeight );
		}
		else if( prevValue == newValue && prevWeight > newWeight ) {
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

}
