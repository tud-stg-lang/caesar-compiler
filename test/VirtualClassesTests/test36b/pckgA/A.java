package generated.test36b.pckgA;

public cclass OuterA
{
	public cclass InnerA
	{
		public String getA() { return "A"; }
	}
}

public cclass OuterB extends OuterA
{
	public cclass InnerA
	{
		public String getA() { return "B"+super.getA(); }
	}
}

public cclass OuterC extends OuterA
{
	public cclass InnerA
	{
		public String getA() { return "C"+super.getA(); }
	}
}

public cclass OuterD extends OuterB & OuterC
{}