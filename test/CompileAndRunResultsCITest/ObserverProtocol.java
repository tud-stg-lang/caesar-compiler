package generated;

public collaboration interface ObserverProtocol extends ObserverProtocolParent
{
	public override interface Subject
	{
		public provided void addObserver(Observer o);
		public provided void removeObserver(Observer o);
		public provided void changed();
		public expected Object getState();
    }
	
	public override interface Observer
	{
		public expected void notify(Subject s);
	}
}
