package generated;

public class ObserverProtocolBinding binds ObserverProtocol 
{
	public Object getNameParentExpected(String child)
	{
		System.out.println("ObserverProtocolBinding.getNameParentExpected(" + child + ")");
		return "ObserverProtocolBinding";
	}

	virtual class SubjectBinding binds Subject
	{
		public void setObserver(Observer o)
		{
			System.out.println("SubjectBinding.setObserver(" + o + ")");
		}
		public Object getState()
		{
			System.out.println("SubjectBinding.getState()");
			return "MyState";
		}
    }
	
	virtual class ObserverBindings binds Observer
	{
		public void notify(Subject s)
		{
			System.out.println("ObserverBinding.notify(" + s + ")");
			System.out.println(s.getState());
		}
	}
}