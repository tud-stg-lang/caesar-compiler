package generated;

import junit.framework.TestCase;

public class KlausTest extends TestCase {
	public KlausTest() {
		super("test");
	}
	public void test() {
		final Foo f = new Foo();
		f.Goo g = f.new Goo();
		deploy(new FooAspect2("Outer")) {
			f.foo();
			g.goo();
			
		}
	}
}



crosscutting class FooAspect {
	private String s;
	public FooAspect(String s) { 
		this.s = s;
	}
	pointcut methodCall() : call(* bar());

	before() : methodCall() {
		System.out.println("Before bar ");
	}

	after() : methodCall() {
		System.out.println("After bar ");
	}
 
}

crosscutting class FooAspect2 extends FooAspect {
	public FooAspect2(String s) { 
		super(s);
	}
	pointcut methodCall2() : call(* generated.Fooo.Goo.goo()) ;


    after(): methodCall2() {
    	System.out.println("After Foo.goo.goo"); 
    }
}

clean class Fooo {
	virtual class Goo {
		public void goo() { 
			System.out.println("Foo.Goo.goo");
		}
	}
	public void foo() {
		System.out.println("Fooo.foo");
	}
}

clean class Foo extends Fooo {
  public void foo() { 
  	System.out.println("Entering foo");
	bar();
	System.out.println("Leaving foo");
  }
  public void bar() {
	System.out.println("Entering bar"); 
  	System.out.println("Leaving bar"); 
  }
}




 