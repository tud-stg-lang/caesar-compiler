package generated;
public class Test {
	
  public virtual class Inner {
    public int m() { return 1; }
    public int n() { return 1; }
    public int k() { return 1; }
	public Inner getThis() {
		return this;
	}
		
    public virtual class InnerInner extends Inner {
      public int m() { return 2; }
    }
  }
  
  public virtual class NewInner extends Inner {
  	public int m() { return super.m() + 1; }
  }

  public virtual class A {
  	private String s;
  	public A( String s ) {
  		this.s = s;
  	}
	  public virtual class AInner extends A {
	  		public AInner() {
	  			super( "" );
	  		}
		  public void foo() {
			System.out.println("Test1.A.AInner.foo"); super.foo(); 
		  }
	  }
	  public void foo() {
	  	System.out.println("Test1.A.foo");    
	  }
  }
  public virtual class B extends A {
  	public B( String s ) {
  		super( s );
  	}
	override class AInner {
	  public void foo() {
		System.out.println("Test1.B.AInner.foo"); super.foo();
	  }
	}
	public void foo() { 
		System.out.println("Test1.B.foo");
		super.foo();
	}
  }
  
  public Test() {
  	new B( "" ).new AInner();  	
	  /*
	  A a = new B( "" );
	  a.new AInner().foo();
	  System.out.println("Foo");
	  */
  }
}