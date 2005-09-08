package generated.test140;

import junit.framework.TestCase;

import java.util.List;

abstract public cclass AspectA
{
	abstract pointcut pcutA(List lst);
	
	before(List lst) : pcutA(lst) {
		ADTestCase.result.append(":before testA");
	}	
}



