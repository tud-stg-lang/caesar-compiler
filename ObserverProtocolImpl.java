package generated;

import java.util.ArrayList;
import java.util.Iterator;

public clean class ObserverProtocolImpl implements ObserverProtocol 
{
	public String getNameParent(String child)
	{
		return "";
	}
	
	virtual public class Subject
	{
		private ArrayList observers = new ArrayList();
		public void addObserver(Observer o)
		{
			observers.add(o);
		}
		public void removeObserver(Observer o)
		{
			observers.remove(o);
		}
		public void changed()
		{
			for (Iterator iter = observers.iterator(); iter.hasNext();)
				((Observer)iter.next()).notify(this);
		}

	}
	
	virtual public class Observer
	{
		public void setSubject(Subject s)
		{
		}		
	}
}