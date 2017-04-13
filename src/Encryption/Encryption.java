package Encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

public class Encryption {

	private static final String CIPHER_TYPE = "AES";
	private static SecretKeySpec key;

	static public void startUp(String passWord){
		byte[] Key;
		MessageDigest sha;
		try {
			Key = passWord.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			Key = sha.digest(Key);
			Key = Arrays.copyOf(Key, 16);
			key = new SecretKeySpec(Key, CIPHER_TYPE);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
	public static void main (String[] args) throws Exception{
		Encryption.startUp("SuperSecretPassWord1092837081");
		String testbericht = "test";
		byte[] encryption = Encryption.encrypt(testbericht.getBytes());
		byte[] decryption = Encryption.decrypt(encryption);
		System.out.println(new String(encryption));
		System.out.println(new String(decryption));
	}

	static public byte[] encrypt(byte[] TextToEncrypt) {
		byte[] encVal = null;
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encVal = cipher.doFinal(TextToEncrypt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encVal;
	}

	static public byte[] decrypt(byte[] encryptedText) {
		String decryptedValue = "";
        System.out.println("dencrypting: " + encryptedText.length);
        try {
			Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decValue = cipher.doFinal(encryptedText);
			decryptedValue = new String(decValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decryptedValue.getBytes();
	}
}