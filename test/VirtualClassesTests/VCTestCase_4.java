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
        
        /*
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
		*/
		
		/*
		PrettyPrintEvalAST evalAst = new PrettyPrintEvalAST();
		PrettyPrintEvalAST.Literal l1 = 
			(PrettyPrintEvalAST.Literal)((PrettyPrintEvalAST.Literal)evalAst.$newLiteral()).init(5);
		PrettyPrintEvalAST.Literal l2 = 
			(PrettyPrintEvalAST.Literal)((PrettyPrintEvalAST.Literal)evalAst.$newLiteral()).init(4);
		PrettyPrintEvalAST.Expression exp = 
			(PrettyPrintEvalAST.Expression)((PrettyPrintEvalAST.Expression)evalAst.$newExpression()).init(l1,l2);

		exp.print();
		System.out.println(" = "+exp.eval());
		*/
		
		PrettyPrintAST evalAst = new PrettyPrintAST();
		PrettyPrintAST.Literal l1 = 
			(PrettyPrintAST.Literal)((PrettyPrintAST.Literal)evalAst.$newLiteral()).init(5);
		PrettyPrintAST.Literal l2 = 
			(PrettyPrintAST.Literal)((PrettyPrintAST.Literal)evalAst.$newLiteral()).init(4);
		PrettyPrintAST.Expression exp = 
			(PrettyPrintAST.Expression)((PrettyPrintAST.Expression)evalAst.$newExpression()).init(l1,l2);
		
		exp.print();		
		System.out.println();
	    
	    
        System.out.println("-------> VCTestCase_4: end");
	}       
}

//=========================================================
/*
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
*/

//=========================================================
public cclass AST  {			
	public cclass Expression {		
		protected Expression r;
		protected Expression l;
		
		public AST.Expression init(AST.Expression l, AST.Expression r) {
			this.r = r;
			this.l = l;
			return this;
		}
	}
	
	public cclass Literal extends Expression {
		protected int val;
		
		public AST.Literal init(int val) {
			this.val = val;
			return this;
		}	
	}	
}

//=========================================================
public cclass EvalAST extends AST {
	public cclass Expression {
		public int eval() {
			return
				((EvalAST.Expression)l).eval() +
				((EvalAST.Expression)r).eval();
		}
	}

	public cclass Literal {
		public int eval() {
			return val;
		}
	}
}

//=========================================================
public cclass PrettyPrintAST extends AST {
	public cclass Expression {
		public void print() {
			((PrettyPrintAST.Expression)l).print();
			System.out.print(" + ( ");	
			((PrettyPrintAST.Expression)r).print();
			System.out.print(" ) ");
		}
	}

	public cclass Literal {
		public void print() {
			System.out.print(val);
		}
	}	
}

//=========================================================
public cclass PrettyPrintEvalAST extends PrettyPrintAST & EvalAST {	
}

//=========================================================
/*
public cclass DrawApp extends CompositePattern {
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
*/
//=========================================================
