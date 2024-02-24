package de.jonathan.boss;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.jonathan.boss.error.WrongKeyException;

/**
 * This class contains all the methods for de- and encryption
 * @author Jonathan Boß
 *
 */
public class CryptoUtils {
	
	private static final String ALGORITHM = "RSA";

	/**
	 * This method hashes the given string with SHA-256 and returns it
	 * @param input The string to hash
	 * @return The hashed input
	 */
	public static String hash(String input){
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		
			byte[] hash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
			BigInteger number = new BigInteger(1, hash);  
			StringBuilder hexString = new StringBuilder(number.toString(16));  
	        while (hexString.length() < 32)  
	        {  
	            hexString.insert(0, '0');  
	        }  
	        return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * This method encrypts the parameter 'input' with the key in the parameter 'key'
	 * @param input The string to encrypt
	 * @param key The key to encrypt the input with
	 * @return The encrypted input
	 */
	public static String encryptWithKey(String input, char[] key) {
		String toEncrypt = input == null ? "" : input;
		
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(CryptoUtils.convertCharArrayToByteArray(key), "AES"));
			return Base64.getEncoder().encodeToString(cipher.doFinal(toEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
			return "";
		}
	}
	
	/**
	 * This method encrypts the parameter 'input' with the key in the parameter 'key'
	 * @param input The string to encrypt
	 * @param key The key to encrypt the input with
	 * @return The encrypted input
	 */
	public static byte[] encryptWithKeyToByteArray(String input, char[] key) {
		String toEncrypt = input == null ? "" : input;
		
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(CryptoUtils.convertCharArrayToByteArray(key), "AES"));
			return cipher.doFinal(toEncrypt.getBytes("UTF-8"));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
			return new byte[0];
		}
	}
	
	/**
	 * This method decrypts the parameter 'input' with the key given in the parameter 'key'
	 * @param input The string to decrypt
	 * @param key The key to decrypt the input with
	 * @return The decrypted input
	 * @throws WrongKeyException Is thrown, if the key is invalid for the given encrypted string
	 */
	public static String decryptWithKey(String input, char[] key) throws WrongKeyException {
		String decrypted = "";
		
		try
		{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(CryptoUtils.convertCharArrayToByteArray(key), "AES"));
			decrypted = new String(cipher.doFinal(Base64.getDecoder().decode(input)));
		} catch (Exception e) {
			decrypted = "";
		}
		
		return decrypted;
	}
	
	/**
	 * This method decrypts the parameter 'input' with the key given in the parameter 'key'
	 * @param input The byte array to decrypt
	 * @param key The key to decrypt the input with
	 * @return The decrypted input
	 * @throws WrongKeyException Is thrown, if the key is invalid for the given encrypted string
	 */
	public static String decryptWithKey(byte[] input, char[] key) throws WrongKeyException {
		String decrypted = "";
		
		try
		{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(CryptoUtils.convertCharArrayToByteArray(key), "AES"));
			decrypted = new String(cipher.doFinal(input));
		} catch (Exception e) {
			decrypted = "";
		}
		
		return decrypted;
	}
	
	/**
	 * Checks, whether the key is valid for a given encrypted string
	 * @param encryptedText The encrypted string to check the key for
	 * @param key The key to check
	 * @return Whether the key is valid for the given encrypted string or not
	 */
	public static boolean validateKey(String encryptedText, char[] key) {
		try {
			decryptWithKey(encryptedText, key);
		} catch (WrongKeyException e) {
			return false;
		}
		return true;
	}

	/**
	 * This method is used to verify that a given string has been encrypted with a specific private key
	 * @param plainText The text which got signed with the private key
	 * @param signature The signature-string generated from the private-key-signing-algorithm
	 * @param publicKey The public key matching the used private key
	 * @return Whether the signature is valid or not
	 * @throws Exception Is thrown, if an exception occurs during the verification
	 */
    public static boolean verifySignature(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }

    /**
     * This method encrypts the given data with a given key via RSA-algorithm
     * @param pKey A private or public key to encrypt with as a byte array (look at getPublicKey/getPrivateKey-methods)
     * @param inputData The data to encrypt
     * @param isPrivateKey Whether the given key is the private key or not
     * @return The encrypted data
     * @throws Exception Is thrown, if an exception during the encryption occurs
     */
    public static byte[] encryptWithRSA(byte[] pKey, byte[] inputData, boolean isPrivateKey)
            throws Exception {

        Key key;
        if(isPrivateKey) {
            key = KeyFactory.getInstance(ALGORITHM)
                    .generatePrivate(new PKCS8EncodedKeySpec(pKey));
        } else {
            key = KeyFactory.getInstance(ALGORITHM)
                    .generatePublic(new X509EncodedKeySpec(pKey));
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(inputData);
    }

    /**
     * This method decrypts the input data with the given key
     * @param pKey The key to decrypt the data with as a byte array (look at getPublicKey/getPrivateKey-methods)
     * @param inputData The encrypted data
     * @param isPrivateKey Whether the given key is the private key or not
     * @return The decrypted data
     * @throws Exception Is thrown, if an error occurs during the decryption
     */
    public static byte[] decryptWithRSA(byte[] pKey, byte[] inputData, boolean isPrivateKey)
            throws Exception {

        Key key;
        if(isPrivateKey) {
            key = KeyFactory.getInstance(ALGORITHM)
                    .generatePrivate(new PKCS8EncodedKeySpec(pKey));
        } else {
            key = KeyFactory.getInstance(ALGORITHM)
                    .generatePublic(new X509EncodedKeySpec(pKey));
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(inputData);
    }
    
    /**
     * This method converts a Base64-formatted public key into a PublicKey-object
     * @param base64Key The public key in a Base64 format
     * @return The PublicKey-object
     * @throws NoSuchAlgorithmException Is thrown, if the algorithm isn't found
     * @throws InvalidKeySpecException Is thrown, if something goes wrong during conversion
     */
    public static PublicKey getPublicKey(String base64Key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] allBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(allBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /**
     * This method converts a Base64-formatted private key into a PrivateKey-object
     * @param base64Key The private key in a Base64 format
     * @return The PrivateKey-object
     * @throws NoSuchAlgorithmException Is thrown, if the algorithm isn't found
     * @throws InvalidKeySpecException Is thrown, if something goes wrong during conversion
     */
    public static PrivateKey getPrivateKey(String base64Key) throws NoSuchAlgorithmException, InvalidKeySpecException {
    	byte[] allBytes = Base64.getDecoder().decode(base64Key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(allBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
    
    /**
     * This method generates a new KeyPair-object
     * @return The generated KeyPair-object
     * @throws NoSuchAlgorithmException Is thrown, if the algorithm isn't found
     * @throws NoSuchProviderException Is thrown, if something goes wrong during conversion
     */
    public static KeyPair generateKeyPair()
            throws NoSuchAlgorithmException, NoSuchProviderException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

        keyGen.initialize(4096, random);

        return keyGen.generateKeyPair();
    }
    
    public static String generateSecureKey() {
    	SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstanceStrong();
	        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	        keyGenerator.init(256, secureRandom);

	        SecretKey secretKey = keyGenerator.generateKey();

	        byte[] keyBytes = secretKey.getEncoded();
			return Base64.getEncoder().encodeToString(keyBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
    }
    
    public static int computeIntegerOffset(String key) {
    	int checksum = 0;
        for (char c : key.toCharArray()) {
            checksum += (int) c;
        }
        return checksum;
    }
    
    private static byte[] convertCharArrayToByteArray(char[] input) {
    	byte[] toReturn = new byte[input.length];
    	
    	for(int i = 0; i < input.length; i++) {
    		toReturn[i] = (byte) input[i];
    	}
    	
    	return toReturn;
    }
}
