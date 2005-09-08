package generated.test139;

public cclass AspectC extends AspectA {
	before() : call(* test(..)) {
		ADTestCase.result.append(":before testC");
	}
}

