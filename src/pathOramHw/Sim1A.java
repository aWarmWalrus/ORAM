/*
 *  You can use/modify this class to test your ORAM (for correctness, not 
 *  security).
 *  
 *  You can experiment modifying this class, but we will not take it into 
 *  account (we will test your ORAM implementations on this as well as other 
 *  Jobs)
 *  
 */

package pathOramHw;

import java.util.Arrays;
import java.io.PrintWriter;

import pathOramHw.ORAMInterface.Operation;

import java.nio.ByteBuffer;
import java.util.Arrays;

import pathOramHw.ORAMInterface.Operation;

public class Sim1A {


	public static void main(String[] args) {
		int bucket_size = 4;
		int num_blocks = (int)Math.pow(2, 3);
		int stashCount[] = new int[10000];
		int max_stash = 0;
		
		//Set the Bucket size for all the buckets.
		Bucket.setMaxSize(bucket_size);
				
		//Initialize new UntrustedStorage
		UntrustedStorageInterface storage = new ServerStorageForHW();

		//Initialize a randomness generator
		RandForORAMInterface rand_gen = 
			new RandomForORAMHW();
		
		//Initialize a new Oram
		ORAMInterface oram = 
			new ORAMWithReadPathEviction(storage, rand_gen, bucket_size, 
					num_blocks);

		PrintWriter pw;
		try {
			pw = new PrintWriter("simulation1A.txt", "UTF-8");
		} catch (Exception e) {
			return;
		}

		// Warm up the ORAM
		System.out.println(" == Warming up the ORAM == ");
		for (int i = 0; i < num_blocks; i++) {
			System.out.printf("   ~~ Writing %d ~~\n", i % num_blocks);
			oram.access(Operation.WRITE, i % num_blocks, sampleData(i));
		}
		System.out.println("   >> First million done...");
		for (int i = 0; i < num_blocks; i++) {
			oram.access(Operation.WRITE, i % num_blocks, sampleData(i));
		}
		System.out.println("   >> Second million done...");
		for (int i = 0; i < num_blocks; i++) {
			oram.access(Operation.WRITE, i % num_blocks, sampleData(i));
		}
		System.out.println("   >> Warmup finished!!");

		System.out.println("   >> Getting on with 500,000 accesses");
		int stashsize= 0;
		for(int i = 0; i < 500000; i++){
			//System.out.println("====\nReading Block " + i + " from ORAM.");
			//System.out.printf("%d\n", i % num_blocks);
			oram.access(Operation.READ, i % num_blocks, null);
			stashsize = oram.getStashSize();
			if (stashsize > max_stash) max_stash = stashsize;
			if (stashsize > 10000) throw new RuntimeException("WHAT IS HAPPENING :(");
			stashCount[stashsize]++;
		}
				
		pw.println("-1, 500000");
		for (int i = 0; i <= max_stash; i++) {
			pw.printf("%d, %d\n", i, stashCount[i]);
		}
		System.out.println(" Yee");
	}
	
	private static byte[] sampleData(int i){
		ByteBuffer bb = ByteBuffer.allocate(24); 
		bb.putInt(i); 
		//System.out.println("Should expect: " + Arrays.toString(bb.array()));
		return bb.array();
	}
	
}
