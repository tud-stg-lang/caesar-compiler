package generated;

/**
 * A binding of MediatorProtocol
 * @author Walter Augusto Werner
 */
public class MediatorProtocolBinding 
  binds MediatorProtocol {
   
  public class MediatorBinding binds Mediator wraps Label
  {
	public void colleagueChanged(
	  Colleague colleague) {
	  wrappee.setText(colleague.getState() + " clicked");
   }
  }

  public class ColleagueBinding binds Colleague wraps Button 
  {
     
	public String getState() {
	  return wrappee.getText();
	}
  }

  public pointcut change(Button b): 
	  (call(void Button.clicked()) && target(b)) ;
	  
  after(Button b): change(b) {
	   ColleagueBinding(b).getMediator().colleagueChanged(ColleagueBinding(b));
   }
   public pointcut change2(): 
	   (call(void Main.foo()))  ;
	  
   after(): change2() {
		System.out.println("change2");
	}

}
