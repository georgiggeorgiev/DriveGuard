package com.joro.driveguard;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;

public final class Utilities
{
    private Utilities() {}

    public static String generateAPIKey()
    {
        final int keyLen = 128;

        try
        {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(keyLen);
            SecretKey secretKey = keyGen.generateKey();
            byte[] encoded = secretKey.getEncoded();
            return DatatypeConverter.printHexBinary(encoded).toLowerCase();
        }
        catch (NoSuchAlgorithmException e)
        {
            return null;
        }
    }
}
