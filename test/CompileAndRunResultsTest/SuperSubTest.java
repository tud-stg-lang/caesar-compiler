package generated;

import junit.framework.TestCase;

public class SuperSubTest extends TestCase {

	public SuperSubTest() {
		super( "test" );
	}
	
	public void test() {
		SuperSubOuter.SuperSubInnerSubSub s = new SuperSubOuter().new SuperSubInnerSubSub();
		SuperSubOuter.SuperSubInnerSub _super = s.fjSuper;
		SuperSubOuter.SuperSubInner superSuper = _super.fjSuper;
		org.caesarj.runtime.Child sub = superSuper.fjSub;
		org.caesarj.runtime.Child subSub = sub.fjSub;
		assertEquals( superSuper.getClass().getName(), "Class_fj_generated.SuperSubOuter$SuperSubInnerSubSub_Impl_fj_generated.SuperSubOuter$SuperSubInner_Impl" );
		assertEquals( sub.getClass().getName(), "Class_fj_generated.SuperSubOuter$SuperSubInnerSubSub_Impl_fj_generated.SuperSubOuter$SuperSubInnerSub_Impl" );
		assertEquals( subSub.getClass().getName(), "generated.SuperSubOuter$SuperSubInnerSubSub_Impl" );
		new SuperSubOuterSub().test();
	}
}

class SuperSubOuter {
	public virtual class SuperSubInner {}
	public virtual class SuperSubInnerSub extends SuperSubInner {}
	public virtual class SuperSubInnerSubSub extends SuperSubInnerSub {}
}

class SuperSubOuterSub extends SuperSubOuter {
	public override class SuperSubInnerSub {
		public void m() {}
	}	
	public void test() {
		new SuperSubInnerSubSub().fjSuper.m();
	}
}