package org.rainbow.criteria;

import java.util.List;

public class PredicateImpl implements Predicate {
	private List<Expression<Boolean>> expressions;
	private BooleanOperator booleanOperator;
	private boolean isNegated;

	public PredicateImpl(BooleanOperator booleanOperator, List<Expression<Boolean>> expressions) {
		super();
		this.booleanOperator = booleanOperator == null ? BooleanOperator.AND : booleanOperator;
		this.expressions = expressions;
	}

	public PredicateImpl(List<Expression<Boolean>> expressions) {
		this(null, expressions);
	}

	public PredicateImpl() {
	}

	@Override
	public BooleanOperator getOperator() {
		return booleanOperator;
	}

	@Override
	public boolean isNegated() {
		return isNegated;
	}

	protected void setNegated(boolean isNegated) {
		this.isNegated = isNegated;
	}

	@Override
	public List<Expression<Boolean>> getExpressions() {
		return expressions;
	}

	@Override
	public Predicate not() {
		PredicateImpl p = new PredicateImpl(booleanOperator, expressions);
		p.setNegated(true);
		return p;
	}

}
