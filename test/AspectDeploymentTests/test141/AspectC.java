package generated.test141;

public cclass AspectC extends AspectA {
	before() : call(* test(..)) {
		ADTestCase.result.append(":before testC");
	}
}

