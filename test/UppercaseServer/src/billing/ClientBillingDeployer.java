package billing;

import org.caesarj.runtime.CaesarThread;
import uppercaseserver.*;
import java.io.*;
import java.net.*;

/**
 * Deploys billing on the client.
 */
public privileged deployed class ClientBillingDeployer {

	pointcut setRequestSocket(UppercaseClient client) : set(* requestSocket) && target(client);

	pointcut execRun() : execution(* UppercaseClient.run(..));

	/**
	 * After the requestSocket is available, ask the use for the billing-mode
	 * and send the answer to the server.
	 */
	after(UppercaseClient client) : setRequestSocket(client) && cflow(execRun()) {
			
		try {
			Writer modeWriter =
				new OutputStreamWriter(client.requestSocket.getOutputStream());
			BufferedReader modeReader =
				new BufferedReader(new InputStreamReader(System.in));

			System.out.print("Please enter billing mode: ");
			String mode = modeReader.readLine();
			modeWriter.write(mode + '\n');
			modeWriter.flush();

		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
}