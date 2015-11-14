package pathOramHw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/*
 * Name: Charles Qian
 * NetID: cq38
 */

public class ORAMWithReadPathEviction implements ORAMInterface{
	
	private int N;              // Total number of blocks outsourced to server
	private int L;              // Height of binary tree
	private final int B = 24;   // Size of blocks (in bytes)
	private int Z;              // Capacity of each bucket (in blocks)
	private HashMap<Integer,Block> S_hm; // Alternative stash data structure
	private Integer pos_map[];

	private Bucket tree[];
	private RandForORAMInterface rand;
	private UntrustedStorageInterface server;
	
	public ORAMWithReadPathEviction(UntrustedStorageInterface storage, 
			RandForORAMInterface rand_gen, int bucket_size, int num_blocks){

		Z = bucket_size;
		N = num_blocks; 
		rand = rand_gen;
		server = storage;
		L = log_2_ceil(N); // Uses fast integer log.
		pos_map = new Integer[N];

		// Initialize the position map
		rand_gen.setBound((int)Math.pow(2,L)); // Initialize on number of leaves
		for (int i = 0; i < pos_map.length; i++) {
			pos_map[i] = rand_gen.getRandomLeaf();
		}

		int capacity = (int)Math.pow(2, L+1) - 1;

		System.out.printf(" >> INITIALIZING ORAM...\n");
		// Initialize server
		server.setCapacity(capacity);
		for (int j = 0; j < capacity; j++) {
			Bucket new_b = new Bucket();
			server.WriteBucket(j, new_b);
		}

		// Initialize stash
		S_hm = new HashMap<Integer, Block>();

		// Debugging printouts
		System.out.printf(
				  "    Number of blocks: %d\n" 
				+ "    Number of levels: %d\n"
				+ "    Number of leaves: %d\n"
				+ "    Capacity : %d\n"
				+ "    Pos_map_head : [%d, %d, %d, %d ...]\n",
				this.N, this.L, this.pos_map.length, capacity,
				this.pos_map[0], this.pos_map[1],this.pos_map[2],this.pos_map[3]);
	}


	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {

		if (blockIndex >= N) {
			throw new RuntimeException(String.format(
						"ORAM Out of Bounds -- Tried to access block %d, but max index is %d", blockIndex, N-1));
		}

		int x = pos_map[blockIndex];
		pos_map[blockIndex] = rand.getRandomLeaf();

		int node = x + (int)Math.pow(2, L); // Leaf_id to Node pos by adding 2^L

		// Optimized traversal of the tree, instead of calling P at L levels.
		do {
			ServerReadBucket(node); // Read meaningful blocks at node to stash
			node = node / 2;
		} while (node > 0);

		// Get the block from the stash
		Block d = S_hm.get(blockIndex);

		// Write the block to the stash.
		if (op == Operation.WRITE) {
			// If d isn't in the tree, or in the stash:
			if (d == null) {
				d = new Block(pos_map[blockIndex], blockIndex, newdata);
				S_hm.put(blockIndex, d);
			} else {
				System.arraycopy(newdata, 0, d.data, 0, 24);
			}
		} else if (d == null) {
			return new byte[24];
		} // Else is a read, and d not null

		d.leaf_id = pos_map[blockIndex];

		// Place as much of the stash into the server as possible
		node = x + (int)Math.pow(2,L); // restore node to be the node val of x
		do {
			// node is essentially the value P(x,l)
			// where l = log_2_floor(node)
			int l = log_2_floor(node);
			int count = 0;
			Bucket acc = new Bucket();
			for (Integer ind : S_hm.keySet()) {
				Block b = S_hm.get(ind);
				if (P(b.leaf_id, l) == node) {
					acc.addBlock(new Block(b));
					count++;
				}
				if (count == Z) break;
			}
			// remove each block from the stash,
			for (Block b : acc.getBlocks()) {
				if (b.index != -1) {
					if (S_hm.remove(b.index) == null) 
						throw new RuntimeException("DUDEEE");
				}
			}
			ServerWriteBucket(node, acc);
			node = node / 2;
		} while (node > 0);

		return d.data;
	}


	@Override
	public int P(int leaf, int level) {
		if (level < 0 || level > L) 
			throw new RuntimeException("Illegal Arguments to P");
		int node = leaf + (int)Math.pow(2,L);
		while (node > 0) {
			if (log_2_floor(node) == level) {
				//System.out.printf(" ~~ P(%d, %d) = %d\n", leaf, level, node);
				return node;
			}
			node = node / 2;
		}
		System.out.printf(" P(%d, %d) = %d?\n", leaf, level, node);
		throw new RuntimeException("What just happened???");
	}


	// Make sure to return a COPY of the position map...
	@Override
	public Integer[] getPositionMap() {
		Integer new_pm[] = new Integer[N];
		for (int i = 0; i < N; i++) {
			new_pm[i] = pos_map[i];
		}
		return new_pm;
	}


	@Override
	public ArrayList<Block> getStash() {
		return new ArrayList<Block>(S_hm.values());
	}


	@Override
	public int getStashSize() {
		return S_hm.size();
	}

	@Override
	public int getNumLeaves() {
		return (int)Math.pow(2, L);
	}


	@Override
	public int getNumLevels() {
		return L;
	}


	@Override
	public int getNumBlocks() {
		return N; 
	}


	@Override
	public int getNumBuckets() {
		return (int)Math.pow(2,L+1) - 1;
	}

	//You may add helper methods.
	private int log_2_ceil(int n) {
		return 32 - Integer.numberOfLeadingZeros(n - 1);
	}

	private int log_2_floor(int n) {
		return 31 - Integer.numberOfLeadingZeros(n);
	}

	// Helper function to provide layer of indirection between actual logic 
	// and the server reads, and filters out dummy blocks, and adds it to 
	// the stash
	private void ServerReadBucket(int node) {
		// Assumes node is a tree node, aka, tree is 1 indexed 
		Bucket node_bucket = server.ReadBucket(node - 1);
		for (Block b : node_bucket.getBlocks()) {
			// If it's not a dummy block, add it to the stash
			if (b.leaf_id != -1 && b.index != -1) {
				//System.out.printf("   > Removing index %d "
				//		+ "leaf %d level %d from tree\n",
				//		b.index, b.leaf_id, log_2_floor(node));
				S_hm.put(b.index, b);
			}
		}
		return;
	}

	private void ServerWriteBucket(int node, Bucket to_write) {
		server.WriteBucket(node - 1, to_write);
	}
}
