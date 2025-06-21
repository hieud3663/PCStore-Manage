package com.pcstore.utils;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;


public class PCrypt {

    /**
     * Utility class for hashing passwords and verifying password hashes
     */
    public static String hashPassword(String password) {
        try{
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            
            return 65536 + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException(String.format(ErrorMessage.PASSWORD_HASH_ERROR.toString(), e.getMessage()), e);
        }
    }

    /**
     * Verifies a password against a stored hash
     */
    public static boolean checkPassword(String password, String storedHash) {
        try{
            String[] parts = storedHash.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);
            
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] testHash = factory.generateSecret(spec).getEncoded();
            
            return Arrays.equals(hash, testHash);
        } catch (Exception e) {
            throw new IllegalStateException(String.format(ErrorMessage.PASSWORD_VERIFY_ERROR.toString(), e.getMessage()), e);
        }
    }
}