package generated;

public class ObserverProtocolWeavelet 
	extends ObserverProtocol(ObserverProtocolImpl, ObserverProtocolBinding)
{
	public static void main(String[] args)
	{
		final ObserverProtocolWeavelet weavelet = new ObserverProtocolWeavelet();
		System.out.println(weavelet.getNameParent("x"));
		weavelet.SubjectWeavelet subject = weavelet.new SubjectWeavelet();
		weavelet.ObserverWeavelet observer = weavelet.new ObserverWeavelet();
		
		subject.addObserver(observer);
		subject.changed();
		
		
		subject.setObserver(observer);
		observer.setSubject(subject);
		
	}
	public class SubjectWeavelet extends Subject(Subject, SubjectBinding)
	{
	}
	public class ObserverWeavelet extends Observer(Observer, ObserverBinding)
	{
	}
}