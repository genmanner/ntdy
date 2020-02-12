package com.genmanner.partygm.core.framework.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.genmanner.partygm.core.common.framework.SpringTools;
import com.genmanner.partygm.core.common.util.safe.AesCryptUtil;
import com.genmanner.partygm.core.common.util.safe.DesCryptUtil;
import com.genmanner.partygm.core.common.util.safe.RsaCryptUtil;

/**
 * 报文加密工具类
 *  
 */
public class MessageEncryptUtil {
	/**
	 * 收到的消息报文
	 *  
	 */
	public static class Message {
		/**
		 * 获取报文数据
		 * @return 报文数据
		 */
		public byte[] getContext() {
			return context;
		}
		
		/**
		 * 设置报文数据
		 * @param context 报文数据
		 */
		public void setContext(byte[] context) {
			this.context = context;
		}
		
		/**
		 * 获取学校代码
		 * @return 学校代码
		 */
		public String getUniversityCode() {
			return universityCode;
		}
		
		/**
		 * 设置学校代码
		 * @param universityCode 学校代码
		 */
		public void setUniversityCode(String universityCode) {
			this.universityCode = universityCode;
		}
		
		/**
		 * 获取时间戳
		 * @return 时间戳
		 */
		public Date getTimeStamp() {
			return timeStamp;
		}
		
		/**
		 * 设置时间戳
		 * @param timeStamp 时间戳
		 */
		public void setTimeStamp(Date timeStamp) {
			this.timeStamp = timeStamp;
		}
		
		/**
		 * 获取状态码
		 * @return 状态码，1：正常；-1：校验失败；-2：超时
		 */
		public int getStatus() {
			return status;
		}
		
		/**
		 * 设置状态码
		 * @param status 状态码，1：正常；-1：校验失败；-2：超时
		 */
		public void setStatus(int status) {
			this.status = status;
		}
		
		private byte[] context;
		private String universityCode;
		private Date timeStamp;
		private int status;
	}
	
	/**
	 * 平台端加密数据
	 * @param context 原文
	 * @param universityCode 学校标识码
	 * @return 密文，加密公式：Encrypt(timeStamp+context+MD5(timeStamp+context), aesKey)
	 * 		+ Encrypt(aesKey, rsaPubKey)
	 */
	public static byte[] encryptOnPlatform(byte[] context, String universityCode) {
		try {
			byte[] bTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).getBytes("UTF8");
			byte[] bContext = new byte[bTimeStamp.length+context.length+16];
			System.arraycopy(bTimeStamp, 0, bContext, 0, bTimeStamp.length);
			System.arraycopy(context, 0, bContext, bTimeStamp.length, context.length);
			byte[] bCheckSum = generateCheckNum(bContext, 0, bTimeStamp.length+context.length);
			System.arraycopy(bCheckSum, 0, bContext, bTimeStamp.length+context.length, 16);
			String aesKey = generateAesKey(32);
			byte[] bData1 = AesCryptUtil.encrypt(bContext, aesKey);
			String rsaPriKey = decryptRsaKey(loadEncryptedRsaPriKeyOnPlatform(universityCode));
			byte[] bData2 = RsaCryptUtil.encryptByPriKey(aesKey.getBytes("UTF8"), rsaPriKey);
			byte[] bMessage = new byte[bData1.length+bData2.length];
			System.arraycopy(bData1, 0, bMessage, 0, bData1.length);
			System.arraycopy(bData2, 0, bMessage, bData1.length, bData2.length);
			return bMessage;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 业务系统端加密数据
	 * @param context 原文
	 * @return 密文，加密公式：universityCode+Encrypt(timeStamp+context+MD5(timeStamp+context), aesKey)
	 * 		+ Encrypt(aesKey, rsaPubKey)
	 */
	public static byte[] encryptOnBusiness(byte[] context) {
		try {
			byte[] bUniversityCode = loadUniversityCodeOnBusiness().getBytes("UTF8");
			byte[] bTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()).getBytes("UTF8");
			byte[] bContext = new byte[bTimeStamp.length+context.length+16];
			System.arraycopy(bTimeStamp, 0, bContext, 0, bTimeStamp.length);
			System.arraycopy(context, 0, bContext, bTimeStamp.length, context.length);
			byte[] bCheckSum = generateCheckNum(bContext, 0, bTimeStamp.length+context.length);
			System.arraycopy(bCheckSum, 0, bContext, bTimeStamp.length+context.length, 16);
			String aesKey = generateAesKey(32);
			byte[] bData1 = AesCryptUtil.encrypt(bContext, aesKey);
			String rsaPubKey = decryptRsaKey(loadEncryptedRsaPubKeyOnBusiness());
			byte[] bData2 = RsaCryptUtil.encryptByPubKey(aesKey.getBytes("UTF8"), rsaPubKey);
			byte[] bMessage = new byte[32+bData1.length+bData2.length];
			System.arraycopy(bUniversityCode, 0, bMessage, 0, (bUniversityCode.length>32?32:bUniversityCode.length));
			System.arraycopy(bData1, 0, bMessage, 32, bData1.length);
			System.arraycopy(bData2, 0, bMessage, 32+bData1.length, bData2.length);			
			return bMessage;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 平台端解密数据
	 * @param data 密文
	 * @return 数据报文，加密公式：universityCode+Encrypt(timeStamp+context+MD5(timeStamp+context), aesKey)
	 * 		+ Encrypt(aesKey, rsaPubKey)
	 */
	public static Message decryptOnPlatform(byte[] data) {
		Message message = new Message();
		
		try {
			int iLastIndex = 0;
			for(; data[iLastIndex]!='\0'&&iLastIndex<32; ++iLastIndex);
			String universityCode = new String(data, 0 ,iLastIndex);
			message.setUniversityCode(universityCode);
			String rsaPriKey = decryptRsaKey(loadEncryptedRsaPriKeyOnPlatform(universityCode));
			byte[] bCodedAesKey = new byte[128];
			System.arraycopy(data, data.length-128, bCodedAesKey, 0, 128);
			String aesKey = new String(RsaCryptUtil.decryptByPriKey(bCodedAesKey, rsaPriKey), "UTF8");
			byte[] bCodedMessage = new byte[data.length-160];
			System.arraycopy(data, 32, bCodedMessage, 0, data.length-160);
			byte[] bMessage = AesCryptUtil.decrypt(bCodedMessage, aesKey);		
			byte[] bCheckSum = generateCheckNum(bMessage, 0, bMessage.length-16);
			
			for(int i=0; i<16; ++i) {
				if(bCheckSum[i] != bMessage[bMessage.length-16+i]) {
					message.setStatus(-1);
					return message;
				}
			}
			
			byte[] bContext = new byte[bMessage.length-35];
			System.arraycopy(bMessage, 19, bContext, 0, bMessage.length-35);
			message.setContext(bContext);
			
			try {
				message.setTimeStamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new String(bMessage, 0, 19)));
				
				if(new Date().getTime()-message.getTimeStamp().getTime() > TIMEOUT_MILLIS) {
					message.setStatus(-2);
				} else {
					message.setStatus(1);
				}
			} catch (ParseException e) {
				message.setStatus(-2);
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	/**
	 * 业务系统端解密数据
	 * @param data 密文
	 * @return 数据报文，加密公式：Encrypt(timeStamp+context+MD5(timeStamp+context), aesKey)
	 * 		+ Encrypt(aesKey, rsaPubKey)
	 */
	public static Message decryptOnBusiness(byte[] data) {
		Message message = new Message();
		String rsaPubKey = decryptRsaKey(loadEncryptedRsaPubKeyOnBusiness());
		byte[] bCodedAesKey = new byte[32];
		System.arraycopy(data, data.length-32, bCodedAesKey, 0, 32);
		String aesKey = new String(RsaCryptUtil.decryptByPubKey(bCodedAesKey, rsaPubKey));
		byte[] bCodedMessage = new byte[data.length-32];
		System.arraycopy(data, 0, bCodedMessage, 0, data.length-32);
		byte[] bMessage = AesCryptUtil.decrypt(bCodedMessage, aesKey);
		byte[] bCheckSum = generateCheckNum(bMessage, 0, bMessage.length-16);
		
		for(int i=0; i<16; ++i) {
			if(bCheckSum[i] != data[data.length-16+i]) {
				message.setStatus(-1);
				return message;
			}
		}
		
		byte[] bContext = new byte[bMessage.length-35];
		System.arraycopy(bContext, 0, bMessage, 19, data.length-35);
		message.setContext(bContext);
		
		try {
			message.setTimeStamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new String(bMessage, 0, 19)));
			
			if(new Date().getTime()-message.getTimeStamp().getTime() > TIMEOUT_MILLIS) {
				message.setStatus(-2);
			} else {
				message.setStatus(1);
			}
		} catch (ParseException e) {
			message.setStatus(-2);
			e.printStackTrace();
		}
		
		return message;
	}
	
	/**
	 * 加密RSA密钥
	 * FIXME! 最终将native实现
	 * @param rsaKey RSA密钥
	 * @return 加密后的RSA密钥
	 */
	public static String encryptRsaKey(String rsaKey) {
		String machineCode = "MyLinuxMachie";	//机器码，native获取
		String magicDesCode = "genmanner";		//魔幻Des码

		try {
			byte[] desKey = DesCryptUtil.encrypt(machineCode.getBytes("UTF8"), magicDesCode); //魔幻Des码加密机器码得到desKey
			byte[] data = DesCryptUtil.encrypt(rsaKey.getBytes("UTF8"), new String(desKey));
			return new BASE64Encoder().encode(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 解密RSA密钥
	 * FIXME! 最终将native实现
	 * @param data 加密后的RSA密钥
	 * @return RSA密钥
	 */
	public static String decryptRsaKey(String data) {
		String machineCode = "MyLinuxMachie";	//机器码，native获取
		String magicDesCode = "genmanner";		//魔幻Des码

		try {
			byte[] desKey = DesCryptUtil.encrypt(machineCode.getBytes("UTF8"), magicDesCode); //魔幻Des码加密机器码得到desKey	
			byte[] rsaKey = DesCryptUtil.decrypt(new BASE64Decoder().decodeBuffer(data), new String(desKey));
			return new String(rsaKey, "UTF8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 业务系统端装载加密后的RSA公钥
	 * @return 加密后的RSA公钥
	 */
	protected static String loadEncryptedRsaPubKeyOnBusiness() {
		//SysConfTools sysConfTools = SpringTools.getBean(SysConfTools.class);
		//return sysConfTools.getSysConf(SysConfTools.SYSCONF_ENCRYPTED_RSA_PUBLIC_KEY).getValue();
		String rasPriKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCE893lrOTHQXBkKwyI+lzczYA10CpcnEO+8R4o"
				+ "Inr6BhM8HLNWMonamhmMGosHqDqpedhoXlvw5jqa+69fgCqP9EngHn5iWleX5/X5wjKFcbwUMydV"
				+ "PQC3Y61ZkzCuzOkiQR48aRtoZz/XNU8ped9jGwk5PIqLETCEPzYvaSSEKQIDAQAB";
		return encryptRsaKey(rasPriKey);
	}
	
	/**
	 * 平台端装载加密后的RSA私钥
	 * FIXME! 最终将从学校基本信息表中装载，需要缓存以提高效率，并保证缓存有效性
	 * @return 加密后的RSA私钥
	 */
	protected static String loadEncryptedRsaPriKeyOnPlatform(String universityCode) {
		String rasPriKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAITz3eWs5MdBcGQrDIj6XNzNgDXQ"
				+ "KlycQ77xHigievoGEzwcs1YyidqaGYwaiweoOql52GheW/DmOpr7r1+AKo/0SeAefmJaV5fn9fnC"
				+ "MoVxvBQzJ1U9ALdjrVmTMK7M6SJBHjxpG2hnP9c1Tyl532MbCTk8iosRMIQ/Ni9pJIQpAgMBAAEC"
				+ "gYBTyr6W+hhV3FBRUTBdRC0ym/Gp+MA6DeJEfQJcmR6YZSvKPuxn7wIi2+wt+KyW7tfJ6BkT7iRT"
				+ "90YIV4d30PcBRnlKlhBeqdxT+5hL75i4DeN2XIKBAJ1+Vj0TwGdoURoISHEEfDiJPmJIY5CniL9k"
				+ "jLBRav4esx3hQqsDm2mzgQJBAN6Agjj5FdIqqh85xFPX4QCfxZlnmUjfQbillZ3d7BD1QmCq1bdn"
				+ "EkTWEY/iuwYyKaauRxWDlr7ioxrNEjyckYUCQQCY+ASzIB6BKmnXu4R3y7Hv7G67HQQtdWk/wojS"
				+ "GXAGj7UFbpxPpYbVCnrjMr8dmzFY2xTj2CMSNr8TLbk5JFdVAkEA1eYeylyS4K9JSqmDFsMzOCFg"
				+ "meAhWQ/fo33zvocouQi+niW9PQomYuyUh4mFGCjDJ8zk40bUdX91I3+/p2sw7QJARdJfgsuUYOzV"
				+ "Jw2Gp4+ohpTq7imOcJl5EwtenFfUFIqf2/zjCE/LvY5tDStqpL7jxWUqod1UFMo8aSqjsvttiQJB"
				+ "ALVZ/lTj680BuFT+DjTxqK2LmJDQEZzrPUsUcjVDUxgtfVSPwlRJgyahYKTBxQ6EvRjBULYFC+1S"
				+ "V3y/XIxV8uY=";
		return encryptRsaKey(rasPriKey);
	}
	
	/**
	 * 业务系统端装载学校标识码
	 * @return 学校标识码
	 */
	protected static String loadUniversityCodeOnBusiness() {
		SysConfTools sysConfTools = SpringTools.getBean(SysConfTools.class);
		return sysConfTools.getSysConf(SysConfTools.SYSCONF_UNIVERSITY_CODE).getValue();
	}
	
	/**
	 * 随机生成AES密钥
	 * @param keyLength 密钥长度
	 * @return AES密钥
	 */
	protected static String generateAesKey(int keyLength) {
		StringBuffer strKey = new StringBuffer();
		Random rdm = new Random(System.currentTimeMillis());
		char[] dic = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		
		for(int i=0; i<keyLength; ++i) {
			strKey.append(dic[rdm.nextInt(dic.length)]);
		}
		
		return strKey.toString();
	}
	
	/**
	 * 获取MD5校验值
	 * @param data 正文
	 * @return MD5校验值
	 */
	protected static byte[] generateCheckNum(byte[] data, int offset, int len) {
		try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(data, offset, len);
            // 获得密文
            return mdInst.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return null;
	}
	
	/**
	 * 消息超时值，15min
	 */
	protected static final long TIMEOUT_MILLIS = 1000 * 60 * 15;
}
