package com.hupu.gamesdk.base;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 描述：MD5加密.
 *
 * @author zhaowb
 */
public class HPMd5 {
	
	/**
	 * 描述：MD5加密.
	 * @param str 要加密的字符串
	 * @return String 加密的字符串
	 */
	private  MessageDigest sMd5MessageDigest;
	private  StringBuffer stringBuffer;


	public HPMd5(){
		try {
			sMd5MessageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
		}
		stringBuffer = new StringBuffer();
	}

	public  String md5(String s) {

		try{
			sMd5MessageDigest.reset();
			sMd5MessageDigest.update(s.getBytes());

			byte digest[] = sMd5MessageDigest.digest();

			stringBuffer.setLength(0);
			for (int i = 0; i < digest.length; i++) {
				final int b = digest[i] & 255;
				if (b < 16) {
					stringBuffer.append('0');
				}
				stringBuffer.append(Integer.toHexString(b));
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return stringBuffer.toString();
	}


}