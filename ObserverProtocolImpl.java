package generated;

import java.util.ArrayList;
import java.util.Iterator;

public class ObserverProtocolImpl implements ObserverProtocol 
{
	public String getNameParent(String child)
	{
		System.out.println("ObserverProtocolImpl.getNameParent(" + child + ")");
		return "ObserverProtocolImpl";
	}
	
	virtual public class Subject
	{
		private ArrayList observers = new ArrayList();
		public void addObserver(Observer o)
		{
			System.out.println("Subject.addObserver(" + o + ")");
			observers.add(o);
		}
		public void removeObserver(Observer o)
		{
			System.out.println("Subject.removeObserver(" + o + ")");
			getNameParent("X");
			observers.remove(o);
		}
		public void changed()
		{
			System.out.println("Subject.changed()");
			for (Iterator iter = observers.iterator(); iter.hasNext();)
				((Observer)iter.next()).notify(this);
		}

	}
	
	virtual public class Observer
	{
		public void setSubject(Subject s)
		{
			System.out.println("Observer.setSubject(" + s + ")");
		}		
	}
}