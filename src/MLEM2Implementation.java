import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class MLEM2Implementation {

	public static void main(String[] args) throws IOException {
		File file = new File("//Users//sdamaraju//Desktop//EECS839//EECS839MissingData.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		Object[] problemMetaData = FileParser.parseFileToCreateComputationalData(br);
		System.out.println(problemMetaData[0]);
		Boolean missingAttributes = (Boolean) problemMetaData[2];
		MLEM2Algorithm algo = new MLEM2Algorithm();
		boolean lowerApproximation = false;
		if (true/* missingAttributes */) {
			// -- AV to cases mapping
			Map<AttributeValue, TreeSet> attributeValueSet = algo.identifyCasesAndMapTo((Map) problemMetaData[0]);
			Map<AttributeValue, TreeSet> attributeValueSetCopy = algo.identifyCasesAndMapTo((Map) problemMetaData[0]);
			Map<AttributeValue, TreeSet> decisionValueSet = algo.identifyCasesAndMapTo((Map) problemMetaData[1]);
			TreeSet universe = new TreeSet();
			for (Map.Entry<AttributeValue, TreeSet> entry : decisionValueSet.entrySet()) {
				// System.out.println(entry.getKey() + " -> " + entry.getValue());
				universe.addAll(entry.getValue());
			}

			Map<Integer, List<AttributeValue>> caseToListOfAttributeValues = algo
					.buildCaseToListOfAttributeValues(attributeValueSet);

			Map<Integer, List<AttributeValue>> caseTodecisionValue = algo
					.buildCaseToListOfAttributeValues(decisionValueSet);
			// -----Reconstruct attributeValue solving the * and -
			for (Map.Entry<AttributeValue, TreeSet> entry : attributeValueSetCopy.entrySet()) {
				if (entry.getKey().value.equals("*")) {
					for (Map.Entry<AttributeValue, TreeSet> innerEntry : attributeValueSet.entrySet()) {
						if (innerEntry.getKey().attribute.equals(entry.getKey().attribute)
								&& !(innerEntry.getKey().value.contentEquals("*")
										|| innerEntry.getKey().value.contentEquals("?")
										|| innerEntry.getKey().value.contentEquals("-"))) {
							innerEntry.getValue().addAll(entry.getValue());
						}
					}
				}
				if (entry.getKey().value.equals("-")) {
					TreeSet casesWithUseOtherValue = entry.getValue();
					Iterator it = casesWithUseOtherValue.iterator();
					while (it.hasNext()) {
						int caseNum = (int) it.next();
						List decisions = caseTodecisionValue.get(caseNum);
						// generally only one decision will be there, so using list.get(0)
						TreeSet setOfCasesWithSameDecision = decisionValueSet.get(decisions.get(0));
						Iterator decisionCasesIterator = setOfCasesWithSameDecision.iterator();
						while (decisionCasesIterator.hasNext()) {
							List avs = caseToListOfAttributeValues.get(decisionCasesIterator.next());
							for (int i = 0; i < avs.size(); i++) {
								if (((AttributeValue) avs.get(i)).attribute.equals(entry.getKey().attribute)
										&& !(((AttributeValue) avs.get(i)).value.equals("*")
												|| ((AttributeValue) avs.get(i)).value.equals("-")
												|| ((AttributeValue) avs.get(i)).value.equals("?"))) {
									attributeValueSet.get(avs.get(i)).add(caseNum);
								}
							}
						}
					}
				}
			}
			// remove unnecessary */-/? from attributeValueSet
			attributeValueSetCopy = new LinkedHashMap();
			for (Map.Entry<AttributeValue, TreeSet> entry : attributeValueSet.entrySet()) {
				if (entry.getKey().value.equals("*") || entry.getKey().value.equals("?")
						|| entry.getKey().value.equals("-")) {
					// skip
				} else {
					attributeValueSetCopy.put(entry.getKey(), entry.getValue());
				}
			}
			attributeValueSet = attributeValueSetCopy;
			Map<Integer, List<AttributeValue>> reCalculatedCaseToListOfAttributeValues = algo
					.buildCaseToListOfAttributeValues(attributeValueSet);
			// ------CharacteristicSetBuilder

			// ----CharacteristicSet calculator
			TreeSet characteristicSet = new TreeSet();
			for (Map.Entry<Integer, List<AttributeValue>> entry : caseToListOfAttributeValues.entrySet()) {
				List avList = entry.getValue();
				TreeSet set = new TreeSet();
				for (int i = 0; i < avList.size(); i++) {
					AttributeValue av = (AttributeValue) avList.get(i);
					TreeSet setToUse = new TreeSet();
					if (av.value.equals("*") || av.value.equals("?")) {
						setToUse = (TreeSet) universe.clone();
					} else if (av.value.equals("-")) {
						List avs = reCalculatedCaseToListOfAttributeValues.get(entry.getKey());
						for (int j = 0; j < avs.size(); j++) {
							AttributeValue avRecalc = (AttributeValue) avs.get(j);
							if (avRecalc.attribute.equals(av.attribute)) {
								setToUse.addAll(attributeValueSet.get(avRecalc));
							}
						}
						if (setToUse.isEmpty()) {
							setToUse = universe;
						}
					} else {
						setToUse = (TreeSet) attributeValueSet.get(av).clone();
					}
					if (set.isEmpty()) {
						set = setToUse;
					} else {
						set.retainAll(setToUse);
					}
				}
				characteristicSet.add(new CharacteristicSet(entry.getKey(), set));
			}
			System.out.println("Final Characteristic Set");
			System.out.println(characteristicSet);
			LinkedHashMap caseAndCharacteristicSet = new LinkedHashMap();
			Iterator cSetIter = characteristicSet.iterator();
			while (cSetIter.hasNext()) {
				CharacteristicSet cset = (CharacteristicSet) cSetIter.next();
				caseAndCharacteristicSet.put(cset.caseNum, cset.intersectedCharacteristicSet);
			}

			// ----Calculate concept approximations..
			Map conceptAndLowerConceptApproximations = new HashMap();
			Map conceptAndUpperConceptApproximations = new HashMap();
			for (Map.Entry<AttributeValue, TreeSet> entry : decisionValueSet.entrySet()) {
				TreeSet lowerConceptApproximation = new TreeSet();
				TreeSet upperConceptApproximation = new TreeSet();
				TreeSet<Integer> concept = (TreeSet) entry.getValue().clone();
				Iterator it = concept.iterator();
				while (it.hasNext()) {
					TreeSet set = (TreeSet) caseAndCharacteristicSet.get(it.next());
					if (concept.containsAll(set)) {
						lowerConceptApproximation.addAll(set);
					}
					TreeSet conceptClone = (TreeSet) concept.clone();

					conceptClone.retainAll(set);
					if (!conceptClone.isEmpty()) {
						upperConceptApproximation.addAll(set);
					}
				}
				conceptAndLowerConceptApproximations.put(entry.getKey(), lowerConceptApproximation);
				conceptAndUpperConceptApproximations.put(entry.getKey(), upperConceptApproximation);
			}
			System.out.println("****");
			System.out.println(conceptAndLowerConceptApproximations);
			System.out.println(conceptAndUpperConceptApproximations);
			if (lowerApproximation) {
				System.out
						.println(algo.runAlgorithm(attributeValueSet, conceptAndUpperConceptApproximations, universe));
			} else {
				System.out
						.println(algo.runAlgorithm(attributeValueSet, conceptAndLowerConceptApproximations, universe));
			}
			// preWork
		} else {
			algo.runAlgorithm((Map) problemMetaData[0], (Map) problemMetaData[1]);
		}
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

// 4. inconsistent data check
// 5. Handle input for inconsistent
//		data.
// 6. Approximation calculations..
// 7. concept approximation.. characteristic sets..
