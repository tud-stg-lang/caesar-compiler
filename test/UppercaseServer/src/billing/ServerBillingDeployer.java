package billing;

import org.caesarj.runtime.CaesarThread;
import uppercaseserver.*;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;

/**
 * Deploys billin on the server.
 * The billing mode is deployed dynamically.
 */
public deployed class ServerBillingDeployer {

	/**
	 * Superimposes the functionality of the UppercaseWorker.run()-method.
	 * Receives the a string from the client, that determines the billing-mode.
	 * Depending on that string a BillingAspect is instantiated.
	 * That BillingAspect is deployed around a proceed-call, that triggers the execution
	 * of the original run()-functionality.
	 */
	void around(UppercaseWorker worker) : execution(* run())
		&& target(worker) {

		String mode = "";
		try {
			BufferedReader modeReader =
				new BufferedReader(
					new InputStreamReader(
						worker._requestSocket.getInputStream()));
			mode = modeReader.readLine();

		} catch (IOException ex) {
		} finally {
		}

		//create the appropriate BillingAspect instance
		BillingAspect billingAspect = null;
		if (mode.equals("flat")) {
			billingAspect = new FlatRateBillingAspect();
			System.out.println("Billing mode: flat rate");
		} else if (mode.equals("request")) {
			billingAspect = new PerRequestBillingAspect();
			System.out.println("Billing mode: per request");
		} else if (mode.equals("letter")) {
			billingAspect = new PerLetterBillingAspect();
			System.out.println("Billing mode: per letter");
		} else if (mode.equals("none")) {
			System.out.println("Billing mode: no billing");
		} else {
			billingAspect = new PerRequestBillingAspect();
			System.out.println("Billing mode: default = per request");
		}

		//deploy the BillingAspect
		deploy(billingAspect) {
			proceed(worker);
		}

		if (billingAspect != null) {
			DecimalFormat df = new DecimalFormat("0.00");
			String price = df.format(billingAspect.getTotalPrice());
			System.out.println("Session price: " + price + " Euro");
		}
	}

}