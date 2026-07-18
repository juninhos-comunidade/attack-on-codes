package com.attackoncodes.worksync.logic.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.ZonedDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.io.Decoders;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class KeyUtils {

	@Value("${spring.application.name:default-app}")
	private String applicationName;
	
	public RSAPublicKey readPublicKey(Resource resource) {
		try {
			String content = new String(resource.getInputStream().readAllBytes());
	        String key = content.replaceAll("(?s).*-----BEGIN PUBLIC KEY-----(.*)-----END PUBLIC KEY-----.*", "$1")
	                            .replaceAll("\\s", "");
			byte[] decode = Decoders.BASE64.decode(key);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(decode);
			return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
		} catch (Exception e) {
			throw new RuntimeException("Could not load public key", e);
		}
	}

	public RSAPrivateKey readPrivateKey(Resource resource) {
		try {
			String content = new String(resource.getInputStream().readAllBytes());
	        String key = content.replaceAll("(?s).*-----BEGIN PRIVATE KEY-----(.*)-----END PRIVATE KEY-----.*", "$1")
	                            .replaceAll("\\s", "");
			byte[] decode = Decoders.BASE64.decode(key);
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decode);
			return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
		} catch (Exception e) {
			throw new RuntimeException("Could not load private key", e);
		}
	}
	
	public boolean isKeySystemGenerated(Resource resource) {
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
	        String firstLine = reader.readLine();
	        return firstLine != null && firstLine.startsWith("System-ID:");
	    } catch (Exception e) {
	        return false;
	    }
	}

	public void saveKeyToFile(Key key, String fileName, String header, String folderPath) {
		try {
			String cleanPath = folderPath.replace("classpath:", "").replace("file:", "");
	        File directory = new File(cleanPath).getAbsoluteFile();

			if (!directory.exists()) {
				boolean created = directory.mkdirs();
				if (created) {
					log.info("Created directory: {}", directory.getAbsolutePath());
					return;
				}
			}

			if (!directory.canWrite()) {
	            log.error("No write permission for directory: {}", directory.getAbsolutePath());
	            return;
	        }
			
			File keyFile = new File(directory, fileName);
			String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
			try (PrintWriter writer = new PrintWriter(keyFile)) {
				writer.printf("System-ID: %s\n", applicationName); 
			    writer.printf("Generated-At: %s\n", ZonedDateTime.now());
				writer.printf("-----BEGIN %s-----\n", header);
				writer.println(encodedKey.replaceAll("(.{64})", "$1\n"));
				writer.printf("-----END %s-----\n", header);
			}
			log.info("Saved: {}", keyFile.getAbsolutePath());
		} catch (IOException e) {
			log.error("Could not save key to file: {}", e.getMessage());
		}
	}
}
