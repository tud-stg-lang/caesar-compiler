package generated;

deployed public class StaticAspect {
	
	private StaticAspect() {}

	pointcut callAnyMethod() : call(* m(..));

	before() : callAnyMethod() {
		System.out.println("Statically deployed Before-Advice.");
	}

	after() : callAnyMethod() {
		System.out.println("Statically deployed After-Advice.");
	}

}
