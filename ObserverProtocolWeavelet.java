package generated;

public class ObserverProtocolWeavelet 
	extends ObserverProtocol(ObserverProtocolImpl, ObserverProtocolBinding)
{
	public static void main(String[] args)
	{
		final ObserverProtocolWeavelet weavelet = new ObserverProtocolWeavelet();
		System.out.println(weavelet.getNameParent("x"));
		weavelet.SubjectBinding subject = weavelet.new SubjectBinding();
		weavelet.ObserverBinding observer = weavelet.new ObserverBinding();
		
		subject.addObserver(observer);
		subject.changed();
		
		
		subject.setObserver(observer);
		observer.setSubject(subject);
		
	}

}