package generated;

public collaboration interface ObserverProtocol
{
	public provided String getName();
	public expected String getNameExpected();
	
	public interface Subject
	{
		public provided void addObserver(Observer o);
		public provided void removeObserver(Observer o);
		public expected void changed();
		public provided Object getState();
    }
	
	public interface Observer
	{
		public expected void notify(Subject s);
	}
}