package generated;

public collaboration interface ObserverProtocolParent
{
	public provided String getNameParent(String child);
	public expected String getNameParentExpected(String child);
	public interface Subject
	{
		public expected void setObserver(Observer o);
	}
	
	public interface Observer
	{
		public provided void setSubject(Subject s);
	}
	
}