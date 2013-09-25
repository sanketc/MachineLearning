/**
 * Starting point for the program.
 * @author Sanket Chandorkar
 */
public class Driver {

	public static void main(String[] args) throws Exception {
		
		if(args.length == 1 && args[0].equalsIgnoreCase("help")){
			Driver.helpMsg();
		}
		if(args.length != 3){
			System.out.println("Argument Missing !!");
			Driver.helpMsg();
		}
		
		String dataSetFileName = args[0];
		String inPartitionFileName = args[1];
		String outPartitionFileName = args[2];

		ID3 id3 = new ID3(dataSetFileName, inPartitionFileName);
		id3.createNewPartition();
		id3.dumpNewPartition(outPartitionFileName);
	}
	
	public static void helpMsg(){
		System.out.println();
		System.out.println("Usage:");
		System.out.println("Driver <DataSetFileName> <input_partition> <output_partition>");
		System.exit(Globals.SYS_SUCCESS);
	}
}