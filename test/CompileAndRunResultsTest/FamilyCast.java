package generated;

public class FamilyCast {
	
	public void test() {
		final Graph g = new Graph();
		g.Node n = g.new Node();
		Object o = n;
		n = (g.Node) o;
	}
}