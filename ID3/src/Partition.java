import java.util.ArrayList;

/**
 * Class represents the partition
 * @author Sanket Chandorkar
 */
public class Partition {

	/**
	 * Partition ID
	 */
	private int id;
	
	/**
	 * List of indexes
	 */
	private ArrayList<Integer> elements;

	public Partition(int id) {
		this.id = id;
		this.elements = new ArrayList<Integer>();
	}

	public int getId() {
		return id;
	}

	public ArrayList<Integer> getElements() {
		return elements;
	}
	
	public void addElements(int indexValue){
		elements.add(new Integer(indexValue));
	}
}