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

        String encoded_sig = split[0];
        String encoded_envelope = split[1];

        // Deserialize the json body
        String json_envelope = new String(new Base64(true).decode(encoded_envelope));
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.reader(CanvasRequest.class);
        CanvasRequest canvasRequest;
        try {
            canvasRequest = reader.readValue(json_envelope);
        } catch (IOException e) {
            throw new SecurityException(String.format("Error [%s] deserializing JSON to Object [%s]", e.getMessage(), CanvasRequest.class.getName()), e);
        }


        SecretKey hmacKey = null;
        String algorithm = null;
        try {
            algorithm = canvasRequest.getAlgorithm() == null ? "HMACSHA256" : canvasRequest.getAlgorithm();
            byte[] key = secret.getBytes();
            hmacKey = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(hmacKey);

            // Check to see if the body was tampered with
            byte[] digest = mac.doFinal(encoded_envelope.getBytes());
            byte[] decode_sig = new Base64(true).decode(encoded_sig);
            if (! Arrays.equals(digest, decode_sig)) {
                String label = "Warning: Request was tampered with";
                throw new SecurityException(label);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(String.format("Problem with algorithm [%s] Error [%s]", algorithm, e.getMessage()), e);
        } catch (InvalidKeyException e) {
            throw new SecurityException(String.format("Problem with key [%s] Error [%s]", hmacKey, e.getMessage()), e);
        }

        // If we got this far, then the request was not tampered with.
        return canvasRequest;
    }


    public static String unsignToString(String input, String secret) throws SecurityException {

        String[] split = getParts(input);

        String encoded_sig = split[0];
        String encoded_envelope = split[1];

        String json_envelope = new String(new Base64(true).decode(encoded_envelope));
        ObjectMapper mapper = new ObjectMapper();

        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() { };
        try {
            HashMap<String,Object> o
                    = mapper.readValue(json_envelope, typeRef);
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, o);

            System.out.println("Hash Map " + o.toString());
            System.out.println("Writer " + writer.toString());

        } catch (IOException e) {
            throw new SecurityException(String.format("Error [%s] deserializing JSON to Object [%s]", e.getMessage(),
                    typeRef.getClass().getName()), e);
        }

//        SecretKey hmacKey = null;
//        String algorithm = null;
//        try {
//            algorithm = canvasRequest.getAlgorithm() == null ? "HMACSHA256" : canvasRequest.getAlgorithm();
//            byte[] key = secret.getBytes();
//            hmacKey = new SecretKeySpec(key, algorithm);
//            Mac mac = Mac.getInstance(algorithm);
//            mac.init(hmacKey);
//
//            // Check to see if the body was tampered with
//            byte[] digest = mac.doFinal(encoded_envelope.getBytes());
//            byte[] decode_sig = new Base64(true).decode(encoded_sig);
//            if (! Arrays.equals(digest, decode_sig)) {
//                String label = "Warning: Request was tampered with";
//                throw new SecurityException(label);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            throw new SecurityException(String.format("Problem with algorithm [%s] Error [%s]", algorithm, e.getMessage()), e);
//        } catch (InvalidKeyException e) {
//            throw new SecurityException(String.format("Problem with key [%s] Error [%s]", hmacKey, e.getMessage()), e);
//        }
//
//        // If we got this far, then the request was not tampered with.
//        return canvasRequest;

        return "";
    }

    private static String[] getParts(String input) {

        if (input == null || input.indexOf(".") <= 0) {
            throw new SecurityException(String.format("Input [%s] doesn't look like a signed request", input));
        }

        String[] split = input.split("[.]", 2);
        return split;
    }


//
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> T unsign(Class<T> clazz, String input, String secret) throws RuntimeException, SecurityException {
//
//        String[] split = input.split("[.]", 2);
//
//        String encoded_sig = split[0];
//        String encoded_envelope = split[1];
//
//        SecretKey hmacKey = null;
//        try {
//            byte[] key = secret.getBytes();
//            hmacKey = new SecretKeySpec(key, ALGORITHM.HMAC_SHA256.toString());
//            Mac mac = Mac.getInstance(ALGORITHM.HMAC_SHA256.toString());
//            mac.init(hmacKey);
//
//            // Check to see if the body was tampered with
//            byte[] digest = mac.doFinal(encoded_envelope.getBytes());
//            byte[] decode_sig = new Base64(true).decode(encoded_sig);
//            if (! Arrays.equals(digest, decode_sig)) {
//                String label = "Warning: Request was tampered with";
//                throw new SecurityException(label);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimException(String.format("Problem with algorithm [%s] Error [%s]", ALGORITHM.HMAC_SHA256.toString(), e.getMessage()), e);
//        } catch (InvalidKeyException e) {
//            throw new SignRequestException(String.format("Problem with key [%s] Error [%s]", hmacKey, e.getMessage()), e);
//        }
//
//        // If we got this far, then the request was not tampered with.
//
//        String json_envelope = new String(new Base64(true).decode(encoded_envelope));
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectReader reader = mapper.reader(clazz);
//
//        T object;
//        try {
//            object = (T)reader.readValue(json_envelope);
//        } catch (IOException e) {
//            throw new SignRequestException(String.format("Error [%s] deserializing JSON to Object [%s]", e.getMessage(), clazz.getName()), e);
//        }
//
//        return object;
//    }
//

}
