package generated;

import junit.framework.*;
import java.util.*;

/**
 * Test &-Operator and linearization
 * 
 * @author Ivica Aracic
 */
public class VCTestCase_4 extends TestCase {

	public VCTestCase_4() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
        System.out.println("-------> VCTestCase_4: start");
        
        DrawApp drawApp = new DrawApp();
	    
	    DrawApp.Rectangle rect = drawApp.$newRectangle();
	    DrawApp.Circle circle = drawApp.$newCircle();
		
		
		
		DrawApp.Composite composite = (DrawApp.Composite)drawApp.$newComposite();		
		
		composite.addChild(rect);
		composite.addChild(circle);	    
		
		for(Iterator it=composite.iterator(); it.hasNext(); ) {
			DrawApp.Component item = (DrawApp.Component)it.next();
			System.out.println("** "+item);
		}
	    
	    
        System.out.println("-------> VCTestCase_4: end");
	}       
}

//=========================================================
public cclass CompositePattern {


	public cclass Component {
	}
	
	public cclass Composite extends Component {
		private List children = new LinkedList();
		
		public void addChild(CompositePattern.Component child) {
			children.add(child);
		}
		
		public void removeChild(CompositePattern.Component child) {
			children.remove(child);
		}
		
		public Iterator iterator() {
			return children.iterator();
		}
	}
}

//=========================================================
public cclass DrawApp extends CompositePattern {
	/** every component has x,y position */
	public cclass Component {		
		private int x,y;
		
		public void setX(int x) {this.x=x;}
		public int getX() {return x;}
		public void setY(int y) {this.y=y;}
		public int getY() {return y;}
		
		public String toString() {
			return "pos("+x+"; "+y+")";
		}
	}
	
	public cclass Rectangle extends Component {
		private int w,h;
		public void setW(int w) {this.w=w;}
		public int getW() {return w;}
		public void setH(int h) {this.h=h;}
		public int getH() {return h;}
		
		public String toString() {
			return "Rectangle: "+super.toString()+", w:"+w+" ,h:"+h;
		}
	}
	
	public cclass Circle extends Component {
		private int r;
		public int getR() {return r;}
		public void setR(int r) {this.r=r;}

		public String toString() {
			return "Circle: "+super.toString()+", r:"+r;
		}
	}	
}

//=========================================================
