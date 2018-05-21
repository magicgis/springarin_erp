package com.springrain.erp.modules.amazoninfo.htmlunit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;

import com.google.common.collect.Lists;

/**
 * Java Server side class for Google Authenticator's TOTP generator
 * Thanks to Enrico's blog for the sample code:
 * @see http://thegreyblog.blogspot.com/2011/12/google-authenticator-using-it-in-your.html
 * @see http://code.google.com/p/google-authenticator
 * @see http://tools.ietf.org/id/draft-mraihi-totp-timebased-06.txt
 */
public class GoogleAuthenticator {
    
    public static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";
    
    public static  URL sys_url = null;
    
    static{
    	try {
			sys_url= new URL("http://192.81.128.219/springrain-erp/a");
		} catch (MalformedURLException e) {}
    }
    
    
    public static String[] get_code(String secret) {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        // convert unix msec time into a 30 second "window" 
        // this is per the TOTP spec (see the RFC for details)
        long time = System.currentTimeMillis();
		/*try {
			URLConnection connect = sys_url.openConnection();
			connect.connect();
			time = connect.getDate();
		} catch (IOException e1) {}*/
        long t = (time/ 1000L) / 30L;
        // Window is used to check codes generated in the near past.
        // You can use this value to tune how far you're willing to go.            long hash;
        try {
           long hash = verify_code(decodedKey, t ); 
           long hash1 = verify_code(decodedKey, t-2 ); 
           long hash2 = verify_code(decodedKey, t-1 );
           String[] rsAarry = new String[3];
           String rs =  hash+"";
           for (int i = 6-rs.length(); i > 0; i--) {
        	   rs="0"+rs;
           }
           rsAarry[0] = rs;
           
           rs =  hash1+"";
           for (int i = 6-rs.length(); i > 0; i--) {
        	   rs="0"+rs;
           }
           rsAarry[1] = rs;
           
           rs =  hash2+"";
           for (int i = 6-rs.length(); i > 0; i--) {
        	   rs="0"+rs;
           }
           rsAarry[2] = rs;
           
           return rsAarry;
        }catch (Exception e) {
            // Yes, this is bad form - but
            // the exceptions thrown would be rare and a static configuration problem
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private static int verify_code(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        // We're using a long because Java hasn't got unsigned int.
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }
    
    public static void main(String[] args) {
    	while (true) {
    		System.out.println(Lists.newArrayList(get_code("LJUL EMAH 3XAI GGKX MHLB 2LFP JOTF Y6KO ZI5X ZGZS COX6 KZUM ZPZA")));
    		try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}