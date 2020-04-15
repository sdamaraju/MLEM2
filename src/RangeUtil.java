import java.util.List;

public class RangeUtil {

	public static Range rangeIntersectionCalculator(List<Range> multipleRanges) {
		Range allRangeIntersection = new Range(Double.valueOf(Integer.MIN_VALUE), Double.MAX_VALUE);
		// System.out.println(multipleRanges);
		for (int i = 0; i < multipleRanges.size(); i++) {
			// System.out.println(allRangeIntersection.minimum + " " +
			// allRangeIntersection.maximum);
			if (multipleRanges.get(i).minimum > allRangeIntersection.minimum) {
				allRangeIntersection.minimum = multipleRanges.get(i).minimum;
			}
			if (multipleRanges.get(i).maximum < allRangeIntersection.maximum) {
				allRangeIntersection.maximum = multipleRanges.get(i).maximum;
			}
			if (allRangeIntersection.maximum < allRangeIntersection.minimum) {
				return null; // exception case..need to be handled.. when do
			}
		}
		return allRangeIntersection;
	}

}
