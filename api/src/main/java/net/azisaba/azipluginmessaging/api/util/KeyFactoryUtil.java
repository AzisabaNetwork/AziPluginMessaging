package net.azisaba.azipluginmessaging.api.util;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class KeyFactoryUtil {
    public static PublicKey getPublicKey(byte[] encoded) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance(EncryptionUtil.ALGORITHM);
        return kf.generatePublic(spec);
    }
}
