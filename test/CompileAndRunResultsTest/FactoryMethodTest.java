package generated;

public class FactoryMethodTest extends junit.framework.TestCase {
	public FactoryMethodTest() {
		super( "test" );
	}
	
	public void test() {
		Test t = new Test();
		Test s = new SubTest();
		Test subs = new SubSubTest();

		assertEquals( "call to this-self", 1, t.new Inner().n() );
		assertEquals( "call to generatd-self-tail", 4, s.new Inner().n() );
		assertEquals( "call to generatd-self-target", -1, subs.new Inner().n() );

		assertEquals( "Test.Inner.m() is 1", 1,
			((Test.Inner)t._createInner()).m() );
		assertEquals( "SubTest.Inner.m() is 4", 4,
			((SubTest.Inner)s._createInner()).m() );
		assertEquals( "Test.NewInner.m() is parent.m()", 2,
			((Test.NewInner)t._createNewInner()).m() );
		assertEquals( "SubTest.NewInner.m() is parent.m()", 5,
			((SubTest.NewInner)s._createNewInner()).m() );
		assertEquals( "s.NewInner() -> s._createNewInner()",
			((SubTest.NewInner)s._createNewInner()).m(), ((SubTest) s).getThis().new NewInner().m() );
		assertNotNull( "is a factory method of the baseclass found?",
			new SubSubTest().new Inner().new InnerInner() );
		assertEquals( "can inner-inner classes be instantiated?", 2,
			new Test().new Inner().new InnerInner().m() );
		assertEquals( "and does inner inner overriding work?", 3,
			((Test)new SubTest()).new Inner().new InnerInner().m() );
			
		Test.Inner left = s.new Inner();
		assertTrue( "does === work - if true", !(left == left.getThis()) && (left === left.getThis()) );
		assertTrue( "does === work - if not true", !(left === s.new Inner()) );
	}
}