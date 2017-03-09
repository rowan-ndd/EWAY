import java.io.*;
import java.util.*;

public class Evaluation {
	// calculates true positives and true negatives for left and right to calculate precision and recall
	
	public static void main(String[] args) throws FileNotFoundException {
		
		// load in the directories and store the files in array lists
		File preds = new File("Test Data/Predictions");
		File anns = new File ("Test Data/Annotations");
		ArrayList<File> predFiles = new ArrayList<File>(Arrays.asList(preds.listFiles()));
		ArrayList<File> annFiles = new ArrayList<File>(Arrays.asList(anns.listFiles()));
		
		int[] counts = new int[8];
		
		
		// go through each file, get the gold standard, then find the matching file from the predictions
		for (File f : annFiles) {
			HashMap<String, String> golds = getLateralityFromAnnotations(f);
			File predictionFile = findMatchingFile(f, predFiles);
			
			// sometimes this DS_Store file gets added, so ignore that one
			if (predictionFile.getName().contains("DS_Store")) {
				continue;
			}
			
			// compare the results of the gold standard and the predictions
			int[] foundCounts = compareResults(golds, predictionFile);
			
			// add the found counts to the overall counts
			for (int i = 0; i < counts.length; i++) {
				counts[i] += foundCounts[i];
			}
		}
		
		calculateValues(counts);
	}
	
	// method to calculate the accuracy, precision, and recall
	public static void calculateValues(int[] counts) {
		int lefttp = counts[0];
		int lefttn = counts[1];
		int leftfp = counts[2];
		int leftfn = counts[3];
		int righttp = counts[4];
		int righttn = counts[5];
		int rightfp = counts[6];
		int rightfn = counts[7];
		
		// calculate total left and right cases
		int totalLeft = lefttp + lefttn + leftfp + leftfn;
		int totalRight = righttp + righttn + rightfp + rightfn;
		
		// calculate accuracy, recall, and precision for left and right separately
		double leftAccuracy = (1.0 + lefttp + lefttn) / totalLeft * 100.0;
		double leftRecall = 100.0 * lefttp / (lefttp + leftfn);
		double leftPrecision = 100.0 * lefttp / (lefttp + leftfp);
		
		double rightAccuracy = (1.0 + righttp + righttn) / totalRight * 100.0;
		double rightPrecision = 100.0 * righttp / (righttp + rightfp);
		double rightRecall = 100.0 * righttp / (righttp + rightfn);
		
		System.out.println("Left:");
		System.out.println("Accuracy: " + leftAccuracy);
		System.out.println("Recall: " + leftRecall);
		System.out.println("Precision: " + leftPrecision);
		System.out.println("Right:");
		System.out.println("Accuracy: " + rightAccuracy);
		System.out.println("Recall: " + rightRecall);
		System.out.println("Precision: " + rightPrecision);
		
	}
	
	// need to separate into left and right to make it a binary classification task
	public static int[] compareResults(HashMap<String, String> golds, File predictionFile) throws FileNotFoundException {
		Scanner input = new Scanner(predictionFile);
		
		int[] counts = new int[8];
		
		// initialize counts
		int lefttn = 0;
		int lefttp = 0;
		int leftfn = 0;
		int leftfp = 0;
		int righttn = 0;
		int righttp = 0;
		int rightfn = 0;
		int rightfp = 0;
		
		// go through the predictions, skip blank lines
		while (input.hasNextLine()) {
			String s = input.nextLine();
			if (s.equals("")) {
				continue;
			}
			
			// break apart the key (patient id) and value (predicted laterality)
			String patientId = s.substring(0, s.indexOf(":"));
			String predictedLat = s.substring(s.indexOf(":") + 1);
			predictedLat = predictedLat.replaceAll(" ", "");
			
			// if the patient id is in the gold standards
			if (golds.containsKey(patientId)) {
				String goldLat = golds.get(patientId);
				
				// go through all cases if the prediction contains "left". remember it can contain both
				if (predictedLat.contains("left")) {
					
					// if both contain "left", then it's a true positive
					if (goldLat.contains("left")) {
						lefttp++;
					}
					
					// otherwise, if the prediction contains it but the gold doesn't, it's a false positive
					else {
						leftfp++;
					}
				}
				
				// now do the same for "right"
				if (predictedLat.contains("right")) {
					if (goldLat.contains("right")) {
						righttp++;
					}
					else {
						rightfp++;
					}
				}
				
				// now go through the cases where the prediction doesn't contain the word
				if (!predictedLat.contains("left")) {
					
					// if they both don't contain "left" then it's a true negative
					if (!goldLat.contains("left")) {
						lefttn++;
					}
					
					// otherwise it's a false negative
					else {
						leftfn++;
					}
				}
				
				// now do the same for "right"
				if (!predictedLat.contains("right")) {
					if (!goldLat.contains("right")) {
						righttn++;
					}
					else {
						rightfn++;
					}
				}
			}
			
			// otherwise, if the patient id is not in the gold standards (I don't think this should happen, but just in case)
			else {
				// if the prediction is "left" or "right" then it is a false positive
				if (predictedLat.contains("left")) {
					leftfp++;
				}
				if (predictedLat.contains("right")) {
					rightfp++;
				}
			}
		}
		
		// fill in array with counts and return it
		counts[0] = lefttp;
		counts[1] = lefttn;
		counts[2] = leftfp;
		counts[3] = leftfn;
		counts[4] = righttp;
		counts[5] = righttn;
		counts[6] = rightfp;
		counts[7] = rightfn;

		return counts;
	}
	
	// method to find the corresponding predictions file
	public static File findMatchingFile(File a, ArrayList<File> predFiles) {
		String aName = a.getName();
		String compare = aName.substring(aName.indexOf("_"), aName.lastIndexOf("_"));
		
		for (File f : predFiles) {
			String fName = f.getName();
			if (fName.contains(compare)) {
				return f;
			}
		}
		return null;
	}
	
	// method to get the laterality from the annotations files and store them in a hashmap
	public static HashMap<String, String> getLateralityFromAnnotations(File f) throws FileNotFoundException {
		HashMap<String, String> hm = new HashMap<String, String>();
		Scanner input = new Scanner(f);
		String key = "";
		String value = "";
		
		// add each patientId as a new key
		while (input.hasNextLine()) {
			String s = input.nextLine();
			
			// since the PatientId is at the end of the annotation, add the key and value when you find it
			if (s.contains("PatientId")) {
				key = s.substring(s.indexOf(":") + 2);
				if (!key.equals("")) {
					hm.put(key, value);
				}
				// then clear the key and value for the next patient
				key = "";
				value = "";
			}
			
			// otherwise get the value of the laterality category
			else {
				s = s.toLowerCase();
				if (s.contains("laterality category")) {
					value = s.substring(s.indexOf(":") + 3, s.indexOf(",") -1);
				}
			}	
		}
		
		return hm;
	}

}
