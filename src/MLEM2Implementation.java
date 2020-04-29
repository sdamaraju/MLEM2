import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class MLEM2Implementation {

	public static void main(String[] args) throws IOException {
		Object io[] = performIO();
		Object[] problemMetaData = FileParser.parseFileToCreateComputationalData((BufferedReader) io[0]);
		Boolean missingAttributesOrDataInconsistent = (Boolean) problemMetaData[2];
		MLEM2Algorithm algo = new MLEM2Algorithm();
		ArrayList finalRuleset = new ArrayList();
		if (missingAttributesOrDataInconsistent) {
			finalRuleset = algo.preWorkAndRunAlgorithm((Map) problemMetaData[0], (Map) problemMetaData[1],
					(Boolean) io[1]);
		} else {
			finalRuleset = algo.runAlgorithm((Map) problemMetaData[0], (Map) problemMetaData[1]);
		}
		StringBuffer finalResultString = new StringBuffer();
		for (int i = 0; i < finalRuleset.size(); i++) {
			finalResultString.append(finalRuleset.get(i).toString() + "\n");
		}
		FileOutputStream writer = (FileOutputStream) io[2];
		writer.write(finalResultString.toString().getBytes());
		writer.close();
	}

	private static Object[] performIO() throws FileNotFoundException {
		Object[] ioRequirements = new Object[3];
		System.out.println("EECS 839, Programming project \n Sai krishna Teja Damaraju \n #3028488 \n\n");
		System.out.println("#File input details#");
		System.out.println("Please enter a valid input file name with absolute path...");
		Scanner sc = new Scanner(System.in);
		File file;
		BufferedReader br = null;
		Boolean lowerApproximation = false;
		boolean inputFileSuccess = true;
		do {
			String filePath = sc.nextLine();
			file = new File(filePath);
			try {
				inputFileSuccess = true;
				br = new BufferedReader(new FileReader(file));
			} catch (IOException ex) {
				inputFileSuccess = false;
				System.out.println(
						"The path you have entered is not valid. " + filePath + "\nPlease enter valid file path");
			}
		} while (!inputFileSuccess);
		System.out.println("File loaded successfully ! \n");
		ioRequirements[0] = br;
		System.out.println("#Approximation level details#");
		System.out.println("Please type in 'lower' for lower concept approximation...");
		System.out.println(
				"Please understand that, if you do not provide 'lower' as input, and data set has missing attribute-values,\nthen algorithm defaults to upper approximation.");
		String approximationDetail = sc.nextLine();
		if (approximationDetail.equalsIgnoreCase("lower")) {
			lowerApproximation = true;
		}
		System.out.println("Considering " + (lowerApproximation ? "'LOWER Approximation'" : "'UPPER Approximation'")
				+ " based on input provided ->" + approximationDetail);
		System.out.print("\n");
		ioRequirements[1] = lowerApproximation;
		System.out.println("#Result output file details#");
		System.out.println("Please enter an output file (path) name...");
		boolean outputFileSuccess = true;
		String outputFilePath = "";
		do {
			outputFilePath = sc.nextLine();
			file = new File(outputFilePath);
			if (!file.exists()) {
				try {
					outputFileSuccess = true;
					file.createNewFile();
				} catch (IOException ex) {
					outputFileSuccess = false;
					System.out.println("Please enter a valid output filename.");
				}
			}
		} while (!outputFileSuccess);
		FileOutputStream writer = new FileOutputStream(outputFilePath);
		ioRequirements[2] = writer;
		return ioRequirements;
	}

}

//Test runs
//URL url = new
// URL("https://people.eecs.ku.edu//~jerzygb/data//iris-35-h.txt"); //
// BufferedReader br1 = new BufferedReader(new
// InputStreamReader(url.openStream()));//
// File file = new
// File("/Users/sdamaraju/Desktop/EECS839/MLEM2NotesProblem2.txt");
// BufferedReader br = new BufferedReader(new FileReader(file));
//Object[] problemMetaData =
// FileParser.parseFileToCreateComputationalData(br1);
