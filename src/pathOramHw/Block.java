package pathOramHw;

public class Block{
	public int leaf_id;
	public int index;
	public byte[] data;
	
	Block(int leaf_id, int index, byte[] data) {
		this.leaf_id = leaf_id;
		this.index = index;
		this.data = new byte[24];
		System.arraycopy(data, 0, this.data, 0, this.data.length);
	}
	
	Block(Block b){
		this.leaf_id = b.leaf_id;
		this.index = b.index;
		this.data = new byte[24];
		System.arraycopy(b.data, 0, this.data, 0, this.data.length);
	}

	Block(){ //dummy index
		this.leaf_id = -1;
		this.index = -1; 
		this.data = new byte[24];
	}
	
}
