package generated;
/**
 * An implementation of Mediator protocol.
 */
public class MediatorProtocolImpl 
  provides MediatorProtocol {
  
  public class Mediator 
  {
  	public boolean isPossible()
  	{
  		return true;
  	}
  }
  //Collegue is implemented here
  public class Colleague {
	private Mediator mediator;
	public void setMediator(Mediator mediator) {
		if (mediator.isPossible())
			System.out.println("Oi rolou!");
	  this.mediator = mediator;	
	}
	public Mediator getMediator() {
	  return mediator;
	}
  }
}