package generated;

import junit.framework.TestCase;

public class GraphTest extends TestCase {
	public GraphTest() {
		super( "test" );
		_e = new Graph();
		_f = new Graph();
	}	
	
	_f.Node _fn;
	final Graph _f;
	private final Graph _e;
	_e.Node _en;

	virtual class SubNode extends _e.Node 
	{
		public void a(_e.Edge s)
		{
			s.a(this);
		}
	}
	
	virtual class SubEdge extends _e.Edge
	{
		public void a(_e.Node n)
		{
			n.a(this);
		}
	}
	
	virtual class SubGraph extends Graph 
	{
	}
	

	public boolean build( final Graph g, g.Node n, g.Edge e, boolean shouldTouch ) {
		
		g.Node m;
		m = n;

		final Graph i = new Graph();
		i.Node j = i.new Node();
		i.Node k = null;
		j = k;

		Graph.Node gn = m;
		
		// let us see if extending _e works
		build3( _e.new Node() );
		_e.Node _en = _e.new Node();
		final GraphTest gt = this;		
		_en = gt.new SubNode();

		// this will rais an error
		// e.setN1( _en );
		
		// this should work
		e.setN2( n );		
		return (n.touches( e ) == shouldTouch);
	}	

	public boolean build2( final OnOffGraph g, g.Edge e, boolean shouldBeEnabled ) {
		return shouldBeEnabled == e.isEnabled();
	}	
	
	public void build3( _e.Node n ) {
	}
	
	public void test() {
		Graph g = new Graph();
		OnOffGraph o = new OnOffGraph();
		final Graph go = new OnOffGraph( new SysoutGraph( new Graph() ) );
		
		assertTrue( build( g, g.new Node(), g.new Edge(), true ) );
		assertTrue( build( o, o.new Node(), o.new Edge(), false ) );

		// the following would not work with
		// ordinary java because Graph.Node and
		// Graph.Edge would be instantiated!

		assertTrue( build( go, go.new Node(), go.new Edge(), false ) );
		assertTrue( build( go, go.new Node(), go.new Edge(), false ) );

		// extending a field's inner possible?
		assertNotNull( this.new SubNode() );

		// and the same with an OnOffGraph
		assertTrue( build2( o, o.new Edge(), false ) );

		go.Edge e = go.new Edge();
		((OnOffGraph.Edge)e).setEnabled( true );
		assertTrue( build( go, go.new Node(), e, true ) );
	}
}

clean class Graph {
	public virtual class Node {
		public boolean touches( Edge e ) {
			boolean result = (this === e.getN1() || this === e.getN2() );
			return result;
		}
		public void a(Edge e)
		{}
	}
	public virtual class Edge {
		private Node n1;
		private Node n2;
		public void setN1( Node n1 ) {
			this.n1 = n1;
		}
		public Node getN1() {
			return this.n1;
		}
		public void setN2( Node n2 ) {
			this.n2 = n2;
		}
		public Node getN2() {
			return this.n2;
		}
		public void a(Node n)
		{
		}
	}
}

clean class OnOffGraph extends Graph {
	public override class Node {
		public boolean touches( Edge _e_ ) {
			boolean result = _e_.isEnabled() && super.touches( _e_ );
			return result;
		}
	}
	public override class Edge {
		private boolean enabled = false;
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled( boolean newEnabled ) {
			enabled = newEnabled;
		}
		public Node getN1() {
			return super.getN1();
		}
	}
}

clean class SysoutGraph extends Graph {
	public override class Node {
		public boolean touches( Edge e ) {			
			//System.out.println( "sysout!" );
			return super.touches( e );
		}
	}
}