import java.util.List;

public class RangeUtil {

	public static Range rangeIntersectionCalculator(List<Range> multipleRanges) {
		Range allRangeIntersection = new Range(Double.MIN_VALUE, Double.MAX_VALUE);
		for (int i = 0; i < multipleRanges.size(); i++) {
			if (multipleRanges.get(i).minimum > allRangeIntersection.minimum) {
				allRangeIntersection.minimum = multipleRanges.get(i).minimum;
			}
			if (multipleRanges.get(i).maximum < allRangeIntersection.maximum) {
				allRangeIntersection.maximum = multipleRanges.get(i).maximum;
			}
			if (allRangeIntersection.maximum < allRangeIntersection.minimum) {
				return null; // exception case..need to be handled..
			}
		}
		return allRangeIntersection;
	}

}
