package generated;

public class FailureInInitializer {
	
	public void test() {
		final Graph g = new Graph();
		final Graph f = new Graph();
		
		g.Edge gE = g.new Edge();
		f.Edge fE = g.new Edge();
	}
}