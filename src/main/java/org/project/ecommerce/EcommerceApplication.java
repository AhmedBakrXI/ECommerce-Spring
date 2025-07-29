package org.project.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.security.*;
import java.util.Base64;

@SpringBootApplication
@EnableCaching
public class EcommerceApplication {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		SpringApplication.run(EcommerceApplication.class, args);
//		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
//		generator.initialize(2048);
//		KeyPair keyPair = generator.generateKeyPair();
//		PrivateKey privateKey = keyPair.getPrivate();
//		PublicKey publicKey = keyPair.getPublic();
//
//		String publicKeyString = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(publicKey.getEncoded());
//		String privateKeyString = Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(privateKey.getEncoded());
//		System.out.println(publicKeyString);
//		System.out.println();
//		System.out.println(privateKeyString);
	}

}
