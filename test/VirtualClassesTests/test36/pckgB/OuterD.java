package generated.test36.pckgB;

import generated.test36.pckgA.*;

import java.rmi.Remote;

public cclass OuterD extends OuterC
{
	cclass InnerC implements Remote
	{
		public String getA() { return super.getA() + ":D.C"; }
	}
}