package generated;

public collaboration interface MediatorProtocol {
   
  public interface Mediator {
	//Only expected contract
	public expected void colleagueChanged(
	  Colleague colleague);
  }
   
  public interface Colleague {
	//Both here.
	public provided void setMediator(Mediator mediator);
	public provided Mediator getMediator();
	public expected String getState();
  }
}