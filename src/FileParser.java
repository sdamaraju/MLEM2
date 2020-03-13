import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileParser {

	static Object[] parseFileToCreateComputationalData(BufferedReader br) throws IOException {
		String line = "";
		String[] attributes = {};
		String values[] = {};
		Map<String, ArrayList> mapAttributeValues = new LinkedHashMap();
		Map<String, ArrayList> mapDecisionValues = new LinkedHashMap();
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("[")) {
				line = (line.substring(1, line.length() - 1)).trim();
				attributes = line.split(" ");
				for (int i = 0; i < attributes.length; i++) {
					// tableData.add(
					if (attributes.length - 1 == i) {
						mapDecisionValues.put(attributes[i], new ArrayList());
					} else {
						mapAttributeValues.put(attributes[i], new ArrayList());
					}
				}
			} else if (line.startsWith("!")) {
				// no-op , continue to next line.
				continue;
			} else if (line.startsWith("<")) {
				//
			} else {
				values = line.split(" ");
				for (int i = 0; i < values.length; i++) {
					if (attributes.length - 1 == i) {
						(mapDecisionValues.get(attributes[i])).add(values[i]);
					} else {
						(mapAttributeValues.get(attributes[i])).add(values[i]);
					}
				}
			}
		}
		return new Object[] { mapAttributeValues, mapDecisionValues };

	}

}