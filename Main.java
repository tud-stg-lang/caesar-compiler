package generated;
 
import javax.swing.*;
import java.awt.event.*;

public class Main {

	static JFrame frame   = new JFrame("Mediator Demo"); 
	static Button button1 = new Button("Button1");
	static Button button2 = new Button("Button2");
	static Label  label   = new Label ("Click a button!");
 
    public void foo() { }
    
	public static void main(String[] args) {

		final MediatorProtocolWeavelet weavelet = 
			new MediatorProtocolWeavelet();

		weavelet.MediatorBinding mediator = weavelet.MediatorBinding(label);
		weavelet.ColleagueBinding colleague1 = weavelet.ColleagueBinding(button1);
		weavelet.ColleagueBinding colleague2 = weavelet.ColleagueBinding(button2);

		colleague1.setMediator(mediator);    
		colleague2.setMediator(mediator);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}
		});
	
		JPanel panel = new JPanel();
		
		panel.add(label);
		panel.add(button1);
		panel.add(button2); 
			
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		
		deploy(weavelet) {
			button1.clicked();
			new Main().foo();
		}
//		mediator.colleagueChanged(colleague1);
	}
}