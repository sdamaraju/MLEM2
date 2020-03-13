public class Range implements Comparable {

	Double minimum;

	Double maximum;

	public Range(Double minimum, Double maximum) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	public Range(String minToMax) {
		this.minimum = Double.parseDouble(minToMax.substring(0, minToMax.indexOf("...")));
		this.maximum = Double.parseDouble(minToMax.substring(minToMax.indexOf("...") + 3, minToMax.length()));
	}

	@Override
	public int compareTo(Object o) {
		if (this.minimum.equals(((Range) o).minimum)) {
			return this.maximum.compareTo(((Range) o).maximum);
		} else {
			return this.minimum.compareTo(((Range) o).minimum);
		}
	}

	public boolean contains(Double x) {
		return x >= this.minimum && x <= this.maximum;
	}

	@Override
	public String toString() {
		return this.minimum.toString() + "..." + this.maximum.toString();
	}

}
