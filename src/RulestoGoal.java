import java.util.List;
import java.util.TreeSet;

public class RulestoGoal {
	List listOfRules;
	TreeSet conceptsCovered;
	String goal;

	public RulestoGoal(String goal, List listOfRules, TreeSet conceptsCovered) {
		this.goal = goal;
		this.listOfRules = listOfRules;
		this.conceptsCovered = conceptsCovered;
	}

	@Override
	public String toString() {
		return this.listOfRules + " --> " + this.goal + " --> " + this.conceptsCovered.toString();
	}
}
