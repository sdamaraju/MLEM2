import java.util.Set;
import java.util.TreeSet;

/*
 * This class helps to maintain the intersection between the attribute value and the decision value
 * Also maintains a lot of other data on which the calculation for which intersectionSelection can be picked as the next best possible rule
 * based on the sort pattern.
 * */

public class IntersectionSelection implements Comparable {
	AttributeValue ruleId;
	Set intersectedCollection; // should be maximum Priority 1
	TreeSet avCollection;
	int sizeOfAttributes; // should be minimum Priority 2
	int orderOfAttribute; // in the same order Priority 3
	boolean alreadyUsed; // actually not necessary as we are sorting.

	public IntersectionSelection(AttributeValue ruleIdentifier, Set intersectedCollection, TreeSet avCollection,
			int sizeOfAttrs, int order) {
		this.ruleId = ruleIdentifier;
		this.intersectedCollection = intersectedCollection;
		this.avCollection = avCollection;
		this.sizeOfAttributes = sizeOfAttrs;
		this.orderOfAttribute = order;
		this.alreadyUsed = false;

	}

	@Override
	public int compareTo(Object o) {
		// we first check the size of the intersection, and that has to be greater, if
		// they both are same, we then evaluate what is the (a,v) cases covered size,
		// and that should be smaller, if that is also same then we go with the order of
		// the intersection happened
		if (((IntersectionSelection) o).intersectedCollection.size() - this.intersectedCollection.size() == 0) {
			if (this.sizeOfAttributes - ((IntersectionSelection) o).sizeOfAttributes == 0) {
				return this.orderOfAttribute - ((IntersectionSelection) o).orderOfAttribute;
			} else {
				return this.sizeOfAttributes - ((IntersectionSelection) o).sizeOfAttributes;
			}
		} else
			return ((IntersectionSelection) o).intersectedCollection.size() - this.intersectedCollection.size();
	}

	@Override
	public String toString() {
		// return avCollection.toString() + " " + intersectedCollection.toString() + "
		// Size of attributes =>"
		// + sizeOfAttributes + " order of the attribute" + orderOfAttribute + " Already
		// Used => " + alreadyUsed
		// + "\n";
		return ruleId.toString() + this.avCollection;
	}
}
