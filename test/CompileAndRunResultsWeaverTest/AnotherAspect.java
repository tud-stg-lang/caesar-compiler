package generated;

public cclass AnotherAspect {

	pointcut execMethod() : execution(* m(..));

	before() : execMethod() {
		System.out.println("AnotherAspect Before: " + thisJoinPoint.toString());
	}

}