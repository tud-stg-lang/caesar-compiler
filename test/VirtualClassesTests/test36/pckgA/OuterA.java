package generated.test36.pckgA;

public cclass OuterA
{
	public cclass InnerA
	{
		public String getA() { return "A.A"; }
	}

	public cclass InnerB extends InnerA
	{
		public String getA() { return super.getA() + ":A.B"; }
	}
}
