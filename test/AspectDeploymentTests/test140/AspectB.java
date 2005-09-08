package generated.test140;

import java.util.List;

public cclass AspectB extends AspectA {
	pointcut pcutA(List lst) : call(* test(List)) && args(lst);
	
	before() : call(* test(..)) {
		ADTestCase.result.append(":before testB");
	}
}

