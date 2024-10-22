
public class AttributeValue {
	String attribute;
	String value;
	Boolean isRangeSpecific;

	public AttributeValue(String attribute, String value, Boolean isRangeSpecific) {
		this.attribute = attribute; // example : Noise
		this.value = value; // example : low
		this.isRangeSpecific = isRangeSpecific;
	}

	@Override
	public boolean equals(Object e) {
		if (e instanceof AttributeValue) {
			return ((AttributeValue) e).attribute.equalsIgnoreCase(this.attribute)
					&& ((AttributeValue) e).value.equalsIgnoreCase(this.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.attribute.length() + this.value.length();
	}

	@Override
	public String toString() {
		return "(" + this.attribute + "," + this.value + ")";
	}
}
