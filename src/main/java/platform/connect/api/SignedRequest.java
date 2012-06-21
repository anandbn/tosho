/*
 * Copyright, 1999-2012, salesforce.com
 * All Rights Reserved
 * Company Confidential
 */
package platform.connect.api;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.type.TypeReference;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

public class SignedRequest {

    public static CanvasRequest unsign(String input, String secret) throws SecurityException {

        String[] split = getParts(input);

        String encodedSig = split[0];
        String encodedEnvelope = split[1];

        // Deserialize the json body
        String json_envelope = new String(new Base64(true).decode(encodedEnvelope));
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader(CanvasRequest.class);
        CanvasRequest canvasRequest;
        String algorithm;
        try {
            canvasRequest = reader.readValue(json_envelope);
            algorithm = canvasRequest.getAlgorithm() == null ? "HMACSHA256" : canvasRequest.getAlgorithm();
        } catch (IOException e) {
            throw new SecurityException(String.format("Error [%s] deserializing JSON to Object [%s]", e.getMessage(), CanvasRequest.class.getName()), e);
        }

        verify(secret, algorithm, encodedEnvelope, encodedSig);

        // If we got this far, then the request was not tampered with.
        // return the request as a Java object
        return canvasRequest;
    }


    public static String unsignAsJson(String input, String secret) throws SecurityException {

        String[] split = getParts(input);

        String encodedSig = split[0];
        String encodedEnvelope = split[1];

        String json_envelope = new String(new Base64(true).decode(encodedEnvelope));
        ObjectMapper mapper = new ObjectMapper();

        String algorithm;
        StringWriter writer;
        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() { };
        try {
            HashMap<String,Object> o = mapper.readValue(json_envelope, typeRef);
            writer = new StringWriter();
            mapper.writeValue(writer, o);
            algorithm = (String)o.get("algorithm");
        } catch (IOException e) {
            throw new SecurityException(String.format("Error [%s] deserializing JSON to Object [%s]", e.getMessage(),
                    typeRef.getClass().getName()), e);
        }

        verify(secret, algorithm, encodedEnvelope, encodedSig);

        // If we got this far, then the request was not tampered with.
        // return the request as a JSON string.
        return writer.toString();
    }

    private static String[] getParts(String input) {

        if (input == null || input.indexOf(".") <= 0) {
            throw new SecurityException(String.format("Input [%s] doesn't look like a signed request", input));
        }

        String[] split = input.split("[.]", 2);
        return split;
    }

    private static void verify(String secret, String algorithm, String encodedEnvelope, String encodedSig )
        throws SecurityException
    {
        SecretKey hmacKey = null;
        try {
            byte[] key = secret.getBytes();
            hmacKey = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(hmacKey);

            // Check to see if the body was tampered with
            byte[] digest = mac.doFinal(encodedEnvelope.getBytes());
            byte[] decode_sig = new Base64(true).decode(encodedSig);
            if (! Arrays.equals(digest, decode_sig)) {
                String label = "Warning: Request was tampered with";
                throw new SecurityException(label);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(String.format("Problem with algorithm [%s] Error [%s]", algorithm, e.getMessage()), e);
        } catch (InvalidKeyException e) {
            throw new SecurityException(String.format("Problem with key [%s] Error [%s]", hmacKey, e.getMessage()), e);
        }

        // If we got here and didn't throw a SecurityException then all is good.
    }
}
