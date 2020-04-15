import java.util.List;
import java.util.TreeSet;

public class RulestoGoal {
	List listOfConditions;
	TreeSet conceptsCovered;
	AttributeValue goal;
	String XYZ;

	public RulestoGoal(AttributeValue goal, List listOfConditions, TreeSet conceptsCovered) {
		this.goal = goal;
		this.listOfConditions = listOfConditions;
		this.conceptsCovered = conceptsCovered;
	}

	@Override
	public String toString() {
		String listToString = this.listOfConditions.toString();
		return this.XYZ + "\n" + listToString.substring(1, listToString.length() - 1) + " -> " + this.goal + "\n";
		// + " --> " + this.conceptsCovered.toString();
	}
}
