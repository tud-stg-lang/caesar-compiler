package generated;

public class SimpleAspect /* extends SuperAspect*/ {

	pointcut callMethod(int i) : call(* m(..)) && args(i);

	//	pointcut execMethod() : call(* m(..));	

	before(int j) : callMethod(j) {
		System.out.println(
			"Before-Advice execution in "
				+ this.toString()
				+ " in "
				+ Thread.currentThread().toString());
	}

	after(int j) : callMethod(j) {
		System.out.println(
			"After-Advice Execution in "
				+ this.toString()
				+ " in "
				+ Thread.currentThread().toString());
	}
	
}
