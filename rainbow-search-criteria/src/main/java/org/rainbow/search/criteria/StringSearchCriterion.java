package org.rainbow.search.criteria;

/**
 *
 * @author Biya-Bi
 */
public class StringSearchCriterion extends AbstractSearchCriterion {

	public static enum Operator {
		EQUAL, NOT_EQUAL, CONTAINS, DOES_NOT_CONTAIN, LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, STARTS_WITH, ENDS_WITH, IS_EMPTY, IS_NOT_EMPTY
	}

	private Operator operator;

	private String value;

	public StringSearchCriterion() {
	}

	public StringSearchCriterion(String path) {
		super(path);
	}

	public StringSearchCriterion(String path, Operator operator, String value) {
		super(path);
		this.operator = operator;
		this.value = value;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
