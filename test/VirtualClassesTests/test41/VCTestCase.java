package generated.test41;

import junit.framework.*;
import java.util.*;

/**
 * Array Test
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public static final String expectedResult = "";

	public void test() {
		System.out.println("-------> VCTest 41: Print expressions");

		ExpressionFeatureImpl.Expression expr = new ExpressionFeatureImpl().new Expression();

		PrintExpressionComp comp = new PrintExpressionComp();

		deploy(comp) {
			System.out.println("aaa");
			expr.doSomething();
		}

        System.out.println("-------> VCTest 41: end");
	}
}