package generated;

class FailureInPassMethodReturn {

	public void test() {
	
		final Outer o = new Outer();
		o.Node on = o.new Node();
		
		final Outer p = new Outer();
		p.Node pn = p.new Node();
		
		o.m( o.getNode() ); // ok
		p.m( o.getNode() ); // error
	}
}

class Outer {
	private final Graph g = new Graph();
	
	virtual class Node extends g.Node {
	}
	
	public Node m( Node n ) {
		return n;
	}
	
	public Node getNode() {
		return new Node();
	}
}