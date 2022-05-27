package net.azisaba.azipluginmessaging.api.util;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class KeyFactoryUtil {
    /**
     * Creates the new instance of PublicKey from given encoded data.
     * @param encoded the encoded data
     * @return the PublicKey
     * @throws Exception If PublicKey could not be created for any reason.
     */
    public static PublicKey getPublicKey(byte[] encoded) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance(EncryptionUtil.ALGORITHM);
        return kf.generatePublic(spec);
    }
}
