package generated;

public class ObserverProtocolWeavelet 
	extends ObserverProtocol(ObserverProtocolImpl, ObserverProtocolBinding)
{
	public static void main(String[] args)
	{
		ObserverProtocolWeavelet weavelet = new ObserverProtocolWeavelet();
		System.out.println(weavelet.getNameParent("x"));
	}
}