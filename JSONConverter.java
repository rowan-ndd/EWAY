import java.io.*;
import java.util.*;

// still working to get HashMap version working as expected, but this one mostly works
public class JSONConverter {

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length != 2) {
			System.out.println("Usage: enter input directory and output directory. Neither directory can have spaces in the name.");
		}
		
		// read input directory and output directory from command line
		String inputDirName = args[0];
		File directory = new File(inputDirName);
		String outputDirName = args[1];
		
		ArrayList<File> files; 
		
		// only works for directories or a single text file
		if (directory.isDirectory()) {
			files = new ArrayList<File>(Arrays.asList(directory.listFiles()));
		}
		else if (getFileExtension(directory).equals("txt")) {
			files = new ArrayList<File>();
			files.add(directory);
		}
		else {
			throw new IllegalArgumentException("Expecting a directory or text file");
		}
		
		for (File f : files) {
			convertFileToJson(f);
			String extension = getFileExtension(f);
			String fileName = f.getName();
			String outputFile = "Temp/" + fileName.substring(0, fileName.indexOf(extension)) + "temp";
			addCommas(new File(outputFile), outputDirName);
		}
		
	}
	
	// get all txt files in a directory
	public static ArrayList<File> getAllFiles (File directory) {
		ArrayList<File> files;
		files = new ArrayList<File>(Arrays.asList(directory.listFiles()));
		
		// remove anything that is a folder or not a txt file
		for (int i = 0; i < files.size(); i++) {
			File f = files.get(i);
			String extension = getFileExtension(f);
			if (f.isDirectory() || !extension.equals("txt")) {
				files.remove(i);
				i--;
			}
		}
		
		return files;
	}
	
	// get the extension of a file
	public static String getFileExtension(File f) {
		String fileName = f.getName();
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		return extension;
	}
	
	// convert the file to json
	public static void convertFileToJson(File f) throws FileNotFoundException {
		String fileName = f.getName();
		String extension = getFileExtension(f);
		
		File tempDir = createDirectory("Temp");
		
		// create new file with same name but the json extension. 
		String outputName = tempDir + "/"  + f.getName().substring(0, fileName.indexOf(extension)) + "temp";
		//String outputName2 = f.getName().substring(0, fileName.indexOf(extension)) + "2." + "json";
		PrintStream ps = new PrintStream(new File(outputName));

		//PrintStream ps2 = new PrintStream(new File(outputName));
		ps.println("{"); // print first line
		Scanner input = new Scanner(f);

		if (!input.hasNextLine()) { // if the file is blank
			input.close();
			throw new IllegalArgumentException("File " + fileName + " is blank");
		}
		
		// pretty much it is a fencepost in a fencepost, so this should handle that
		
		// go through until you find the first patient id tag and print that out
		while (input.hasNextLine()) {
			String s = input.nextLine();
			
			// if it is a patient id, print it out
			if (s.equals("<PATIENT_DISPLAY_ID>")) {
				String patientId = input.nextLine();
				ps.println("\t" + "\"" + patientId + "\"" + ": {");
				s = input.nextLine();
				break;
			}
			else {
				continue;
			}
		}
		
		// then go through the rest of the file
		while (input.hasNextLine()) {
			String s = input.nextLine();
			
			// if it is a patient id, print out the bracket and comma from the line before then the id
			if (s.equals("<PATIENT_DISPLAY_ID>")) {
				String patientId = input.nextLine();
				ps.println("\t" + "},");
				ps.println("\t" + "\"" + patientId + "\"" + ": {");
			}
			
			// if it is an opening tag
			else if (s.contains("<") && s.contains(">") && s.contains("_") && !s.contains("/")) {
				String tag = s.substring(1, s.length() - 1);
				String value = "";
				
				// go through the next lines until you hit the next opening tag
				while (input.hasNextLine()) {
					String s2 = input.nextLine();
					if (s2.contains(tag)) {
						break;
					}
					else {
						s2 = s2.replace("[", "").replace("]", "").replace("\\", "").replace("\"", "'").replace("{", "").replace("}", "");
						value += s2 + " ";
					}
				}
				
				// if the value for the tag is empty, then don't print it
				if (value.isEmpty() || value.equals("") || value.equals(" ")) { 
					continue;
				}
				
				// otherwise print the tag and value pair
				else {
					value = value.trim();
					ps.println("\t\t" + "\"" + tag + "\"" + ": " + "\"" + value + "\"");
				}
			}
			
			// otherwise, it's some text between a closing of one tag and opening of another, so ignore it
			else {
				continue;
			}
			
		}
		
		// print closing braces
		ps.println("\t}");
		ps.println("}");
		
		ps.close();
		input.close();
	}
	
	// method to create a new directory if it doesn't exist
		public static File createDirectory(String dirName) {
			File dir = new File(dirName);
			if (!dir.exists()) {
				if (dir.mkdir()) {
					
				}
				else {
					System.out.println("Failed to create temp directory");
				}
			}
			
			return dir;
		}
	
	// probably not the best way to do it, but got it adding the commas in the right way
	public static void addCommas(File f, String outputDir) throws FileNotFoundException {
		Scanner input = new Scanner(f);
		String extension = getFileExtension(f);
		String name = f.getName();
		String output = name.substring(0, name.indexOf(extension));
		File jsonDir = createDirectory(outputDir);
		PrintStream ps = new PrintStream(jsonDir + "/" + output + "json");
		ArrayList<String> lines = new ArrayList<String>();
		while (input.hasNextLine()) {
			String s = input.nextLine();
			lines.add(s);
		}
		
		for (int i = 0; i < lines.size() -1; i++) {
			String current = lines.get(i);
			String next = lines.get(i + 1);
			
			if (!current.contains("{") && !current.contains("}") && !next.contains("}")) {
				current += ",";
			}
			ps.println(current);
		}
		ps.println(lines.get(lines.size() - 1));
//		ps.close();
//		input.close();
	}
	
	

}




