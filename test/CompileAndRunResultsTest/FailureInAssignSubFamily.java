package generated;

class FailureInAssignSubFamily {

	private final Graph g = null;
	
	virtual class SubNode extends g.Node {}

	public void test() {
		final FailureInAssignSubFamily f = new FailureInAssignSubFamily();
		f.SubNode sn = null;
		g.Node gn = sn;
		sn = gn; // => error
	}
}