package generated.test139;

import junit.framework.TestCase;

import java.util.List;

abstract public cclass AspectA
{
	before(List lst) : call(* test(List)) && args(lst) {
		ADTestCase.result.append(":before testA");
	}	
}



