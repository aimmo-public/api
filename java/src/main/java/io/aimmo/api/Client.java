
package io.aimmo.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.net.ProtocolException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Iterator; 

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.digest.DigestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

public class Client {

	public static final String STAGE_API = "https://staging.aimmo.io/api/v2/partners/";
        public static final String PRODUCTION_API = "https://app.aimmo.io/api/v2/partners/";

	private String keyId; 
	private String keySecret;
	private String api;

	private Algorithm algorithm;
	private DigestUtils digest; 

	protected boolean verbose = false; 

	public Client(String keyId, String keySecret) {
		this(keyId, keySecret, STAGE_API);  
	}

	public Client(String keyId, String keySecret, String api) {
		this.keyId = keyId; 
		this.keySecret = keySecret; 
		this.api = api; 

		this.algorithm = Algorithm.HMAC256(keySecret);
		this.digest = new DigestUtils("SHA-256");
	}

	public String postProtocol(String body) throws MalformedURLException, ProtocolException, IOException {
		return post(api, body);
	}

	public void setVerbose() {
		this.verbose=true;
	}

	protected void log(String message) {
		if (verbose) 
		   System.out.println(message); 
	}

	protected String dumpHeaders(Map<String, List<String>> headers) {
		  StringBuilder sb = new StringBuilder();
  Iterator<?> it = headers.keySet().iterator();
  sb.append('\n');
  for(String name : headers.keySet()) {
    sb.append("\n" + name + ":" + headers.get(name).toString());
  }
  sb.append('\n');
  return sb.toString();

	} 

	protected String post(String api, String body) throws MalformedURLException, ProtocolException, IOException {
			final String method = "POST"; 

			String token = getBearerToken(body);

                        URL url = new URL(api);
                        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setInstanceFollowRedirects(false);
                        conn.setRequestProperty("Authorization", "Bearer " + token);

			conn.setRequestProperty("Accept", "application/json"); 

                        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                        conn.setRequestMethod(method);

			log("\n" + method + " " + api + "\n" );
			log("Authorizaion: Bearer " + token ); 
			log(dumpHeaders(conn.getRequestProperties()));

                        // loaded inputs
                        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                        wr.write(body.getBytes(StandardCharsets.UTF_8));
                        wr.flush();
                        wr.close();

                        String output;
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        StringBuffer response = new StringBuffer();
                        while ((output = in.readLine()) != null) {
                                response.append(output);
                        }

                        in.close();

			String responseBody = response.toString(); 
			int code = conn.getResponseCode();

			String dump = "\nHeaders: " + dumpHeaders(conn.getHeaderFields()) + "\nBody: " + responseBody + "\n";
		        log(dump); 	

			if (201 != code)
				throw new IOException ("Expected status 201, got " + code + dump); 

			return responseBody;

	}

	protected String getBearerToken(String body) {
			return JWT.create() //
			  .withIssuedAt(new Date())//
			  .withIssuer(keyId) //
			  .withSubject(digest.digestAsHex(body)) //
		          .sign(algorithm);
	}


}

