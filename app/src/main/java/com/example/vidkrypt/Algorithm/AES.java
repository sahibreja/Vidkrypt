package com.example.vidkrypt.Algorithm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AES {
    private final static int IV_LENGTH = 16; // Default length with Default 128
    // key AES encryption
    private final static int DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE = 1024;
    private final static String ALGO_RANDOM_NUM_GENERATOR = "SHA1PRNG";
    private final static String ALGO_SECRET_KEY_GENERATOR = "AES";
    private final static String ALGO_VIDEO_ENCRYPTOR = "AES/CBC/PKCS5Padding";
    static long startTime1,elapsedTime1,startTime2,elapsedTime2;

    @SuppressWarnings("resource")
    public static void encrypt(SecretKey key, InputStream in, OutputStream out)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IOException {
        try {
            byte[] iv = new byte[] { (byte) 0x8E, 0x12, 0x39, (byte) 0x9C,
                    0x07, 0x72, 0x6F, 0x5A, (byte) 0x8E, 0x12, 0x39, (byte) 0x9C,
                    0x07, 0x72, 0x6F, 0x5A };
            //generate new AlgorithmParameterSpec
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
            Cipher c = Cipher.getInstance(ALGO_VIDEO_ENCRYPTOR);
            c.init(Cipher.ENCRYPT_MODE, key,paramSpec);
            out = new CipherOutputStream(out, c);
            int count = 0;
            startTime1 = System.nanoTime();
            byte[] buffer = new byte[DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE];
            while ((count = in.read(buffer)) >= 0) {
                out.write(buffer, 0, count);
            }
        } finally {
            out.close();
        }
        elapsedTime1 = System.nanoTime() - startTime1;
    }

    @SuppressWarnings("resource")
    public static void decrypt(SecretKey key, InputStream in, OutputStream out)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IOException {
        try {
            byte[] iv = new byte[] { (byte) 0x8E, 0x12, 0x39, (byte) 0x9C,
                    0x07, 0x72, 0x6F, 0x5A, (byte) 0x8E, 0x12, 0x39, (byte) 0x9C,
                    0x07, 0x72, 0x6F, 0x5A };
            // read from input stream AlgorithmParameterSpec
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
            Cipher c = Cipher.getInstance(ALGO_VIDEO_ENCRYPTOR);
            c.init(Cipher.DECRYPT_MODE, key, paramSpec);
            out = new CipherOutputStream(out, c);
            int count = 0;
            startTime1 = System.nanoTime();
            byte[] buffer = new byte[DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE];
            while ((count = in.read(buffer)) >= 0) {
                out.write(buffer, 0, count);
            }
        } finally {
            out.close();
        }
        elapsedTime2 = System.nanoTime() - startTime2;
    }
    public static long getEncTime()
    {
       return elapsedTime1;
    }
    public static long getDecTime()
    {
        return elapsedTime2;
    }
}
