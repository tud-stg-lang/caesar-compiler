package generated;

public class SuperAspect {

	pointcut execMethod() : execution(* m(..));

	before() : execMethod() {
		System.out.println("SuperAspect Before: " + thisJoinPoint.toString());
	}

}