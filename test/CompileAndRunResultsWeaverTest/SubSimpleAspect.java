package generated;

public class SubSimpleAspect extends SimpleAspect {

/*	before() : callMethod() {
		System.out.println("SubSimpleAspect: Before " + thisJoinPoint.toString());
	}*/
	
	int around(int j) : callMethod(j) {
		System.out.println("SubSimpleAspect: Around " + j);
	
		return proceed(j);
	}


}
