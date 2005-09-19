package generated.test141;

public cclass AspectB extends AspectA {
	before() : call(* test(..)) {
		ADTestCase.result.append(":before testB");
	}
}

