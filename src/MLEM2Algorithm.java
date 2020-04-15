import java.math.RoundingMode;
import java.text.DecimalFormat;
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

	public Map<Integer, List<AttributeValue>> buildCaseToListOfAttributeValues(
			Map<AttributeValue, TreeSet> attributeValueSet) {
		Map<Integer, List<AttributeValue>> characteristicSetBuilder = new HashMap();
		for (Map.Entry<AttributeValue, TreeSet> entry : attributeValueSet.entrySet()) {
			AttributeValue av = entry.getKey();
			TreeSet avSet = entry.getValue();
			Iterator it = avSet.iterator();
			while (it.hasNext()) {
				int caseNum = (Integer) it.next();
				if (characteristicSetBuilder.get(caseNum) == null) {
					List avList = new ArrayList();
					avList.add(av);
					characteristicSetBuilder.put(caseNum, avList);
				} else {
					List avList = characteristicSetBuilder.get(caseNum);
					avList.add(av);
				}
			}
		}
		return characteristicSetBuilder;
	}

	private TreeSet calculateCutPoints(List<String> listOfValuesforTheAttribute, TreeSet set) {
		DecimalFormat df = new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.DOWN);
		Iterator iter = set.iterator();
		double sum = 0.0;
		Double currentValue;
		TreeSet cutPoints = new TreeSet(); // create cut-points by taking average of consecutive numbers.
		while (iter.hasNext()) {
			Object next = iter.next();
			if (next.toString().contentEquals("*") || next.toString().contentEquals("?")
					|| next.toString().contentEquals("-")) {
				continue;
			}
			currentValue = (Double) next;
			if (cutPoints.size() == 0 && sum == 0) {
				sum = sum + currentValue;
			} else {
				sum = sum + currentValue;
				cutPoints.add(Double.parseDouble(df.format(sum / 2)));
				sum = currentValue;
			}
		}
		// return the cut-points
		return cutPoints;
	}

	private TreeSet calculateRangeSets(TreeSet cutPoints, TreeSet<Double> set) {
		Iterator it = cutPoints.iterator(); // use the range thing here....
		Double currValue;
		TreeSet<Range> setOfAllRanges = new TreeSet<Range>(); // Using Range class to create the bracket.
		Double firstValue = set.first();
		Double lastValue = set.last();
		while (it.hasNext()) {
			Object next = it.next();
			if (next.toString().contentEquals("*") || next.toString().contentEquals("?")
					|| next.toString().contentEquals("-")) {
				continue;
			}
			currValue = Double.parseDouble(next.toString());
			setOfAllRanges.add(new Range((firstValue), currValue));
			setOfAllRanges.add(new Range(currValue, (lastValue)));
		}
		return setOfAllRanges;
	}

	private TreeSet calculateTheIntersectionSet(Map<AttributeValue, TreeSet> attributeValueSet, TreeSet currentDecision,
			List<AttributeValue> alreadyEvaluatedAttributeValues) {
		int i = 1; // order of the intersection logic.
		TreeSet<IntersectionSelection> avDvIntersectionSets = new TreeSet<IntersectionSelection>();
		// set to contain the intersection of attribute values with decision,value.
		for (Map.Entry<AttributeValue, TreeSet> entryAV : attributeValueSet.entrySet()) {
			if (isConditionSkipPossible(alreadyEvaluatedAttributeValues, entryAV.getKey())) {
				continue;
			}
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

	private ArrayList<RulestoGoal> calculateXYZ(ArrayList<RulestoGoal> allListOfRulesForAllDecisions,
			Map<AttributeValue, TreeSet> originalDecisionValueSet) {
		int x, y, z;
		for (int i = 0; i < allListOfRulesForAllDecisions.size(); i++) {
			RulestoGoal r = allListOfRulesForAllDecisions.get(i);
			TreeSet actualDecision = (TreeSet) (originalDecisionValueSet.get(r.goal)).clone();
			x = r.listOfConditions.size();
			z = r.conceptsCovered.size();
			actualDecision.retainAll(r.conceptsCovered);
			y = actualDecision.size();
			r.XYZ = x + ", " + y + ", " + z;
		}

		return allListOfRulesForAllDecisions;
	}

	// Used to identify cases for each attribute and value.
	public Map identifyCasesAndMapTo(Map<String, ArrayList> attributeValues, Boolean isDecision) {
		Map<AttributeValue, TreeSet> attributeValueSet = new LinkedHashMap();
		Set<AttributeValue> tempAttributeValue = new HashSet<AttributeValue>();
		for (Map.Entry<String, ArrayList> entry : attributeValues.entrySet()) {
			String attributeName = entry.getKey(); // gets the unique attribute name.
			List listOfValuesforTheAttribute = entry.getValue();
			if (!isDecision && isCutPointsNecessary(listOfValuesforTheAttribute)) {
				TreeSet set = new TreeSet(); // add all elements to Set (increasing order of values.)
				for (int i = 0; i < listOfValuesforTheAttribute.size(); i++) {
					String temp = ((String) listOfValuesforTheAttribute.get(i));
					if (temp.equals("-") || temp.equals("*") || temp.equals("?")) {
						continue;
					}
					set.add(Double.parseDouble(temp));
				}
				TreeSet cutPoints = calculateCutPoints(listOfValuesforTheAttribute, set);
				TreeSet rangeSets = calculateRangeSets(cutPoints, set);
				for (int i = 0; i < listOfValuesforTheAttribute.size(); i++) {
					Iterator rangesIterator = rangeSets.iterator();
					while (rangesIterator.hasNext()) {
						Range r = (Range) rangesIterator.next();
						String currentValue = (String) listOfValuesforTheAttribute.get(i);
						if (currentValue.equals("-") || currentValue.equals("*") || currentValue.equals("?")) {
							AttributeValue av = new AttributeValue(attributeName, currentValue, true);
							if (!tempAttributeValue.contains(av)) {
								tempAttributeValue.add(av);
								TreeSet _set = new TreeSet();
								_set.add(i + 1); // (maintains the list of cases that the attributevalue is a part
													// of)
								attributeValueSet.put(av, _set); // adds it to the final set of attrribute values.
							} else {
								attributeValueSet.get(av).add(i + 1);// if already exists, gets and adds the case.
							}
							continue;
						}
						if (r.contains(Double.parseDouble(currentValue))) { // if the current value is within current
																			// range,
							// add it with range as its value to attribute, else continue and look for next
							// range.
							AttributeValue av = new AttributeValue(attributeName, r.toString(), true);
							if (!tempAttributeValue.contains(av)) {
								tempAttributeValue.add(av);
								TreeSet _set = new TreeSet();
								_set.add(i + 1); // (maintains the list of cases that the attributevalue is a part
													// of)
								attributeValueSet.put(av, _set); // adds it to the final set of attrribute values.
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
					AttributeValue av = new AttributeValue(attributeName, (String) listOfValuesforTheAttribute.get(i),
							false);
					// create a new attributeValue instance (if not already exists)
					if (!tempAttributeValue.contains(av)) {
						tempAttributeValue.add(av);
						TreeSet set = new TreeSet();
						set.add(i + 1); // (maintains the list of cases that the attributevalue is a part of)
						attributeValueSet.put(av, set); // adds it to the final set of attribute values.
					} else {
						attributeValueSet.get(av).add(i + 1);// if already exists, gets and adds the case.
					}
				}
			}
		}
		return attributeValueSet;
	}

	private boolean isConditionSkipPossible(List<AttributeValue> alreadyCalcuatedAttributeValues,
			AttributeValue currentCondition) {
		if (alreadyCalcuatedAttributeValues.contains(currentCondition)) {
			return true; // same attribute value, must be skipped
		} else {
			AttributeValue rhs = currentCondition;
			Range cumulativeRange = null;
			ArrayList<Range> listOfRanges = new ArrayList<Range>();
			ArrayList listOfAttributesAlreadyUsed = new ArrayList();
			for (int k = 0; k < alreadyCalcuatedAttributeValues.size(); k++) {
				AttributeValue lhs = alreadyCalcuatedAttributeValues.get(k);
				if (!lhs.isRangeSpecific) {
					listOfAttributesAlreadyUsed.add(lhs.attribute);
					// here we are making sure that we collect all the already used
					// attributes, like say, (headache,yes) is already used, we do not consider
					// (headache,no) too, so , the whole attribute has to be skipped.
					// It is not applicable to rangeSpecifc attributes
				}
				if (currentCondition.isRangeSpecific) {
					if (lhs.attribute.equals(rhs.attribute)) {
						cumulativeRange = new Range(lhs.value);
						listOfRanges.add(cumulativeRange);
						cumulativeRange = RangeUtil.rangeIntersectionCalculator(listOfRanges);
						Range rlhs = new Range(lhs.value);
						Range rrhs = new Range(rhs.value);
						if (rrhs.isSuperSet(rlhs)) {
							// if the new condition issuper set of already selected condition, skip it
							return true;
						}
					}
				}
			}
			if (currentCondition.isRangeSpecific) {
				listOfRanges.add(new Range(currentCondition.value));
				if (RangeUtil.rangeIntersectionCalculator(listOfRanges) == null) {
					// if the intersection is null, then that means the new range is disjunctive to
					// the current
					// collection of ranges., so skip it.
					return true;

				}
			} else {
				if (listOfAttributesAlreadyUsed.contains(currentCondition.attribute)) {
					// here we are making sure that collect all the already used
					// attributes, like say, (headache,yes) is already used, we do not consider
					// (headache,no) too, so , the whole attribute has to be skipped.
					// It is not applicable to rangeSpecifc attributes
					return true;
				}
			}
		}
		return false;

	}

	private boolean isCutPointsNecessary(List listOfValuesForTheAttribute) {
		for (int i = 0; i <= listOfValuesForTheAttribute.size(); i++) {
			try {
				String temp = (String) listOfValuesForTheAttribute.get(i);
				if (temp.equals("-") || temp.equals("*") || temp.equals("?")) {
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

	public ArrayList preWorkAndRunAlgorithm(Map<String, ArrayList> attributeValues,
			Map<String, ArrayList> decisionValues, boolean lowerApproximation) {
		// -- AV to cases mapping
		Map<AttributeValue, TreeSet> attributeValueSet = identifyCasesAndMapTo(attributeValues, false);
		Map<AttributeValue, TreeSet> attributeValueSetCopy = identifyCasesAndMapTo(attributeValues, false);
		Map<AttributeValue, TreeSet> decisionValueSet = identifyCasesAndMapTo(decisionValues, true);
		TreeSet universe = new TreeSet();
		for (Map.Entry<AttributeValue, TreeSet> entry : decisionValueSet.entrySet()) {
			// System.out.println(entry.getKey() + " -> " + entry.getValue());
			universe.addAll(entry.getValue());
		}

		Map<Integer, List<AttributeValue>> caseToListOfAttributeValues = buildCaseToListOfAttributeValues(
				attributeValueSet);

		Map<Integer, List<AttributeValue>> caseTodecisionValue = buildCaseToListOfAttributeValues(decisionValueSet);
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
		// -----Reconstruct attributeValue solving the * and -
		for (Map.Entry<AttributeValue, TreeSet> entry : attributeValueSetCopy.entrySet()) {
			// there can be many don't care conditions for a single attribute,
			// the attribute values for that attribute should include, all don't care
			// conditions.
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
		Map<Integer, List<AttributeValue>> reCalculatedCaseToListOfAttributeValues = buildCaseToListOfAttributeValues(
				attributeValueSet);
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
		// System.out.println("Final Characteristic Set");
		// System.out.println(characteristicSet);
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
		List allListOfRules = new ArrayList();
		if (lowerApproximation) {
			System.out.println("\nProcessing rules for lower approximation...");
			allListOfRules = runAlgorithm(attributeValueSet, conceptAndLowerConceptApproximations, universe,
					decisionValueSet);
			System.out.println("\nFinal Ruleset...");
			for (int i = 0; i < allListOfRules.size(); i++) {
				System.out.println(allListOfRules.get(i));
			}
		} else {
			System.out.println("\nProcessing rules for Upper approximation...");
			allListOfRules = runAlgorithm(attributeValueSet, conceptAndUpperConceptApproximations, universe,
					decisionValueSet);
			System.out.println("\nFinal Ruleset...");
			for (int i = 0; i < allListOfRules.size(); i++) {
				System.out.println(allListOfRules.get(i));
			}
		}
		return (ArrayList) allListOfRules;
	}

	public ArrayList<RulestoGoal> runAlgorithm(Map<AttributeValue, TreeSet> attributeValueSet,
			Map<AttributeValue, TreeSet> decisionValueSet, TreeSet universe,
			Map<AttributeValue, TreeSet> originalDecisionValueSet) {
		// decisionValueSet can be an approximation value set too.
		// originalDecisionValueSet is the unaltered decision value set. (used for x,y,z
		// calculation)
		ArrayList<RulestoGoal> allListOfRulesForAllDecisions = new ArrayList();
		// iterate over each concept in decisionValueSet and start calculation of rules
		// for goals.
		for (Map.Entry<AttributeValue, TreeSet> concept : decisionValueSet.entrySet()) {
			ArrayList<RulestoGoal> allListOfRules = new ArrayList<RulestoGoal>();// list of all rules.
			System.out.println("\nCurrent Concept => (d,w) = " + concept.getKey() + " ->" + concept.getValue());
			TreeSet completeDecision = concept.getValue(); // needed to check if the full concept is achieved or not,
															// will never change during calculation.
			TreeSet currentDecision = concept.getValue(); // this can get updated to a part of the complete decision
															// when the other part of the concept is achieved
			TreeSet partDecision = new TreeSet(); // intersect with all attribute value pairs.
			boolean recalc = false; // tells whether the algorithm needs a recalc of intersections between attribute
									// value pairs and new goal(part goal)
			boolean completeDecisionReached = false;
			int test = 0;
			// System.out.println("New Decision " + currentDecision);
			List<AttributeValue> listOfConditionsForARule = new ArrayList<AttributeValue>();
			List alreadyCalculatedAttributeValues = new ArrayList();
			TreeSet<?> rulesIntersectionCalculator = new TreeSet();
			boolean partCalc = false;
			do {
				// System.out.println(currentDecision);
				if (completeDecisionReached) {
					break;
				}
				recalc = false;
				do {
					TreeSet avDvIntersectionSets;
					// System.out.println("Current Decision " + currentDecision);
					if (partCalc) {
						partCalc = false;
						avDvIntersectionSets = calculateTheIntersectionSet(attributeValueSet, currentDecision,
								alreadyCalculatedAttributeValues);
					} else {
						avDvIntersectionSets = calculateTheIntersectionSet(attributeValueSet, currentDecision,
								new ArrayList());
						listOfConditionsForARule = new ArrayList<AttributeValue>();
						rulesIntersectionCalculator = new TreeSet();
					}

					// calculates the the intersections based on the attribute values and current
					// decision.
					Iterator<IntersectionSelection> it = avDvIntersectionSets.iterator();
					while (it.hasNext()) {
						// iterate over the intersections.
						IntersectionSelection ruleToConsider = it.next(); // rules to consider are already sorted, so
						// its always the next rule.
						if (isConditionSkipPossible(alreadyCalculatedAttributeValues, ruleToConsider.ruleId)) {
							continue;
						}
						alreadyCalculatedAttributeValues.add(ruleToConsider.ruleId);
						if (ruleToConsider.intersectedCollection.isEmpty()) {
							continue; // do not consider empty intersection sets.
						}
						if (listOfConditionsForARule.isEmpty()) {
							// System.out.println(ruleToConsider.ruleId);
							listOfConditionsForARule.add(ruleToConsider.ruleId);
							rulesIntersectionCalculator = (TreeSet<?>) ruleToConsider.avCollection.clone();
							// add the first attribute value collection to rulesIntersection calculator
						} else {
							// System.out.println(ruleToConsider.ruleId);
							listOfConditionsForARule.add(ruleToConsider.ruleId);
							rulesIntersectionCalculator.retainAll(ruleToConsider.avCollection);
							// if not empty then use rulesIntersection calculator for cumulative
							// intersection.
						}
						if (concept.getValue().containsAll(rulesIntersectionCalculator)) {
							// check if the cumulative intersection of rules is subset of concept.
							if (rulesIntersectionCalculator.equals(concept.getValue())) {
								// if complete match with concept, goal achieved.
								allListOfRules.add(new RulestoGoal(
										concept.getKey(), simplifyIntervals(simplify(listOfConditionsForARule,
												attributeValueSet, universe, rulesIntersectionCalculator)),
										rulesIntersectionCalculator));
								// System.out.println("Goal achieved" + rulesIntersectionCalculator
								// + "is subset and matched goal " + concept.getValue());
								completeDecisionReached = true;
								break;
							} else {
								// part of goal satisfied.
								allListOfRules.add(new RulestoGoal(
										concept.getKey(), simplifyIntervals(simplify(listOfConditionsForARule,
												attributeValueSet, universe, rulesIntersectionCalculator)),
										rulesIntersectionCalculator));
								currentDecision = new TreeSet(concept.getValue());
								// current decision gets updated..
								partDecision.addAll(rulesIntersectionCalculator);
								// partDecision maintains cumulative of all goals achieved.
								currentDecision.removeAll(partDecision);
								// bug fix --> not resetting the already used attributes when partial goal is
								// achieved.
								alreadyCalculatedAttributeValues = new ArrayList();

								// current decision gets updated to total concept minus part decision.
								if (partDecision.equals(concept.getValue())) {
									completeDecisionReached = true;
									recalc = false;
									System.out.println("Complete goal achieved.. ");
								} else {
									// System.out.println("Part Goal achieved" + rulesIntersectionCalculator
									// + "is subset of" + concept.getValue());
									// System.out.println("Therefore the goal changes.. " + currentDecision);
									recalc = true;
								}
								break;
							}
						} else {

							TreeSet duplicaterulesIntersectionCalculator = (TreeSet) rulesIntersectionCalculator
									.clone();
							duplicaterulesIntersectionCalculator.retainAll(currentDecision);
							if (!duplicaterulesIntersectionCalculator.equals(currentDecision)) {
								currentDecision = duplicaterulesIntersectionCalculator;
								// System.out.println("updated current decision" + currentDecision);
								partCalc = true;
								break;
							}
							// if its not the subset, go ahead and take the next rule from
							// avDvIntersectionSets
							continue;
						}

					}
				} while (partCalc);

			} while (recalc); // if recalc is set to true, execute the same process with same concept but
								// different currentDecision

			// System.out.println("size of rules before redundancy check " +
			// allListOfRules.size());
			allListOfRules = runRedundancyCheck(allListOfRules);
			// System.out.println("size of rules after redundancy check " +
			// allListOfRules.size());
			allListOfRulesForAllDecisions.addAll(allListOfRules);
		}
		allListOfRulesForAllDecisions = calculateXYZ(allListOfRulesForAllDecisions, originalDecisionValueSet);
		return allListOfRulesForAllDecisions;
	}

	public ArrayList runAlgorithm(Map<String, ArrayList> attributeValues, Map<String, ArrayList> decisionValues) {
		// identify the cases for the attribute values --> example : (noise,low) -->
		// cases are {1,2,4,5}
		Map<AttributeValue, TreeSet> attributeValueSet = identifyCasesAndMapTo(attributeValues, false);
		Map<AttributeValue, TreeSet> decisionValueSet = identifyCasesAndMapTo(decisionValues, true);
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

		System.out.println("\nProcessing...");
		List allListOfRules = runAlgorithm(attributeValueSet, decisionValueSet, universe, decisionValueSet);
		System.out.println("\nFinal Ruleset...");
		for (int i = 0; i < allListOfRules.size(); i++) {
			System.out.println(allListOfRules.get(i));
		}
		return (ArrayList) allListOfRules;
	}

	private ArrayList<RulestoGoal> runRedundancyCheck(ArrayList<RulestoGoal> allListOfRules) {
		Set alreadyCovered = new TreeSet();
		ArrayList nonRedundantRules = new ArrayList();
		for (int i = 0; i < allListOfRules.size(); i++) {
			RulestoGoal r = allListOfRules.get(i);
			if (!alreadyCovered.containsAll(r.conceptsCovered)) {
				alreadyCovered.addAll(r.conceptsCovered);
				nonRedundantRules.add(r);
			}
		}
		return nonRedundantRules;
	}

	private List simplify(List listOfConditionsForARule, Map<AttributeValue, TreeSet> attributeValueSet,
			TreeSet universe, TreeSet conceptsCovered) {
		int numberOfConditions = listOfConditionsForARule.size();
		List<Integer> indexesThatCanBeRemoved = new ArrayList<Integer>();
		for (int j = 0; j < numberOfConditions; j++) {
			TreeSet set = new TreeSet();
			set.addAll(universe);
			for (int k = 0; k < numberOfConditions; k++) {
				if (indexesThatCanBeRemoved.contains(k)) {
					continue;
				}
				if (j == k) {
					continue;
				}
				set.retainAll(attributeValueSet.get((listOfConditionsForARule.get(k))));

			}
			if (conceptsCovered.containsAll(set)) {
				indexesThatCanBeRemoved.add(j);
			}
		}
		if (indexesThatCanBeRemoved.size() > 0) {
			for (int l = indexesThatCanBeRemoved.size() - 1; l >= 0; l--) {
				listOfConditionsForARule.remove((int) indexesThatCanBeRemoved.get(l));
			}
		}
		return listOfConditionsForARule;
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
				if (simplifiedRange == null) {
					System.out.println(ranges);
				}
				// System.out.println(simplifiedRange);
				// Simplified range is calculated..
				AttributeValue simplifiedAV = new AttributeValue(entry.getKey(), simplifiedRange.toString(), true);
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
