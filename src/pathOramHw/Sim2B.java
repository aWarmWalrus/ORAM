package pathOramHw;

import java.util.Arrays;
import java.io.PrintWriter;

import pathOramHw.ORAMInterface.Operation;

import java.nio.ByteBuffer;
import java.util.Arrays;

import pathOramHw.ORAMInterface.Operation;

public class Sim2B {

	public static void main(String[] args) {
		int bucket_size = 2;
		int num_blocks = (int)Math.pow(2, 20);
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
			new ORAMWithDeterministicRLEviction(storage, rand_gen, bucket_size, 
					num_blocks);


		// Warm up the ORAM
		System.out.println(" == Warming up the ORAM == ");
		for (int i = 0; i < num_blocks; i++) {
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

		PrintWriter pw;
		try {
			pw = new PrintWriter("simulation2B.txt", "UTF-8");
		} catch (Exception e) {
			return;
		}			
		
		System.out.println("   >> Warmup finished!!");

		System.out.printf("   >> Getting on with 500,000,000 accesses\n"+
				"     We will record stash size every 5,000,000 accesses.\n  0.0%% complete...");
		int stashsize= 0;
		for(int i = 0; i < 500_000_000; i++){
			oram.access(Operation.READ, i % num_blocks, null);
			stashsize = oram.getStashSize();
			if (stashsize > max_stash) max_stash = stashsize;
			if (stashsize > 10_000) throw new RuntimeException("WHAT IS HAPPENING :(");
			stashCount[stashsize]++;
			if(i % 500_000 == 0){
				System.out.printf("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b%5.1f%% complete...", i / 5_000_000.);
				if (i % 5_000_000 == 0) {
					pw.println("==============");
					pw.printf("-1, %d\n", i);
					for (int j = 0; j <= max_stash; j++) {
						int count = 0;
						for (int k = max_stash; k > j; k--) count += stashCount[k];
						pw.printf("%d, %d\n", j, count);
					}
					pw.flush();
				}
			}
		}
	
		pw.println("One more for good luck, if we get here...");
		pw.println("-1, 500000");
		for (int i = 0; i <= max_stash; i++) {
			int count = 0;
			for (int j = max_stash; j > i; j--) {
				count += stashCount[j];
			}
			pw.printf("%d, %d\n", i, count);
		}
		pw.close();
		
		System.out.println("Computation completed, whoopee.");
	}
	
	private static byte[] sampleData(int i){
		ByteBuffer bb = ByteBuffer.allocate(24); 
		bb.putInt(i); 
		//System.out.println("Should expect: " + Arrays.toString(bb.array()));
		return bb.array();
	}

	private static byte[] sampleData2(int i){
		ByteBuffer bb = ByteBuffer.allocate(24); 
		bb.putInt(8, i); 
		//System.out.println("Should expect: " + Arrays.toString(bb.array()));
		return bb.array();
	}
}
