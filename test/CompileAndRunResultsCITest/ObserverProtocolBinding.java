package generated;

public class ObserverProtocolBinding binds ObserverProtocol 
{
	public String getNameExpected()
	{
		return "";
	}
	virtual class SubjectBinding binds Subject
	{
		public void changed()
		{
			
		}
	}
	
	virtual class Observer binds Observer
	{
		public void notify(Subject s)
		{
			
		}
	}	
}