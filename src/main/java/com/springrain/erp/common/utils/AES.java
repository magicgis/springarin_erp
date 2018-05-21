package com.springrain.erp.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * AES加解密算法
 * 
 * @author
 * 
 */

public class AES {
	
	private final static Logger logger = LoggerFactory.getLogger(AES.class);
	
	private static final String DEFUALT_PASSWORD = "springRain#4@eRp";
	

    public static void main(String[] args) throws Exception {
    	String str = AES.jiami("sdwfaga");
    	System.out.println(str);
		str = new String(AES.jiemi(str));
    	System.out.println(str);
    }
    
    // 加密
    public static String Encrypt(byte[] sSrc) throws Exception {
        byte[] raw = DEFUALT_PASSWORD.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        IvParameterSpec iv = new IvParameterSpec("Aze@tmf09~SdfdsL".getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc);

        return new BASE64Encoder().encode(encrypted);
    }
    
    // 加密
    public static byte[] EncryptImg(byte[] sSrc) throws Exception{
        byte[] raw = DEFUALT_PASSWORD.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        IvParameterSpec iv = new IvParameterSpec("Aze@tmf09~SdfdsL".getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc);

        return encrypted;
    }
    
    // 加密
    public static String Encrypt(String password,byte[] sSrc) throws Exception {
        String cKey = password;  //加密密码
        byte[] raw = cKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        IvParameterSpec iv = new IvParameterSpec("Aze@tmf09~SdfdsL".getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc);
        return new BASE64Encoder().encode(encrypted);
    }

    // 解密
    public static String Decrypt(String sSrc) throws Exception {
        try {
            byte[] raw = DEFUALT_PASSWORD.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec iv = new IvParameterSpec("Aze@tmf09~SdfdsL".getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
            //不是32的倍数,表示数据错误
           if(encrypted1.length%32!=0){
        	   return "dataerror";
           }
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
            	logger.error("解密失败", e);
                return null;
            }
            
//            EncryptedWorkBookHandle d ;
        } catch (Exception ex) {
        	logger.error("解密失败", ex);
            return null;
        }
    }
    
    // 解密
    public static String Decrypt(String password,String sSrc) throws Exception {
        try {
            String cKey = password;
            byte[] raw = cKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec iv = new IvParameterSpec("Aze@tmf09~SdfdsL".getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
            	logger.error("解密失败", e);
                return null;
            }
            
        } catch (Exception ex) {
        	logger.error("解密失败", ex);
            return null;
        }
    }
    
 // 解密
    public static byte[] DecryptImg(byte[] sSrc) throws Exception {
        try {
            byte[] raw = DEFUALT_PASSWORD.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec iv = new IvParameterSpec("Aze@tmf09~SdfdsL".getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            try {
                byte[] original = cipher.doFinal(sSrc);
//                String originalString = new String(original);
                return original;
            } catch (Exception e) {
            	logger.error("解密失败", e);
                return null;
            }
            
        } catch (Exception ex) {
        	logger.error("解密失败", ex);
            return null;
        }
    }
    
    public static String jiami(String data) {
    	return jiami(DEFUALT_PASSWORD, data);
    }
    
    
    public static String jiami(String password,String data) {
    	String outputStr = "";
		try {
			byte[] ib = transInputstreamToBytes(new ByteArrayInputStream(data.getBytes()));
			outputStr = Encrypt(password,getBASE64Bytes(ib));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputStr;
	}
    
    public static byte[] jiemi(String data) {
    	return jiemi(DEFUALT_PASSWORD, data);
    }
	
	public static byte[] jiemi(String password,String data) {
		String str = "";
		byte[] outputStr = null;
		try {
			byte[] ib = transInputstreamToBytes(new ByteArrayInputStream(data.getBytes()));
			 str = Decrypt(password,new String(ib));
			int len = str.indexOf(";");
			str = str.substring(0,len);
			outputStr = getUnBASE64Bytes(str.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputStr;
	}
	
	/**
	 * 将 InputStream 转化为 byte[]
	 * 
	 * @param in
	 *            待转化的 InputStream
	 * @return 转化后的 byte[]
	 */
	public static byte[] transInputstreamToBytes(InputStream in) {
		byte[] in2b = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			byte[] buff = new byte[4096];
			int len = 0;
			while ((len = in.read(buff, 0, 4096)) > 0) {
				baos.write(buff, 0, len);
				baos.flush();
			}
			in2b = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return in2b;
	}
	
	public static byte[] getBASE64Bytes(byte[] input) {
		byte[] b = null;
		try {
			if (input != null && input.length > 0) {
				BASE64Encoder bd = new BASE64Encoder();
				String output = bd.encode(input)+";";
				b = output.getBytes();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}

	public static byte[] getUnBASE64Bytes(byte[] input) {
		byte[] b = null;
		try {
			if (input != null && input.length > 0) {
				BASE64Decoder bd = new BASE64Decoder();
				b = bd.decodeBuffer(new String(input));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	
	
}
