package uppercaseserver;

// UppercaseServer.java
import java.io.*;
import java.net.*;

public class UppercaseServer {
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Usage: java UppercaseServer <portNum>");
			System.exit(1);
		}

		int portNum = Integer.parseInt(args[0]);
		ServerSocket serverSocket = new ServerSocket(portNum);

		while (true) {
			Socket requestSocket = serverSocket.accept();
			Thread serverThread =
				new Thread(new UppercaseWorker(requestSocket));
			serverThread.start();
		}
	}
}

