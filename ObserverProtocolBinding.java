package generated;

public class ObserverProtocolBinding binds ObserverProtocol 
{
	public Object getNameParentExpected(String child)
	{
		return "";
	}
	virtual class SubjectBinding binds Subject
	{
		public void setObserver(Observer o)
		{
		}
		public Object getState()
		{
			return "MyState";
		}
    }
	
	virtual class ObserverBinding binds Observer
	{
		public void notify(Subject s)
		{
			System.out.println(s.getState());
		}
	}
}