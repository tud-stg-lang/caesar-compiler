package generated;

import java.util.ArrayList;
import java.util.Iterator;

public clean class ObserverProtocolImplSub extends ObserverProtocolImpl
{
	public String getNameParent(String child)
	{
		System.out.println("ObserverProtocolImplSub.getNameParent(" + child + ")");
		//getNameParentExpected(child);
		return "ObserverProtocolImpl";
	}

	public override class Subject
	{
		private ArrayList observers = new ArrayList();
		public void addObserver(Observer o)
		{
			System.out.println("SubjectSub.addObserver(" + o + ")");
			observers.add(o);
		}
		public void removeObserver(Observer o)
		{
			System.out.println("SubjectSub.removeObserver(" + o + ")");
			getNameParent("X");
			observers.remove(o);
//			for (int i = 0; i < observers.length; i++)
//			{
//				if (observer[i] == o)
//					observer[i] = null;	
//			}
		}
		public void changed()
		{
			System.out.println("SubjectSub.changed()");
//			for (int i = 0; i < observers.length; i++)
//				if (observers[i] != null) observers[i].notify(this);
			for (int i = 0; i < observers.size(); i++)
				((Observer)observers.get(i)).notify(this);
			//System.out.println(iter.next());
		}

	}
	
	public override class Observer
	{
		public void setSubject(Subject s)
		{
			System.out.println("ObserverSub.setSubject(" + s + ")");
		}		
	}
}