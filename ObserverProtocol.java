package generated;

public collaboration interface ObserverProtocol extends ObserverProtocolParent
{
	public interface Subject
	{
		public provided void addObserver(Observer o);
		public provided void removeObserver(Observer o);
		public provided void changed();
		public expected Object getState();
    }
	
	public interface Observer
	{
		public expected void notify(Subject s);
	}
}