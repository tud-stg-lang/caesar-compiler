package generated;

import junit.framework.*;
import java.util.*;

/**
 * Expression Problem
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

		EvalNegAST ast = new EvalNegAST_Impl();
		EvalNegAST.Literal l1 = (EvalNegAST.Literal)ast.$newLiteral();
		EvalNegAST.Literal l2 = (EvalNegAST.Literal)ast.$newLiteral();
		EvalNegAST.AddExpression add = (EvalNegAST.AddExpression)ast.$newAddExpression();
		EvalNegAST.NegExpression neg = (EvalNegAST.NegExpression)ast.$newNegExpression();

		l1.init(5);
		l2.init(4);
		add.init(l1, l2);
		neg.init(add);

		System.out.println("-(5+4) = "+neg.eval());
	    
        System.out.println("-------> VCTestCase_4: end");
	}       
}

//=========================================================
public cclass AST  {			
	public cclass Expression {
	}

	public cclass AddExpression extends Expression {		
		protected AST.Expression r;
		protected AST.Expression l;
		
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
		public int eval() {return 0;}
	}

	public cclass AddExpression {
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
public cclass NegAST extends AST {
	public cclass NegExpression extends Expression {		
		protected NegAST.Expression expr;
		public void init(NegAST.Expression expr) {
			this.expr = expr;
		}
	}
}

//=========================================================
public cclass EvalNegAST extends EvalAST & NegAST {	
	public cclass NegExpression {
		public int eval() {
			return -((EvalNegAST.Expression)expr).eval();
		}
	}
}

//=========================================================
