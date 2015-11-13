/*
 * You are not allowed to change this class. It implements the untrusted storage
 * for the Oram locally on the same machine.
 * You should not even have to look at this class, and can refer to the 
 * definition of UntrustedStorage for the interfaces that it uses.
 * 
 */

package pathOramHw;

public class ServerStorageForHW implements UntrustedStorageInterface{
	
	private static boolean is_initialized = false;
	private static boolean is_capacity_set = false;
	private int capacity;

	Bucket[] buckets;
	
	public ServerStorageForHW(){
		if(is_initialized == true){
			throw new RuntimeException("ONLY ONE ServerStorage CAN BE USED AT A"
				   	+ " TIME IN THIS HOMEWORK");
		}
		is_initialized  = true;
	}
	
	@Override
	public Bucket ReadBucket(int position){
		if(is_capacity_set == false){
			throw new RuntimeException("Please call setCapacity before reading" 
					+ " or writing any block");
		}
		
		if(position >= this.capacity || position < 0)
			throw new RuntimeException("You are trying to access Bucket " 
					+ position + ", but this Server contains only " + capacity 
					+ " buckets.");
		
		if(buckets[position] == null) return null;
		
		return new Bucket(buckets[position]);
	}
	
	@Override
	public void WriteBucket(int position, Bucket bucket_to_write){
		if(is_capacity_set == false){
			throw new RuntimeException("Please call setCapacity before reading "
					+ "or writing any block");
		}

		if(position >= this.capacity || position < 0)
			throw new RuntimeException("You are trying to access Bucket " 
					+ position + ", but this Server contains only " + capacity 
					+ " buckets.");
		
		buckets[position] = new Bucket(bucket_to_write);
		return;
	}



	//You are required to call setCapacity BEFORE reading or writing any 
	//buckets. You are not allowed to change the capacity after you set it.
	//You should only use ONE UntrustedStorage in your ORAM.
	@Override
	public void setCapacity(int totalNumOfBuckets) {
		if(is_capacity_set == true){
			throw new RuntimeException("Capacity of ServerStorage cannot be " 
					+ "changed");
		}
		is_capacity_set = true;
		this.capacity = totalNumOfBuckets;
		
		buckets = new Bucket[totalNumOfBuckets];
	}
}
