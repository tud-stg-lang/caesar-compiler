package generated;

import junit.framework.TestCase;

public class BugReport1 extends TestCase {
	
	public BugReport1() {
		super( "test" );
	}
	
	/**
	 * tests, if inter-family casts fail as expected.
	 * @throws Throwable
	 */
	public void test() throws Throwable {

		final TestingSuperclassExtended e = new TestingSuperclassExtended();
		e.Sub s = e.new Sub();
		s.n();
		
		new Test1();
		new ToStringSub().toString();
		
		final X a = new X();
		final X b = new X();
		X.Inner x = a.new Inner();
		a.Inner ax = (a.Inner) x;
		try {
			b.Inner bx = (b.Inner) x;
			fail( "cast must runtime-fail" );
		} catch( RuntimeException e2 ) {
			assertEquals( "cast to given family not possible", e2.getMessage() );
		}
	}
}

/**
 * helper for test(), supplying a family enclosure for Inner().
 * 
 */
class X {
	virtual class Inner {
		public void foo() {
			System.out.println("X.inner.foo");
		}
		public void _bar(Inner i2) {
			System.out.println("X.Inner.bar");
		}
	}
	virtual class Inner2 extends Inner {
		public void foo() { System.out.println("X.Inner2.foo");
			super.foo();
		}
	} 
}
class Y extends X {
	override class Inner {
		public void foo() {
			System.out.println("Y.Inner.foo");
			super.foo();
		}
		/*
		public void bar(Inner i2) {
			System.out.println("X.Inner.bar");
			i2.baz();
		}*/

		public void baz() {
		}
	}
	void test(Inner __i_) {
		__i_.baz();
	}
}
class Z {
	private final X x;	
	public Z(X x) { this.x = x; }
	virtual class Inner extends x.Inner {}
	virtual class ZInner extends x.Inner2 {
		public void foo() {
			System.out.println("Z.ZInner.foo" + x);
			x.Inner i = x.new Inner2();
			i._bar( this );
			Inner j__ = new Inner();
			j__._bar( j__ );
			_bar( j__ );
			super.foo();
		}
	}
}

class TestingSuperclassFieldAccess {

    public virtual class Super{
        public void m(){
            System.out.println("m in Super");
        }
    }
}
class TestingSuperclassExtended extends TestingSuperclassFieldAccess {

    //careful extends virtual Object!!!
    public virtual class Sub{
        public void n(){
            System.out.println("n in Sub");

            //this code doesn't works if Super is not overriden in this class
            Super v = new Super();
            v.m();
        }

        //this doesn't works!!!!! the super.toString() doesn't find appropiate method
        //error:Cannot find method "familyj.runtime.Child._toString_selfContext(java.lang.Object)

        public String toString() {        	
            return super.toString();
        }
        
        public void finalize() throws Throwable {
        	super.finalize();
        }

    }
}

class TestingConstructor {
    public virtual class Build{
        private int f;
        private int g;

        public Build(int a){
            this(a, 0);
        }

        public Build(int a, int b){
            f=a;
            g=b;
        }
        public int get(){
            return f+g;
        }
    }
}

class TestCastingVirtualClasses {
    public virtual class C{
        public void m(){
            System.out.println("m in C");
        }
    }
    public virtual class UseC{
        private Object c;
        public void casting(Object var){
            System.out.println("casting to C ");
            ((C)var).m();
            c = var;
        }

/*
       Next two methods:
        public void useObjectAsC()
        public C get()

        crashes the familyj compiler!!!

  /*      java.lang.NullPointerException
	at familyj.compiler.FjMethodCallExpression.getFamily(FjMethodCallExpression.java:420)
	at familyj.compiler.FjTypeSystem.checkFamilies(FjTypeSystem.java:92)
	at familyj.compiler.FjVariableDefinition.analyse(FjVariableDefinition.java:90)
	at at.dms.kjc.JVariableDeclarationStatement.analyse(JVariableDeclarationStatement.java:99)
	at at.dms.kjc.JBlock.analyse(JBlock.java:88)
	at at.dms.kjc.JMethodDeclaration.checkBody1(JMethodDeclaration.java:221)
	at at.dms.kjc.JClassDeclaration.checkTypeBody(JClassDeclaration.java:478)
	at familyj.compiler.FjVirtualClassDeclaration.checkTypeBody(FjVirtualClassDeclaration.java:118)
	at at.dms.kjc.JClassDeclaration.checkTypeBody(JClassDeclaration.java:461)
	at at.dms.kjc.JCompilationUnit.checkBody(JCompilationUnit.java:248)
	at at.dms.kjc.Main.checkBody(Main.java:409)
	at at.dms.kjc.Main.run(Main.java:183)
	at familyj.compiler.Main.run(Main.java:44)
	at familyj.compiler.Main.compile(Main.java:40)
	at familyj.compiler.Main.main(Main.java:35)
*/
        public void useObjectAsC(){
            C var = get();
            var.m();
        }
        public C get(){
           return (C)c;
        }

          /*
        Note that this method works!!
    */
    public void useCastingOutside(){
          Object var = get();
           ((C)var).m();
           //by the way, this also works:
           get().m();
        }


    }

    public static void main(String[] args){
      // this works:
      final TestCastingVirtualClasses f = new TestCastingVirtualClasses();
      f.C varC = f.new C();
      f.UseC useC = f.new UseC();
      useC.casting(varC);


    }
}

class TestCastingInterface {
    public interface I{
        public void m();
    }

    public virtual class C implements I{
        public void m(){
            System.out.println("m in C");
        }
    }

    public virtual class UseC{
        private Object c;
        public void casting(Object var){
            System.out.println("casting to I ");
            ((I)var).m();
            c = var;
        }

/*
       Next two methods:
        public void useCasI()
        public I get()

        crashes the familyj compiler!!!
*/
  /*      java.lang.NullPointerException
	at familyj.compiler.FjMethodCallExpression.getFamily(FjMethodCallExpression.java:420)
	at familyj.compiler.FjTypeSystem.checkFamilies(FjTypeSystem.java:92)
	at familyj.compiler.FjVariableDefinition.analyse(FjVariableDefinition.java:90)
	at at.dms.kjc.JVariableDeclarationStatement.analyse(JVariableDeclarationStatement.java:99)
	at at.dms.kjc.JBlock.analyse(JBlock.java:88)
	at at.dms.kjc.JMethodDeclaration.checkBody1(JMethodDeclaration.java:221)
	at at.dms.kjc.JClassDeclaration.checkTypeBody(JClassDeclaration.java:478)
	at familyj.compiler.FjVirtualClassDeclaration.checkTypeBody(FjVirtualClassDeclaration.java:118)
	at at.dms.kjc.JClassDeclaration.checkTypeBody(JClassDeclaration.java:461)
	at at.dms.kjc.JCompilationUnit.checkBody(JCompilationUnit.java:248)
	at at.dms.kjc.Main.checkBody(Main.java:409)
	at at.dms.kjc.Main.run(Main.java:183)
	at familyj.compiler.Main.run(Main.java:44)
	at familyj.compiler.Main.compile(Main.java:40)
	at familyj.compiler.Main.main(Main.java:35)*/


/*
        the problem seem to be when determining the compatibility
        between the type of get() and the type of var. Next method crashes:
*/
        public void useCasI(){
            I var = get();
            var.m();
        }


        public I get(){
           return (I)c;
        }


    /*
        Note that this method works!!
    */
    public void useCasICastingOutside(){
            Object var = get();
            ((I)var).m();
        }

    }

    public static void main(String[] args){
      // this works:
      final TestCastingInterface f = new TestCastingInterface();
      f.I varC = f.new C();
      f.UseC useC = f.new UseC();
      useC.casting(varC);


    }
}

class TestingFamilyToCompose {

    public virtual class C{
        public void m(){

        }
    }
    public virtual class T{
        public void mT(){

        }
    }

}

class TestingComposedFamily {
   private static final TestingFamilyToCompose _f= new TestingFamilyToCompose();

    public virtual class Composed{

        private _f.C var;

        public Composed(){
            setVar(_f.new C());
        }
        public void print(){
            //System.out.println("printing var: " + var);
        }

        //not allowed for the compiler: error message:
        //Cannot find type "f/C"
        //if I want to compose my family with other family I can't pass the field
        //to any method, because it is not possible to define a method that use as
        // parameter a virtual class defined in another family!
        public void setVar(_f.C c){
            //var = c;
        }
    }
   public static void main(String[] args){
         final TestingComposedFamily cf = new TestingComposedFamily();
         cf.Composed compo = cf.new Composed();
         compo.print();

   }
}


class Test1 {
	public Test1() {
		System.out.println("Anfang");
		final Y_ y = new Y_();
		y.SubInner i = y.new SubInner();
		i.baz();
		System.out.println("Fertich");
	}
}

clean class ToString {
	public String toString() { return "ToString " + super.toString(); }
}

/**
 * tests a clean class inheriting from a clean class.
 */
clean class ToStringSub extends ToString {
	public String toString() { return "ToStringSub " + super.toString(); }
}

class EncapsulatesAnX {
	private final X x = new X();
	public virtual class NewInner extends x.Inner {}
}