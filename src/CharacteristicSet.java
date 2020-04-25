import java.util.TreeSet;

public class CharacteristicSet implements Comparable {
	int caseNum;
	TreeSet<Integer> intersectedCharacteristicSet;

	public CharacteristicSet(int caseNum, TreeSet characteristicSet) {
		this.caseNum = caseNum;
		this.intersectedCharacteristicSet = characteristicSet;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof CharacteristicSet) {
			return this.caseNum - ((CharacteristicSet) o).caseNum;
		}
		return 0;
	}

	@Override
	public String toString() {
		return "Case " + this.caseNum + " --> " + this.intersectedCharacteristicSet;
	}

}
