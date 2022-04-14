package net.azisaba.azipluginmessaging.api.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAKeyGenParameterSpec;

public class EncryptionUtil {
    public static final String ALGORITHM = "RSA";

    /**
     * Generates a new RSA key pair for encryption and decryption.
     *
     * @param bits the number of bits to use
     * @return The generated key pair
     * @throws Exception If an error occurs
     */
    @NotNull
    public static KeyPair generateKeyPair(int bits) throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(ALGORITHM);
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(bits, RSAKeyGenParameterSpec.F4);
        keygen.initialize(spec);
        return keygen.generateKeyPair();
    }

    /**
     * Encrypts a block of data.
     *
     * @param data The data to encrypt
     * @param key  The key to encrypt with
     * @return The encrypted data
     * @throws Exception If an error occurs
     */
    public static byte[] encrypt(byte[] data, @NotNull PublicKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * Decrypts a block of data.
     *
     * @param data The data to decrypt
     * @param key  The key to decrypt with
     * @return The decrypted data
     * @throws Exception If an error occurs
     */
    public static byte[] decrypt(byte[] data, @NotNull PrivateKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    @Contract("_ -> new")
    public static @NotNull String encodePublicKey(@NotNull PublicKey key) {
        return Base64Util.encode(key.getEncoded());
    }

    @Contract("_ -> new")
    public static @NotNull PublicKey decodePublicKey(@NotNull String key) throws Exception {
        return KeyFactoryUtil.getPublicKey(Base64Util.decode(key));
    }
}
