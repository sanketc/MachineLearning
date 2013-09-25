import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class generates the ID3 partitions.
 * @author Sanket Chandorkar
 */
public class ID3 {

	/**
	 * Attribute table
	 */
	private int[][] table;

	/**
	 * Partition list.
	 */
	private ArrayList<Partition> partitionList;
	
	/**
	 * Number of Instances
	 */
	private int noOfInstances = 0;
	
	/**
	 * Number of attributes/features
	 */
	private int noOfAttributes = 0;
	
	/**
	 * Target column index.
	 */
	private int targetColumnIndex = 0;
	
	public ID3(String dataSetFileName, String inPartitionFileName) throws Exception {

		File dataFilefile = new File(dataSetFileName);
		File partitionFile = new File(inPartitionFileName);

		/* Validate file existence */
		validateFileExistence(dataFilefile);
		validateFileExistence(partitionFile);

		/* Process files */
		processDataSetFile(dataFilefile);
		partitionList = new ArrayList<Partition>();
		processInPartitionFile(partitionFile);
	}
	
	private void processDataSetFile(File file) throws Exception{

		BufferedReader br = new BufferedReader(new FileReader(file));
		
		/* ------------  Process Header ------------- */
		
		String header = br.readLine();
		if(header == null){
			System.out.println("DataSet header is not in correct format !!");
			System.exit(Globals.SYS_FAILURE);
		}
		
		StringTokenizer stk = new StringTokenizer(header);
		if(stk.countTokens() != 2){
			System.out.println("DataSet header is not in correct format !!");
			System.exit(Globals.SYS_FAILURE);
		}
		
		noOfInstances = Integer.parseInt(stk.nextToken());
		noOfAttributes = Integer.parseInt(stk.nextToken());
		targetColumnIndex = noOfAttributes - 1;
		table = new int[noOfInstances][noOfAttributes];
		
		/* ------------  Process Body ------------- */
		
		String line = null;
		int lineCount = 0;
		while ((line = br.readLine()) != null) {
			
			stk = new StringTokenizer(line);
			if(stk.countTokens() != noOfAttributes){
				System.out.println("DataSet body is not in correct format !!");
				System.exit(Globals.SYS_FAILURE);
			}
			int columnCount = 0;
			while(stk.hasMoreTokens()){
				int tokenvalue = Integer.parseInt(stk.nextToken());
				table[lineCount][columnCount] = tokenvalue;
				columnCount++;
			}
			lineCount++;
		}
		
		if(lineCount != noOfInstances){
			System.out.println("DataSet body is not in correct format | Line Count did not match !!");
			System.exit(Globals.SYS_FAILURE);
		}
		br.close();
	}
	
	private void validateFileExistence(File file){
		if(!file.exists()){
			System.out.println("File = ' " + file.getName() + " ' does not exist !!");
			System.exit(Globals.SYS_FAILURE);
		}
	}

	private void processInPartitionFile(File file) throws Exception{

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line;
		StringTokenizer stk;
		String token;
		Partition partition = null;
		boolean firstToken;
		while ((line = br.readLine()) != null) {
			stk = new StringTokenizer(line);
			firstToken = true;
			while (stk.hasMoreTokens()) {
				token = stk.nextToken();
				if (firstToken) {
			 partition = new Partition(Integer.parseInt(token));
					 firstToken = false;
					 continue;
				 }
				 partition.addElements(Integer.parseInt(token));
			 }
			 partitionList.add(partition);
		}

		br.close();
	}
	
	public void createNewPartition() {

		Partition partitionToReplace = null;
		double e_s, e_f;
		double fValueMax = 0;
		double fValueCurr, ratio;
		int maxFeatureId = 0, finalFeatureId = 0;
		double maxFeatureGain, currFeatureGain;
		// for each partition
		for(Partition partition: partitionList){
			e_s = AnalysisAPI.entropyOverPartition(table, partition.getElements(), targetColumnIndex);
			maxFeatureGain = 0;
			//for each attribute/feature
			for(int featureIndex = 0 /* Feature or column index */ ; featureIndex < noOfAttributes - 1 ; featureIndex++ ){
				e_f = AnalysisAPI.entropyOverFeature(table, partition.getElements(), featureIndex, targetColumnIndex );
//				System.out.println(e_f);
				currFeatureGain = e_s - e_f;
				if(currFeatureGain > maxFeatureGain){
					maxFeatureGain = currFeatureGain;
					maxFeatureId = featureIndex + 1;
				}
			}
			
			// f calculation
			ratio = (double) partition.getElements().size() / (double) table.length;
			fValueCurr =  ratio * maxFeatureGain;
					
			if(fValueCurr > fValueMax){
				partitionToReplace = partition;
				fValueMax = fValueCurr;
				finalFeatureId = maxFeatureId;
			}
		}
		
		// calculate new partitions

		String newPartitionStr = "";
		ArrayList<Partition> newList = new ArrayList<>();
		for(Partition p: partitionList){
			if(p != partitionToReplace){
				newList.add(p);
				continue;
			}
			
			// create new partitions
			
			Partition p0 = new Partition(partitionToReplace.getId() * 10 + 1);
			Partition p1 = new Partition(partitionToReplace.getId() * 10 + 2);
			Partition p2 = new Partition(partitionToReplace.getId() * 10 + 3);
			for(Integer index: partitionToReplace.getElements()){
				switch(table[index-1][finalFeatureId-1]){
					case 0:	 p0.addElements(index); break;
					case 1:	 p1.addElements(index); break;
					case 2:  p2.addElements(index); break;
				}
			}
			
			// add partitions
			
			if(!p0.getElements().isEmpty()) {
				newList.add(p0);
				newPartitionStr = newPartitionStr + " " + p0.getId();
			}
			if(!p1.getElements().isEmpty()) {
				newList.add(p1);
				newPartitionStr = newPartitionStr + " " + p1.getId();
			}
			if(!p2.getElements().isEmpty()) {
				newList.add(p2);
				newPartitionStr = newPartitionStr + " " + p2.getId();
			}
			
		}
		partitionList = newList;
		
		// output message
		System.out.println("PartitionId " + partitionToReplace.getId() + " was replaced with "
				+ "partitionsID " + newPartitionStr + " using feature " + finalFeatureId);
		
	}

	public void dumpNewPartition(String outPartitionFileName) throws Exception {
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outPartitionFileName)));
		for (Partition p : partitionList) {
			pw.print(p.getId());
			for (Integer ele : p.getElements()) {
				pw.print(" " + ele.toString());
			}
			pw.println();
		}
		pw.close();
	}

}