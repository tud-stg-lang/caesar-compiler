package generated.test41;

public cclass PrintFeatureBinding extends PrintFeature {
	public cclass ExpressionPrintItem extends PrintItem
 		wraps ExpressionFeature.Expression {
 		/*...*/
	}

	before(ExpressionFeature.Expression e):
		(call(void ExpressionFeature.Expression+.doSomething()) && target(e))
	{
 		PrintFeatureBinding.ExpressionPrintItem(e).print();
	}
}