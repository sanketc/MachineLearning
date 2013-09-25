import java.util.ArrayList;

/**
 * Class contains analysis APIs for entropy calculation.
 * 
 * @author Sanket Chandorkar
 */
public class AnalysisAPI {

	/**
	 *  Returns Entropy over one feature: Eg: Entropy(Target|feature)
	 */
	public static double entropyOverFeature(int array[][], ArrayList<Integer> indexList, 
			int featureIndex, int targetColumnIndex){
		
		double[] proArray = AnalysisAPI.probabilityArray(array, indexList, featureIndex);
		double result = 
				proArray[0]*entropyOverValue(array, indexList, featureIndex, 0 /*featureValue*/, targetColumnIndex) +
				proArray[1]*entropyOverValue(array, indexList, featureIndex, 1 /*featureValue*/, targetColumnIndex) +
				proArray[2]*entropyOverValue(array, indexList, featureIndex, 2 /*featureValue*/, targetColumnIndex);
		
		return result;
	}

	/**
	 * Entropy(Target|featureValue)
	 */
	private static double entropyOverValue(int array[][], ArrayList<Integer> indexList, 
			int featureIndex, int featureValue, int targetColumnIndex){
		
		int c0 = 0;
		int c1 = 0;
		int countOccurances = 0;
		
		/* Calculate counts */
		for(Integer index: indexList) {
			if(array[index - 1][featureIndex] != featureValue)
				continue;
			countOccurances++;
			switch(array[index - 1][targetColumnIndex]){
				case 0: c0++; break;
				case 1: c1++; break;
				default: throw new IllegalArgumentException("entropyOverPartition");
			}
		}
		
		if(countOccurances == 0)	/* Added to handle zero occurances */
			return 0;
		
		/* Calculate probabilities and entropy */
		double p0 = (double)c0/(double) countOccurances;
		double p1 = (double)c1/(double) countOccurances;
		return AnalysisAPI.entropy(p0, p1);
	}

	/**
	 * To calculate S
	 */
	public static double entropyOverPartition(int array[][], ArrayList<Integer> indexList, int targetColumnIndex){
		
		int c0 = 0;
		int c1 = 0;
		
		/* Calculate counts */
		for(Integer index: indexList) {
			switch(array[index - 1][targetColumnIndex]){
				case 0: c0++; break;
				case 1: c1++; break;
				default: throw new IllegalArgumentException("entropyOverPartition");
			}
		}
		
		/* Calculate probabilities and entropy */
		double p0 = (double)c0/(double)indexList.size();
		double p1 = (double)c1/(double)indexList.size();
		return AnalysisAPI.entropy(p0, p1);
	}
	
	/**
	 * Returns entropy
	 * @param p1 Probability 1
	 * @param p2 Probability 2
	 * @return Entropy
	 */
	public static double entropy(final double p1, final double p2){
		double result1, result2;
		if(p1 == 0)
			result1 = 0;
		else
			result1 = p1 * AnalysisAPI.log2(1/p1);
		if(p2 == 0)
			result2 = 0;
		else
			result2 = p2 * AnalysisAPI.log2(1/p2);
		return result1 + result2;
	}
	
	/**
	 * Return log to the base 2.
	 * 
	 * @param number Input number
	 * @return Log to the base 2
	 */
	private static double log2(final double number) {
		if (number <= 0) {
			throw new IllegalArgumentException("log2(" + number + ")");
		}
		return (Math.log10(number) / Math.log10(2 /*base*/ ));
	}	
	
	/**
	 * Calculate probability array.
	 */
	public static double[] probabilityArray(int array[][], ArrayList<Integer> indexList, int attributeColumn){
		
		int c0 = 0;
		int c1 = 0;
		int c2 = 0;
		
		/* Calculate counts */
		for(Integer index: indexList){
			switch(array[index - 1][attributeColumn]){
				case 0: c0++; break;
				case 1: c1++; break;
				case 2: c2++; break;
				default: throw new IllegalArgumentException("probabilityArray");
			}
		}
	
		/* Calculate probabilities */
		int size = indexList.size();
		double result[] = new double[3];
		result[0] = (double)c0/(double)size;
		result[1] = (double)c1/(double)size;
		result[2] = (double)c2/(double)size;
		return result;
	}	

}