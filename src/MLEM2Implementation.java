import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class MLEM2Implementation {

	public static void main(String[] args) throws IOException {
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
		System.out.println("#Result output file details#");
		System.out.println("Please enter an output file (path) name...");
		String outputFilePath = sc.nextLine();
		boolean outputFileSuccess = true;
		do {
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

		Object[] problemMetaData = FileParser.parseFileToCreateComputationalData(br);
		Boolean missingAttributes = (Boolean) problemMetaData[2];
		MLEM2Algorithm algo = new MLEM2Algorithm();

		ArrayList finalRuleset = new ArrayList();
		if (missingAttributes) {
			finalRuleset = algo.preWorkAndRunAlgorithm((Map) problemMetaData[0], (Map) problemMetaData[1],
					lowerApproximation);
		} else {
			finalRuleset = algo.runAlgorithm((Map) problemMetaData[0], (Map) problemMetaData[1]);
		}
		String finalResultString = finalRuleset.toString();
		writer.write(finalResultString.substring(1, finalResultString.length() - 1).getBytes());
		writer.close();
	}

}

// Things pending..
// 1. cutpoints calculation.       done..
// 2. final rules simplification   done..
// 3. interval simplification..    done..

//*** minor issue, decimals taking too much unnecessary precision.. solved
//major issue solved. missed goal = goal intersection [t] done..
//*** minor simplification issue, call simplifyIntervals and then simplify rules.. but once interval is simplified, cant get the attribute value pairs as cutpoints are solved.. handle this

// otherwise tested on multiple data, looks fine.

// 4. inconsistent data check ....mostly not required
// 5. Handle input for inconsistent DONE
//		data.
// 6. Approximation calculations.. DONE
// 7. concept approximation.. characteristic sets.. DONE

//Pending :
//Rigorous testing on exam problem hw problem and all classwork problems and sample problems
//Handle IO as per the instructions
// Clean the code and optimize wherevr possible.
/// Comments wherver necessary
// readme file

// URL url = new
// URL("https://people.eecs.ku.edu//~jerzygb//data//m-global.txt");
// URL url = new
// URL("https://people.eecs.ku.edu//~jerzygb//data//austr-aca-35.txt");
// URL url = new
// URL("https://people.eecs.ku.edu//~jerzygb//data//echo-35-s.txt");
// URL url = new
// URL("https://people.eecs.ku.edu//~jerzygb//data//iris-35-qm.txt");
// URL url = new
// URL("https://people.eecs.ku.edu//~jerzygb/data//iris-35-h.txt");
// BufferedReader br1 = new BufferedReader(new
// InputStreamReader(url.openStream()));

// File file = new
// File("//Users//sdamaraju//Desktop//EECS839//EECS839MissingData.txt");
// File file = new
// File("//Users//sdamaraju//Desktop//EECS839//NotesProblem1.txt");
// File file = new
// File("/Users/sdamaraju/Desktop/EECS839/MLEM2NotesProblem2.txt");
// BufferedReader br = new BufferedReader(new FileReader(file));
