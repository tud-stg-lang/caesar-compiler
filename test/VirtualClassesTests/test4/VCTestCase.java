package generated.test4;

import junit.framework.*;
import java.util.*;

/**
 * Expression Problem
 *
 * @author Ivica Aracic
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public StringBuffer result = new StringBuffer();

	public void test() {
        System.out.println("-------> VCTest 4: start");

		EvalPrettyPrintNegAST ast = new EvalPrettyPrintNegAST();
		EvalPrettyPrintNegAST.Literal l1 = ast.new Literal();
		EvalPrettyPrintNegAST.Literal l2 = ast.new Literal();
		EvalPrettyPrintNegAST.AddExpression add = ast.new AddExpression();
		EvalPrettyPrintNegAST.NegExpression neg = ast.new NegExpression();

		l1.init(5);
		l2.init(4);
		add.init(l1, l2);
		neg.init(add);


		System.out.println(neg.print()+" = "+neg.eval());

        System.out.println("-------> VCTest 4: end");
	}
}

//=========================================================
public cclass AST  {

	public cclass Expression {
		public Expression() {
		}
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
			return l.eval() + r.eval();
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
		public String print() {return "";}
	}

	public cclass AddExpression {
		public String print() {
			return "(" + l.print() + "+" + r.print() + ")";
		}
	}

	public cclass Literal {
		public String print() {
			return ""+val;
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
public cclass EvalPrettyPrintNegAST extends EvalAST & PrettyPrintAST & NegAST {
	public cclass NegExpression {
		public int eval() {
			return -expr.eval();
		}
		public String print() {
			return "-(" + expr.print() + ")";
		}
	}
}

//=========================================================
