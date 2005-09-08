package generated.test139;

public cclass AspectB extends AspectA {
	before() : call(* test(..)) {
		ADTestCase.result.append(":before testB");
	}
}

