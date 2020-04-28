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
		boolean missingAttributes = false;
		boolean dataInconsistent = false;
		Map<String, ArrayList> mapAttributeValues = new LinkedHashMap();
		Map<String, ArrayList> mapDecisionValues = new LinkedHashMap();
		Map<String, String> inConsistencyCalc = new LinkedHashMap();
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
				String attributeString = line.substring(0, line.lastIndexOf(" "));
				String decisionString = line.substring(line.lastIndexOf(" ") + 1, line.length());
				if (inConsistencyCalc.get(attributeString) == null) {
					inConsistencyCalc.put(attributeString, decisionString);
				} else {
					String decisionAlreadyInExisitingLine = inConsistencyCalc.get(attributeString);
					if (decisionAlreadyInExisitingLine.equals(decisionString)) {
						// no-op, its okay, its not going to impact any of the calculation.
					} else {
						dataInconsistent = true;
						// if there is already a line that has a different decision and we also have the
						// current line which matches with a line already in the map and the current
						// decision is different then that implies that the data is inconsistent.
					}
				}

				values = line.split(" ");
				for (int i = 0; i < values.length; i++) {
					if (attributes.length - 1 == i) {
						(mapDecisionValues.get(attributes[i])).add(values[i]);
					} else {
						if (values[i].equalsIgnoreCase("*") || values[i].equalsIgnoreCase("-")
								|| values[i].equalsIgnoreCase("?")) {
							missingAttributes = true;
						}
						(mapAttributeValues.get(attributes[i])).add(values[i]);
					}
				}
			}
		}
		return new Object[] { mapAttributeValues, mapDecisionValues, missingAttributes || dataInconsistent };

	}

}