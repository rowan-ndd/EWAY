import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;


public class LateralityPredictor {
	
	// simple word counter that counts occurrences of left and right and determines laterality based on that

	public static void main(String[] args) throws FileNotFoundException {
		// read in all files in the JSON folder, put them in an array list and predict left or right
		File dir = new File("Test Data/JSON");
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(dir.listFiles()));
		
		for (File f : files) {
			predictLeftOrRight(f);
		}
	}
	
	public static void predictLeftOrRight(File f) throws FileNotFoundException {
		
		String fileName = f.getName();
		String extension = getFileExtension(f);
		String outputName = f.getName().substring(0, fileName.indexOf(extension) -1) + "_prediction.txt";
		PrintStream ps = new PrintStream(new File(outputName));
		
		
		Scanner input = new Scanner(f);
		String patientId = "";
		String text = "";
		
		// go through the file line by line to find each patient
		while (input.hasNextLine()) {
			String s = input.nextLine();
			
			if (s.contains("\t\"PAT")) {
				String laterality = getLaterality(text);
				int[] counts = getLeftAndRightCounts(text);
				outputLeftOrRight(patientId, counts, laterality, ps);
				//outputLeftOrRight(patientId, text, ps);
				patientId = s.substring(s.indexOf("\""), s.indexOf(":") +1);
				//ps.print(patientId);
				text = "";
			}
			
			else {
				text += s;
				//s = s.toLowerCase();
			}
		}
		String laterality = getLaterality(text);
		int[] counts = getLeftAndRightCounts(text);
		outputLeftOrRight(patientId, counts, laterality, ps);
		//outputLeftOrRight(patientId, text, ps);
	}
	
	// method to go through the text for each patient and see if the laterality is indicated in there
	public static String getLaterality(String text) {
		
		// go through the text, make it all lower case for easier matching and split it on whitespace
		text = text.toLowerCase();
		String[] split = text.split("\\s+");
		String laterality = "";
		
		// look for the phrase "laterality:" then pull the next word, assuming it's the laterality
		for (int i = 0; i < split.length; i++) {
			String s = split[i];
			if (s.contains("laterality:")) {
				laterality = split[i+1];
				break;
			}
		}
		
		// if the laterality is left or right, return it. Otherwise it's "unilateral", which is not helpful
		if (laterality.equals("left") || laterality.equals("right")) {
			return laterality;
		}
		
		// or maybe we want to know if it's unilateral so we don't return left and right in that case
		else {
			return "";
		}
		
	}
	
	// method to go through the text for each patient and get the counts of "left" and "right"
	public static int[] getLeftAndRightCounts(String text) {
		// create int array for counts, first item is left, second is right
		int[] counts = new int[2];
		
		// then go through the text, make it all lower case for easier matching and split it on whitespace
		text = text.toLowerCase();
		String[] split = text.split("\\s+");
		int leftCount = 0;
		int rightCount = 0;
				
		// increment counts appropriately
		for (String s : split) {
			if (s.contains("left")) {
				leftCount++;
			}
			if (s.contains("right")) {
				rightCount++;
			}
		}
		
		// fill in the array and return it
		counts[0] = leftCount;
		counts[1] = rightCount;
		return counts;
				
	}
	
	public static void outputLeftOrRight(String patientId, int[] counts, String laterality, PrintStream ps) {
		
		// first print the patient id
		ps.print(patientId);
		
		// get left and right counts from the array
		int leftCount = counts[0];
		int rightCount = counts[1];
		
		// laterality found in report takes precedence
		if (laterality.equals("left")) {
			ps.println(" left");
		}
		
		else if (laterality.equals("right")) {
			ps.println(" right");
		}
		
		// if "left" occurs more, then assume it's left
		else if (leftCount > rightCount) {
			ps.println(" left");
		}
		// if "right" occurs more, assume it's right
		else if (rightCount > leftCount) {
			ps.println(" right");
		}
		// if they both occur equally and are not zero, assume it's both
		else if (rightCount != 0 && leftCount != 0 && rightCount == leftCount) {
			ps.println( " right & left");
		}
		else {
			ps.println();
		}
	}

	// method to get the extension of a file, used to output file names appropriately
	public static String getFileExtension(File f) {
		String fileName = f.getName();
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		return extension;
	}

}
