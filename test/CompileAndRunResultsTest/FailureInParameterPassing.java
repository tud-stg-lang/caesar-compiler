package generated;

class FailureInParameterPassing {

	public void test() {

		final Graph g = new Graph();
		final Graph f = new Graph();
		g.Node n = g.new Node();
		f.Node m = f.new Node();
		
		familizedMethod( g, n ); // OK
		familizedMethod( g, m ); // error
	}
	
	public void familizedMethod( final Graph g, g.Node n ) {}
}