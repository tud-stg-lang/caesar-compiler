package generated.test36.pckgA;

public cclass OuterB
{
	public cclass InnerC
	{
		public String getA() { return "B.C"; }
	}

	public cclass InnerD extends InnerC
	{
		public String getA() { return super.getA() + ":B.D"; }
	}
}