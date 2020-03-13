import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MLEM2Algorithm {

	private TreeSet calculateCutPoints(List<String> listOfValuesforTheAttribute, TreeSet set) {
		Iterator iter = set.iterator();
		double sum = 0.0;
		Double currentValue;
		TreeSet cutPoints = new TreeSet(); // create cut-points by taking average of consecutive numbers.
		while (iter.hasNext()) {
			currentValue = Double.parseDouble((String) iter.next());
			if (cutPoints.size() == 0 && sum == 0) {
				sum = sum + currentValue;
			} else {
				sum = sum + currentValue;
				cutPoints.add(sum / 2);
				sum = currentValue;
			}
		}
		// return the cut-points
		return cutPoints;
	}

	private TreeSet calculateRangeSets(TreeSet cutPoints, TreeSet set) {
		Iterator it = cutPoints.iterator(); // use the range thing here....
		Double currValue;
		TreeSet<Range> setOfAllRanges = new TreeSet<Range>(); // Using Range class to create the bracket.
		String firstValue = (String) set.first();
		String lastValue = (String) set.last();
		while (it.hasNext()) {
			currValue = (Double) it.next();
			setOfAllRanges.add(new Range(Double.parseDouble(firstValue), currValue));
			setOfAllRanges.add(new Range(currValue, Double.parseDouble(lastValue)));
		}
		return setOfAllRanges;
	}

	private TreeSet calculateTheIntersectionSet(Map<AttributeValue, TreeSet> attributeValueSet,
			TreeSet currentDecision) {
		int i = 1; // order of the intersection logic.
		TreeSet<IntersectionSelection> avDvIntersectionSets = new TreeSet<IntersectionSelection>();
		// set to contain the intersection of attribute values with decision,value.
		for (Map.Entry<AttributeValue, TreeSet> entryAV : attributeValueSet.entrySet()) {
			// run the intersection of each attribute value set with current decision
			// and insert the result into the set, its sorting will identify which rule to
			// be picked first.
			TreeSet<?> tempSet = (TreeSet<?>) entryAV.getValue().clone(); // tempSet needed to alter the set // during
			// the intersection logic
			tempSet.retainAll(currentDecision); // actual intersection logic.
			avDvIntersectionSets.add(new IntersectionSelection(entryAV.getKey(), tempSet, entryAV.getValue(),
					entryAV.getValue().size(), i++));
		}
		return avDvIntersectionSets;
	}

	// Used to identify cases for each attribute and value.
	private Map identifyCasesAndMapTo(Map<String, ArrayList> attributeValues) {
		Map<AttributeValue, TreeSet> attributeValueSet = new LinkedHashMap();
		Set<AttributeValue> tempAttributeValue = new HashSet<AttributeValue>();
		for (Map.Entry<String, ArrayList> entry : attributeValues.entrySet()) {
			String attributeName = entry.getKey(); // gets the unique attribute name.
			List<String> listOfValuesforTheAttribute = entry.getValue();
			if (isCutPointsNecessary(listOfValuesforTheAttribute)) {
				TreeSet set = new TreeSet(); // add all elements to Set (increasing order of values.)
				for (int i = 0; i < listOfValuesforTheAttribute.size(); i++) {
					set.add(listOfValuesforTheAttribute.get(i));
				}
				TreeSet cutPoints = calculateCutPoints(listOfValuesforTheAttribute, set);
				TreeSet rangeSets = calculateRangeSets(cutPoints, set);
				for (int i = 0; i < listOfValuesforTheAttribute.size(); i++) {
					Iterator rangesIterator = rangeSets.iterator();
					while (rangesIterator.hasNext()) {
						Range r = (Range) rangesIterator.next();
						if (r.contains(Double.parseDouble(listOfValuesforTheAttribute.get(i)))) {
							AttributeValue av = new AttributeValue(attributeName, r.toString());
							if (!tempAttributeValue.contains(av)) {
								tempAttributeValue.add(av);
								TreeSet test = new TreeSet();
								test.add(i + 1); // (maintains the list of cases that the attributevalue is a part
													// of)
								attributeValueSet.put(av, test); // adds it to the final set of attrribute values.
							} else {
								attributeValueSet.get(av).add(i + 1);// if already exists, gets and adds the case.
							}
						} else {
							continue;
						}
					}
				}

			} else {
				// getValue() gets all possible values for that attribute in a list.
				for (int i = 0; i < listOfValuesforTheAttribute.size(); i++) {
					AttributeValue av = new AttributeValue(attributeName, listOfValuesforTheAttribute.get(i));
					// create a new attributeValue instance (if not already exisits)
					if (!tempAttributeValue.contains(av)) {
						tempAttributeValue.add(av);
						TreeSet set = new TreeSet();
						set.add(i + 1); // (maintains the list of cases that the attributevalue is a part of)
						attributeValueSet.put(av, set); // adds it to the final set of attrribute values.
					} else {
						attributeValueSet.get(av).add(i + 1);// if already exists, gets and adds the case.
					}
				}
			}
		}
		return attributeValueSet;
	}

	private boolean isCutPointsNecessary(List listOfValuesForTheAttribute) {
		for (int i = 0; i <= listOfValuesForTheAttribute.size(); i++) {
			try {
				String temp = (String) listOfValuesForTheAttribute.get(i);
				if (temp.equals("-") || temp.equals(" ") || temp.equals("?")) {
					continue;
				}
				Float.parseFloat((String) listOfValuesForTheAttribute.get(i));
				return true;
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		return false;
	}

	public void runAlgorithm(Map<String, ArrayList> attributeValues, Map<String, ArrayList> decisionValues) {
		// identify the cases for the attribute values --> example : (noise,low) -->
		// cases are {1,2,4,5}
		Map<AttributeValue, TreeSet> attributeValueSet = identifyCasesAndMapTo(attributeValues);
		Map<AttributeValue, TreeSet> decisionValueSet = identifyCasesAndMapTo(decisionValues);
		TreeSet universe = new TreeSet();
		// identify the concepts for the decision values --> example : (quality,low) -->
		// cases are {1,3}
		System.out.println("Attribute -> Value");
		for (Map.Entry<AttributeValue, TreeSet> entry : attributeValueSet.entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue());
		}
		System.out.println("\n");
		System.out.println("Decision -> Value");
		for (Map.Entry<AttributeValue, TreeSet> entry : decisionValueSet.entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue());
			universe.addAll(entry.getValue());
		}

		ArrayList<RulestoGoal> allListOfRules = new ArrayList<RulestoGoal>();// list of all rules.

		// iterate over each concept in decisionValueSet and start calculation of rules
		// for goals.
		System.out.println("\nProcessing...");
		for (Map.Entry<AttributeValue, TreeSet> concept : decisionValueSet.entrySet()) {
			// System.out.println("\nCurrent Concept => (d,w) = " + concept.getKey() + " ->
			// " + concept.getValue());
			TreeSet completeDecision = concept.getValue(); // needed to check if the full concept is achieved or not,
															// will never change during calculation.
			TreeSet currentDecision = concept.getValue(); // this can get updated to a part of the complete decision
															// when the other part of the concept is achieved
			TreeSet partDecision = new TreeSet(); // intersect with all attribute value pairs.
			boolean recalc = false; // tells whether the algorithm needs a recalc of intersections between attribute
									// value pairs and new goal(part goal)
			boolean completeDecisionReached = false;
			do {
				if (completeDecisionReached) {
					break;
				}
				recalc = false;
				TreeSet avDvIntersectionSets = calculateTheIntersectionSet(attributeValueSet, currentDecision);
				// calculates the the intersections based on the attribute values and current
				// decision.
				Iterator<IntersectionSelection> it = avDvIntersectionSets.iterator();
				List<AttributeValue> listOfRules = new ArrayList<AttributeValue>();
				TreeSet<?> rulesIntersectionCalculator = new TreeSet();
				while (it.hasNext()) {
					// iterate over the intersections.
					IntersectionSelection ruleToConsider = it.next(); // rules to consider are already sorted, so its
																		// always the next rule.
					if (ruleToConsider.intersectedCollection.isEmpty()) {
						continue; // do not consider empty intersection sets.
					}
					if (listOfRules.isEmpty()) {
						listOfRules.add(ruleToConsider.ruleId);
						rulesIntersectionCalculator = (TreeSet<?>) ruleToConsider.avCollection.clone();
						// add the first attribute value collection to rulesIntersection calculator
					} else {
						listOfRules.add(ruleToConsider.ruleId);
						rulesIntersectionCalculator.retainAll(ruleToConsider.avCollection);
						// if not empty then use rulesIntersection calculator for cumulative
						// intersection.
					}
					if (concept.getValue().containsAll(rulesIntersectionCalculator)) {
						// check if the cumulative intersection of rules is subset of concept.
						if (rulesIntersectionCalculator.equals(concept.getValue())) {
							// if complete match with concept, goal achieved.
							allListOfRules.add(new RulestoGoal(
									concept.getKey().toString(), simplifyIntervals(simplify(listOfRules,
											attributeValueSet, universe, rulesIntersectionCalculator)),
									rulesIntersectionCalculator));
							// System.out.println("Goal achieved" + rulesIntersectionCalculator
							// + "is subset and matched goal " + concept.getValue());
							completeDecisionReached = true;
						} else {
							// part of goal satisfied.
							allListOfRules.add(new RulestoGoal(
									concept.getKey().toString(), simplifyIntervals(simplify(listOfRules,
											attributeValueSet, universe, rulesIntersectionCalculator)),
									rulesIntersectionCalculator));
							currentDecision = new TreeSet(concept.getValue());
							// current decision gets updated..
							partDecision.addAll(rulesIntersectionCalculator);
							// partDecision maintains cumulative of all goals achieved.
							currentDecision.removeAll(partDecision);
							// current decision gets updated to total concept minus part decision.
							if (partDecision.equals(concept.getValue())) {
								completeDecisionReached = true;
								recalc = false;
								// System.out.println("Complete goal achieved.. ");
							} else {
								// System.out.println("Part Goal achieved" + rulesIntersectionCalculator + "is
								// subset of"
								// + concept.getValue());
								// System.out.println("Therefore the goal changes.. " + currentDecision);
								recalc = true;
							}
							break;
						}
					} else {
						// if its not the subset, go ahead and take the next rule from
						// avDvIntersectionSets
						continue;
					}

				}

			} while (recalc); // if recalc is set to true, execute the same process with same concept but
								// different currentDecision
		}
		System.out.println("\nFinal Ruleset...");
		for (int i = 0; i < allListOfRules.size(); i++) {
			System.out.println(allListOfRules.get(i));
		}
	}

	private List simplify(List listOfRules, Map<AttributeValue, TreeSet> attributeValueSet, TreeSet universe,
			TreeSet conceptsCovered) {
		int numberOfRules = listOfRules.size();
		int ruleIndexThatCanBeRemoved = -1;
		for (int j = 0; j < numberOfRules; j++) {
			TreeSet set = new TreeSet();
			set.addAll(universe);
			for (int k = 0; k < numberOfRules; k++) {
				if (j == k) {
					continue;
				}
				set.retainAll(attributeValueSet.get((listOfRules.get(k))));
			}
			if (conceptsCovered.containsAll(set)) {
				// ruleIndexThatCanBeRemoved = j;
				break;
			}
		}
		if (ruleIndexThatCanBeRemoved >= 0) {
			listOfRules.remove(ruleIndexThatCanBeRemoved);
		}
		return listOfRules;
	}

	private List simplifyIntervals(List listOfRules) {
		Map<String, String> attributeValues = new HashMap();
		AttributeValue rule;
		// go over all attribute values and see what attributes can be simplified..
		for (int i = 0; i < listOfRules.size(); i++) {
			rule = (AttributeValue) listOfRules.get(i);
			if (attributeValues.get(rule.attribute) == null) {
				attributeValues.put(rule.attribute, rule.value);
			} else
				attributeValues.put(rule.attribute, (attributeValues.get(rule.attribute) + "," + rule.value));
		} // this loop returns a map with attributes and their values, if more than 1
			// value for an attribute, append a comma in between.

		for (Map.Entry<String, String> entry : attributeValues.entrySet()) {
			String multipleValues[] = entry.getValue().split(","); // iterate over the above map and split by ","
			List ranges = new ArrayList();
			if (multipleValues.length > 1) { // if length is > 1 , then we have to simplify the values..
				for (int j = 0; j < multipleValues.length; j++) {
					ranges.add(new Range(multipleValues[j])); // String to range constructor is used to create multiple
																// ranges.
				}
				Range simplifiedRange = RangeUtil.rangeIntersectionCalculator(ranges);
				// Simplified range is calculated..
				AttributeValue simplifiedAV = new AttributeValue(entry.getKey(), simplifiedRange.toString());
				Iterator it = listOfRules.iterator();
				while (it.hasNext()) {
					AttributeValue tempAV = (AttributeValue) it.next();
					if (tempAV.attribute.equals(simplifiedAV.attribute)) {
						it.remove(); // once we have a simplified attribute value, remove all non
						// simplified existing
						// attributes
					}
				}
				listOfRules.add(simplifiedAV); // add the simplified version of the rule.
			}
		}

		return listOfRules;
	}

}
