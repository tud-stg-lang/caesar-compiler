ObserverProtocolImplSub.java ObserverProtocolBinding.java ObserverProtocolBindingSub.java  ObserverProtocol.java ObserverProtocolParent.java ObserverProtocolImpl.java ObserverProtocolWeavelet.java ObserverProtocolWeaveletSub.java GraphTest.java  CIWithoutProvidedOrExpected.java CollaborationInterface1.java
ObserverProtocol.java ObserverProtocolBinding.java ObserverProtocolBindingSub.java ObserverProtocolImpl.java ObserverProtocolParent.java ObserverProtocolWeavelet.java

ObserverProtocolImplSub.java ObserverProtocolBinding.java ObserverProtocolBindingSub.java  ObserverProtocol.java ObserverProtocolBindingSubSub.java  ObserverProtocolParent.java ObserverProtocolImpl.java ObserverProtocolImplSubSub.java ObserverProtocolWeavelet.java ObserverProtocolWeaveletSub.java

ObserverProtocolBindingSub.java ObserverProtocolImplSub.java ObserverProtocolBinding.java ObserverProtocol.java  ObserverProtocolParent.java ObserverProtocolImpl.java ObserverProtocolImplSubSub.java ObserverProtocolWeavelet.java ObserverProtocolWeaveletSub.java Wrappee.java

Main.java MediatorProtocol.java MediatorProtocolBinding.java MediatorProtocolBindingSub.java MediatorProtocolImpl.java MediatorProtocolWeavelet.java Button.java Label.java

The familyj compiler 1.0

This file is thought to act as a first introduction
on how to build the familyj compiler, how to compile
source-files with it and to give small hints on the
additional language features being the difference of
familyj and java.


Building the familyj compiler

In order to build the familyj compiler you must have
the java software development kit 1.4.0 or greater
installed. When compiling the sources be sure to have
subfolder src and all .jar files in subfolder lib
in your classpath environment varible. The following
statements should do the job on a unix machine and
place the compiler classes to the bin subfolder:

cd ~/familyj
javac -classpath src:lib/BCEL.jar:lib/gnu-regexp-1.1.4.jar:lib/java-getopt-1.0.9.jar:lib/JFlex.jar:lib/junit.jar -d bin src/family/compiler/Main.java


Invoking the familyj compiler

The class to be invoked in order to start a compile
is familyj.compiler.Main. It expects a list of .java
source files as commandline parameters.

cd ~/familyj
java -classpath bin:lib/BCEL.jar:lib/gnu-regexp-1.1.4.jar:lib/java-getopt-1.0.9.jar:lib/JFlex.jar:lib/junit.jar family.compiler.Main <java source file>+


Using the familyj compiler

To get a first impression an familyj's features, please
study the following example - It is ready to be compiled
or extended - have fun!

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
		xercesDoc.appendChild( xercesDoc.new Element("A") );
		crimsonDoc.appendChild( crimsonDoc.new Element("A") );

		// this raises a compile time error
		// because of families being mixed (*):
		// xercesDoc.appendChild( crimsonDoc.new Element("A") );

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
		n = d.new Node("A");
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
		private String name;
		public Node(String name)
		{
			this.name = name;
		}
		public String toString() { return "Node"; }
	}

	public virtual class Element extends Node {
		public Node(String name)
		{
			super(name);
		}	
		public String toString() { return "Element extending " + super.toString(); }
	}
	
	public virtual class Attribute extends Node {
		public Node(String name)
		{
			super(name);
		}	
		public String toString() { return "Attribute extending " + super.toString(); }
	}
}

class XercesDocument extends JAXPDocument {
	override class Node {
		public Node(String name)
		{
			super(name);
		}	

		public String toString() { return "XercesNode"; }
	}
}

class CrimsonDocument extends JAXPDocument {
	override class Node {
		public Node(String name)
		{
			super(name);
		}	

		public String toString() { return "CrimsonNode"; }
	}
}