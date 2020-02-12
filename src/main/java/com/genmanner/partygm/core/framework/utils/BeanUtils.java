package com.genmanner.partygm.core.framework.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;

/**
 * 对Bean进行操作的相关工具方法
 *  
 *
 */
public class BeanUtils {
	/**
	 * 将Bean对象转换成Map对象，将忽略掉值为null或size=0的属性
	 * @param obj 对象
	 * @return 若给定对象为null则返回size=0的map对象
	 */
	public static Map<String, Object> toMap(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (obj == null) {
			return map;
		}
		BeanMap beanMap = new BeanMap(obj);
		Iterator<String> it = beanMap.keyIterator();
		while (it.hasNext()) {
			String name = it.next();
			Object value = beanMap.get(name);
			// 转换时会将类名也转换成属性，此处去掉
			if (value != null && !name.equals("class")) {
				map.put(name, value);
			}
		}
		return map;
	}

	/**
	 * 该方法将给定的所有对象参数列表转换合并生成一个Map，对于同名属性，依次由后面替换前面的对象属性
	 * @param objs 对象列表
	 * @return 对于值为null的对象将忽略掉
	 */
	public static Map<String, Object> toMap(Object... objs) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Object object : objs) {
			if (object != null) {
				map.putAll(toMap(object));
			}
		}
		return map;
	}

	/**
	 * 获取接口的泛型类型，如果不存在则返回null
	 * @param clazz
	 * @return
	 */
	public static Class<?> getGenericClass(Class<?> clazz) {
		Type t = clazz.getGenericSuperclass();
		if (t instanceof ParameterizedType) {
			Type[] p = ((ParameterizedType) t).getActualTypeArguments();
			return ((Class<?>) p[0]);
		}
		return null;
	}
	
	/**
	 * 深度克隆对象
	 * @param cloneTargetObj			:克隆目标对象
	 * @return
	 */
	public static Object deepCloneObj(Object cloneTargetObj) {
		Object retObject = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
		try {
			/*将对象写到流里*/
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(cloneTargetObj);
			oos.flush();
	        /*从流里读回来*/
	        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bis);
			retObject = ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != oos) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(null != ois) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(null != bos) {//可以忽略(oos里已经关闭bos)
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        
        return retObject;
    }
	
	/**
	 * 序列化对象,并返回字节数组
	 * @param targetObj		:序列化目标对象
	 * @return				:字节数组
	 */
	public static byte[] serialObj(Object targetObj) {
		byte[] retBytes = null;
		ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(targetObj);
			oos.flush();
			retBytes = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != oos) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(null != bos) {//可以忽略(oos里已经关闭bos)
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return retBytes;
	}
	
	/**
	 * 序列化对象,并返回字节数组
	 * @param targetObj		:序列化目标对象
	 * @return				:字节数组
	 */
	/**
	 * 序列化对象,并返回字节数组
	 * @param targetObj		:序列化目标对象
	 * @return				:字节数组
	 */
	public static <T> T derialObj(byte[] objBytes) {
		T retObj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
		try {
	        bis = new ByteArrayInputStream(objBytes);
			ois = new ObjectInputStream(bis);
			retObj = (T) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(null != ois) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        
		return retObj;
	}
	
}