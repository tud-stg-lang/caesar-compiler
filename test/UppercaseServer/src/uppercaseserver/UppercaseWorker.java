package uppercaseserver;

import java.io.*;
import java.net.*;

/**
 * Worker thread that handles a client session.
 */
public class UppercaseWorker implements Runnable {
	public Socket _requestSocket;

	public UppercaseWorker(Socket requestSocket) throws IOException {
		System.out.println("Creating new worker");
		_requestSocket = requestSocket;
	}

	public void run() {
		BufferedReader requestReader = null;
		Writer responseWriter = null;
		
		try {
			
			requestReader =
				new BufferedReader(
					new InputStreamReader(_requestSocket.getInputStream()));
			responseWriter =
				new OutputStreamWriter(_requestSocket.getOutputStream());

			boolean exit = false;
			while (!exit) {

				exit = handleRequest(requestReader, responseWriter);

			}

		} catch (IOException ex) {
		} finally {
			try {
				if (responseWriter != null) {
					responseWriter.close();
				}
				if (requestReader != null) {
					requestReader.close();
				}
				_requestSocket.close();
			} catch (IOException ex2) {
			}
		}
		System.out.println("Ending the session");
	}

	private boolean handleRequest(
		BufferedReader requestReader,
		Writer responseWriter)
		throws IOException {

		String requestString = requestReader.readLine();
		if (requestString == null || requestString.equals("exit")) {
			return true;
		}
		System.out.println("Got request: " + requestString);
		responseWriter.write(requestString.toUpperCase() + "\n");
		responseWriter.flush();

		return false;
	}
}
