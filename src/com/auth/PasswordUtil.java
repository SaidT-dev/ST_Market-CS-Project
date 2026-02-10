package com.auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 128;
    private static final int SALT_LENGTH = 16;

     public static String hashPassword(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
         SecureRandom random = new SecureRandom();
         byte[] salt = new byte[SALT_LENGTH];
         random.nextBytes(salt);

         KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
         SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();

        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String encodedHash = Base64.getEncoder().encodeToString(hash);

        return encodedSalt + "$" + encodedHash;
     }

     public static boolean checkPassword(String password, String storedHash)
             throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedHash.split("\\$");
        if (parts.length != 2) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] hash = Base64.getDecoder().decode(parts[1]);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] testHash = factory.generateSecret(spec).getEncoded();

        return MessageDigest.isEqual(hash, testHash);
     }
}
