package generated;

public class ObserverProtocolWeavelet 
	extends ObserverProtocol(ObserverProtocolImplSub, ObserverProtocolBindingSub)
{
	public ObserverProtocolWeavelet(String a)
	{
		super(a);
		System.out.println("HEHEHEHEEHEH");
		
	}
	public ObserverProtocolWeavelet()
	{
		this("");
		System.out.println("WithoutParam");
	}

	public static void main(String[] args)
	{
		final ObserverProtocolWeavelet weavelet = new ObserverProtocolWeaveletSub();
		//final ObserverProtocolBinding binding = weavelet._getBinding();
		System.out.println(weavelet.getNameParent("x"));
		weavelet.SubjectBinding subject = weavelet.new SubjectBinding();
		weavelet.ObserverBindings observer = weavelet.new ObserverBindings();
		weavelet._getProviding().getNameParent("x");
		subject.addObserver(observer);
		subject.changed();
		subject.setObserver(observer);
		observer.setSubject(subject);
	}
}