
package io.aimmo.api;

import java.nio.file.Files; 
import java.nio.file.Path;
import java.io.IOException;

public class CLI {

	public static void die (String message) {
		System.err.println(message);
		return; 
	}

	public static String readFile (String file) throws IOException {
		return Files.readString(Path.of(file)); // java 11
	}

	public static void main(String... args) {
		try {

			String keyId = System.getenv("KEY_ID");
			String keySecret = System.getenv("KEY_SECRET"); 
			boolean verbose = false;		

			String file;
		       
			if ( keyId == null || keySecret == null || args.length < 1 || args.length > 2 || ( args.length == 2 && "-v" != args[0]) ) {
				die ("USAGE: KEY_ID=... KEY_SECRET=... mvn compile exec:java CLI.class [-v] file.json"); 
				return; 
			}
			
			file = args[0];

			if ( args.length == 2 && "-v" == args[0] ) { 
				verbose = true;
				file = args[1];
			} 

			Client client = new Client (keyId, keySecret);

			if (verbose) { 
				client.setVerbose();
		 	}	

			String response = client.postProtocol(readFile(file)); 

			System.out.println(response); 

			System.out.println("Response: " + response);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

