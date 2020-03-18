import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class MLEM2Implementation {

	public static void main(String[] args) throws IOException {
		File file = new File("//Users//sdamaraju//Desktop//EECS839Sample 2 copy.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		Object[] problemMetaData = FileParser.parseFileToCreateComputationalData(br);
		// Evaluate the problemMetaData 0 the attributevalues for numbers to calculate
		// the cutpoints.
		System.out.println(problemMetaData[0]);
		MLEM2Algorithm algo = new MLEM2Algorithm();
		algo.runAlgorithm((Map) problemMetaData[0], (Map) problemMetaData[1]);

	}

}

// Things pending..
// 1. cutpoints calculation.       done..
// 2. final rules simplification   done..
// 3. interval simplification..    done..

//major issue solved. missed goal = goal intersection [t] done..
//*** minor simplification issue, call simplifyIntervals and then simplify rules.. but once interval is simplified, cant get the attribute value pairs as cutpoints are solved.. handle this
//*** minor issue, decimals taking too much unnecessary precision..
// otherwise tested on multiple data, looks fine.

// 4. inconsistent data check
// 5. Handle input for inconsistent
//		data.
// 6. Approximation calculations..
// 7. concept approximation.. characteristic sets..
