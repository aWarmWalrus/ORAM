package pathOramHw;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Name: Charles Qian
 * NetID: cq38
 */

public class ORAMWithDeterministicRLEviction implements ORAMInterface{

	/**
	 * TODO add necessary variables 
	 */
	private int G;
	private int N;
	private int L;
	private final int B = 24;
	private int Z;
	private HashMap<Integer,Block> S_hm;
	private Integer pos_map[];

	private Bucket tree[];
	private RandForORAMInterface rand;
	private UntrustedStorageInterface server;
		
	public ORAMWithDeterministicRLEviction(UntrustedStorageInterface storage, 
			RandForORAMInterface rand_gen, int bucket_size, int num_blocks){
		G = 0;
		Z = bucket_size;
		N = num_blocks;
		rand = rand_gen;
		server = storage;
		L = log_2_ceil(N);
		pos_map = new Integer[N]; //(int) Math.pow(2, L) ];

		// Initialize the position map
		rand_gen.setBound((int) Math.pow(2, L));
		for (int i = 0; i < pos_map.length; i++) {
			pos_map[i] = rand_gen.getRandomLeaf();
		}

		int capacity = (int)Math.pow(2,L+1) - 1;

		System.out.printf(" >> Initializing ORAM!\n");
		
		server.setCapacity(capacity);
		for (int j = 0; j < capacity; j++) {
			server.WriteBucket(j, new Bucket());
		}

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
		
	// Returns a COPY of the position map
	@Override
	public Integer[] getPositionMap(){
		Integer new_pm[] = new Integer[N];
		for(int i = 0; i < N; i++) {
			new_pm[i] = pos_map[i];
		}
		return new_pm;
	}


	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {

		// Check argument legality
		if (blockIndex >= N) {
			throw new RuntimeException(String.format(
				"ORAM Out of Bounds--Tried to access block %d, max index is %d", 
				blockIndex, N-1));
		}

		// Lines 2 - 3
		int x = pos_map[blockIndex];
		pos_map[blockIndex] = rand.getRandomLeaf();

		int node = x + (int)Math.pow(2, L); // Leaf's node value is leaf value
											// plus 2^L

		// Lines 4 - 11 of pseudo code
		ServerFindBlock(node, blockIndex);

		// Lines 12 - 16
		Block d = S_hm.get(blockIndex);
		// If we're writing block to stash
		if (op == Operation.WRITE) {
			// Event when d isn't in the tree nor in the stash.
			if (d == null) {
				d = new Block(pos_map[blockIndex], blockIndex, newdata);
				S_hm.put(blockIndex, d);
			} else {
				System.arraycopy(newdata, 0, d.data, 0, 24);
			}
		} else if (d == null) {
			return new byte[24]; // Read for uninitialized...return 0 array.
		} // Else, it's a read and d is not null

		d.leaf_id = pos_map[blockIndex];

		for (int j = 0; j < 2; j++) {
			DeterministicEvict();
		}

		return d.data;
	}


	@Override
	public int P(int leaf, int level) {
		if (level < 0 || level > L)
			throw new RuntimeException("Illegal Arguments to P");
		int node = leaf + (int)Math.pow(2,L);
		while (node > 0) {
			if (log_2_floor(node) == level) return node;
			node = node / 2;
		}
		throw new RuntimeException("Impossible Case");
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
		return (int)Math.pow(2,L);
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
	
	public int getGlobalG() {
		return G;
	}
	
	// Reverse an integer using really awesomely fast bitwise operations
	public int ReverseBits(int g, int bits_length)
	{
		int rem, limit, mask;
		int acc = 0;
		limit = (int)Math.pow(2, bits_length);
		mask = limit - 1;
		g = g & mask;
		while (bits_length > 0) {
			rem = g & 0x1;
			g = g >>> 1;
			acc = (acc << 1) | rem;
			bits_length--;
		}
		return acc;
	}

	//You may add helper methods.
	
	/* Performs the log of n, with ceiling. */
	private int log_2_ceil(int n) {
		return 32 - Integer.numberOfLeadingZeros(n - 1);
	}

	/* Performs the log of n, with floor. */
	private int log_2_floor(int n) {
		return 31 - Integer.numberOfLeadingZeros(n);
	}

	// Helper function to handle the deterministic evictions and restores.
	private void DeterministicEvict() {
		int leaf = ReverseBits(G++, L);
		int node = leaf + (int) Math.pow(2,L);

		// Traverse tree from node to root, reading meaningful blocks
		// into the stash. (lines 20 - 22)
		do{
			ServerReadBucket(node);
			node = node / 2;
		} while (node > 0);

		node = leaf + (int)Math.pow(2,L); // restore value of node
		//System.out.println(" ==================== ");
		do {
			int l = log_2_floor(node);
			int count = 0;
			Bucket acc = new Bucket();
			for (Integer ind : S_hm.keySet()) {
				Block b = S_hm.get(ind);
				if (P(b.leaf_id, l) == node) {
					acc.addBlock(new Block(b));
					count++;
				}
				// Check to see if the bucket is full.
				if (count == Z) {
					//System.out.println("Bucket full!\n");
					break;
				}
			}
			// remove each block from the stash,
			for (Block b : acc.getBlocks()) {
				if (b.index != -1) {
					if (S_hm.remove(b.index) == null) 
						throw new RuntimeException("DUDEEE");
				}
			}
			//System.out.printf(" Stash size: %d\n", S_hm.size());
			server.WriteBucket(node - 1, acc);
			node = node / 2;
		} while (node > 0);

		return;
	}	

	// Helper function to provide layer of indirection between actual logic 
	// and the server reads. Extracts desired block, and adds to stash.
	private void ServerFindBlock(int node, int blockIndex) {
		boolean found = false;
		//System.out.printf(" >> Looking for block %d (along path to %d)...", 
				//blockIndex, node);
		do {
			// Assumes node is a tree node, aka, tree is 1 indexed 
			Bucket node_bucket = server.ReadBucket(node - 1);
			for (Block b : node_bucket.getBlocks()) {
				// If it's not a dummy block, add it to the stash
				if (b.index == blockIndex) {
					S_hm.put(b.index, new Block(b));
					b.leaf_id = -1;
					b.index = -1;
					b.data = new byte[24];
					found = true;
					break;
				}
			}
			server.WriteBucket(node - 1, node_bucket);
			node = node / 2;
		} while (node > 0 && !found);
		//System.out.printf("%s", found ? 
		//		"and found it! :D\n" : "but didn't find it :(\n");
		return;
	}

	// Helper function to provide layer of indirection between actual logic 
	// and the server reads, and filters out dummy blocks, and adds it to 
	// the stash
	private void ServerReadBucket(int node) {
		// Assumes node is a tree node, aka, tree is 1 indexed 
		Bucket node_bucket = server.ReadBucket(node - 1);
		for (Block b : node_bucket.getBlocks()) {
			// If it's not a dummy block, add it to the stash
			if (b.leaf_id != -1 && b.index != -1) S_hm.put(b.index, b);
		}
		return;
	}
	
}
