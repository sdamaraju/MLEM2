import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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

	private void simplify(List listOfAllRules, Map<AttributeValue, TreeSet> attributeValueSet, TreeSet universe) {
		for (int i = 0; i < listOfAllRules.size(); i++) {
			RulestoGoal ruleToGoal = (RulestoGoal) listOfAllRules.get(i);
			List<AttributeValue> listOfRules = ruleToGoal.listOfRules;
			int numberOfRules = listOfAllRules.size();
			int ruleIndexThatCanBeRemoved = -1;
			for (int j = 0; j < numberOfRules; j++) {
				TreeSet set = new TreeSet();
				set.addAll(universe);
				for (int k = 0; k < numberOfRules; k++) {
					if (j == k) {
						continue;
					}
					System.out.println(listOfRules.get(k).toString());
					System.out.println(attributeValueSet.get(listOfRules.get(k).toString()));
					// *****set.retainAll(attributeValueSet.get((listOfRules.get(k).toString())));
				}
				if (ruleToGoal.conceptsCovered.containsAll(set)) {
					ruleIndexThatCanBeRemoved = j;
					break;
				}
			}
			if (ruleIndexThatCanBeRemoved >= 0) {
				ruleToGoal.listOfRules.remove(ruleIndexThatCanBeRemoved);
			}
		}

	}

	private List simplifyIntervals(List listOfAllRules) {
		for (int i = 0; i < listOfAllRules.size(); i++) {
			RulestoGoal rule = (RulestoGoal) listOfAllRules.get(i);
			Map<String, String> attributeValues = new HashMap();
			for (int k = 0; k < rule.listOfRules.size(); k++) {
				if (attributeValues.get(((AttributeValue) (rule.listOfRules.get(k))).attribute) == null) {
					attributeValues.put(((AttributeValue) (rule.listOfRules.get(k))).attribute,
							((AttributeValue) (rule.listOfRules.get(k))).value);
				} else
					attributeValues.put(((AttributeValue) (rule.listOfRules.get(k))).attribute,
							(attributeValues.get(((AttributeValue) (rule.listOfRules.get(k))).attribute) + ","
									+ ((AttributeValue) (rule.listOfRules.get(k))).value));

			}

			for (Map.Entry<String, String> entry : attributeValues.entrySet()) {
				String multipleValues[] = entry.getValue().split(",");
				List ranges = new ArrayList();
				if (multipleValues.length > 1) {
					for (int j = 0; j < multipleValues.length; j++) {
						ranges.add(new Range(multipleValues[j]));
					}
					Range simplifiedRange = RangeUtil.rangeIntersectionCalculator(ranges);
					System.out.println(simplifiedRange);
				}
			}

			System.out.println("\n\n");

		}
		return listOfAllRules;
	}
}