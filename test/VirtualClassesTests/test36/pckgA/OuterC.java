package generated.test36.pckgA;

public cclass OuterC extends OuterB & OuterA
{
    public cclass InnerC extends InnerA
	{
		public String getA() { return super.getA() + ":C.C"; }
	}

	public cclass InnerE extends InnerC & InnerB
	{
		public String getA() { return super.getA() + ":C.E"; }
	}

	public cclass InnerF extends InnerD & InnerB
	{
		public String getA() { return super.getA() + ":C.F"; }
	}
}