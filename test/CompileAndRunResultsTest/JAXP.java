/***
 * This is a short, ready to be compiled example of familyj.
 * It shows inner classes of JAXPDocument being declared
 * virtual and JAXPDocument's subclasses overriding them.
 * 
 * Running the main method will show that the inner classes
 * instantiation is late bound.
 * 
 * Uncommenting the statements following (*), (**) and (***)
 * will show that the compiler prevents mixing families due
 * to extension to the java type system.
 * 
 * Pleas note:
 * Virtual classes are at this moment set to some restrictions:
 * - they may only inherit other virtual classes
 * - they may only contain private fields
 * - they may only declare public or private methods.
 * 
 * Andreas Wittmann, 3rd December 2002
 */

package generated;

import java.util.Vector;

public class JAXPDocument {
	
	protected Vector nodes = new Vector();
	
	public static void main( String[] args ) {
		
		JAXPDocument xercesDoc = new XercesDocument();
		JAXPDocument crimsonDoc = new CrimsonDocument();
		
		// this is OK considering families!
		xercesDoc.appendChild( xercesDoc.new Element() );
		crimsonDoc.appendChild( crimsonDoc.new Element() );

		// this raises a compile time error
		// because of families being mixed (*):
		// xercesDoc.appendChild( crimsonDoc.new Element() );

		System.out.println( xercesDoc.getRootElement() );
		System.out.println( crimsonDoc.getRootElement() );
		
		// this is OK, too
		xercesDoc.appendChild( xercesDoc.getRootElement() );

		// this again raises a compile time error (**):
		//xercesDoc.appendChild( crimsonDoc.getRootElement() );
		
		final JAXPDocument d = new XercesDocument();
		// these are interesting new possibilities
		// regarding variable declarations (possible
		// prifxes are final local variables, final
		// method parameters and private final class
		// fields):
		d.Node n;
		n = d.new Node();
		n = d.getRootElement();

		// this again raises a compile time error (***):
		// n = xercesDoc.new Node();
		// n = xercesDoc.getRootElement();
	}
	
	public void appendChild( Node n ) {
		nodes.add( n );
	}
	
	public Node getRootElement() { return (Node) nodes.elementAt( 0 ); }
	
	public virtual class Node {
		public String toString() { return "Node"; }
	}

	public virtual class Element extends Node {
		public String toString() { return "Element extending " + super.toString(); }
	}
	
	public virtual class Attribute extends Node {
		public String toString() { return "Attribute extending " + super.toString(); }
	}
}

class XercesDocument extends JAXPDocument {
	override class Node {
		public String toString() { return "XercesNode"; }
	}
}

class CrimsonDocument extends JAXPDocument {
	override class Node {
		public String toString() { return "CrimsonNode"; }
	}
}