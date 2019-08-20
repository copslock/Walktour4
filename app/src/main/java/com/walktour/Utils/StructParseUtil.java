package com.walktour.Utils;

import com.walktour.model.BaseStructParseModel;
import com.walktour.model.BaseStructParseModel.StructType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * scanner结构体解析工具类
 * 
 * @author jianchao.wang
 * 
 */
public class StructParseUtil {
	/**
	 * 解析指定的数据到对象中属性中
	 * 
	 * @param obj
	 *            保存数据的对象
	 * @param bytes
	 *            数据
	 * @return 解析生成的对象列表
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T extends BaseStructParseModel> List<T> parse(T obj,
			byte[] bytes) throws Exception {
		if (obj.propMap.isEmpty() || bytes == null || bytes.length == 0)
			return null;
		int size = bytes.length / obj.getModelLen();
		List<T> list = new ArrayList<T>();
		Field[] fields = obj.getClass().getDeclaredFields();
		int pos = 0;
		if (size == 1) {
			parse(obj, bytes, fields, pos);
			list.add(obj);
		} else {
			for (int index = 0; index < size; index++) {
				T obj1 = (T) obj.getClass().newInstance();
				pos = parse(obj1, bytes, fields, pos);
				list.add(obj1);
			}
		}
		return list;
	}

	/**
	 * 解析指定的数据到对象中属性中
	 * 
	 * @param obj
	 *            保存数据的对象
	 * @param bytes
	 *            数据
	 * @param fields
	 *            属性数组
	 * @param pos
	 *            当前位置
	 * @return
	 */
	private static <T extends BaseStructParseModel> int parse(T obj,
			byte[] bytes, Field[] fields, int pos) {
		for (String propName : obj.propMap.keySet()) {
			BaseStructParseModel.StructType structType = obj.propMap
					.get(propName);
			for (Field field : fields) {
				if (field.getName().equals(propName)) {
					try {
						byte[] value = new byte[structType.getLen()];
						for (int i = 0, j = pos; j < pos + structType.getLen(); i++, j++) {
							value[i] = bytes[j];
						}
						pos += structType.getLen();
						if (structType == StructType.Int) {
							field.setInt(obj, bytes2int(value));
						} else if (structType == StructType.Int64) {
							field.setLong(obj, bytes2long(value));
						} else if (structType == StructType.Double) {
							field.setDouble(obj, byte2Double(value));
						} else {
							field.setFloat(obj, bytes2float(value));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		return pos;
	}

	/**
	 * byte数组转换成整型
	 * 
	 * @param bytes
	 *            数据
	 * @return 整型
	 */
	private static int bytes2int(byte[] bytes) {
		int value = 0;
		byte bLoop;

		for (int i = 0; i < bytes.length; i++) {
			bLoop = bytes[i];
			value += (bLoop & 0xFF) << (8 * i);
		}
		return value;
	}

	/**
	 * byte数组转换成长整型
	 * 
	 * @param bytes
	 *            数据
	 * @return 长整型
	 */
	public static long bytes2long(byte[] bytes) {
		long iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < bytes.length; i++) {
			bLoop = bytes[i];
			iOutcome += ((long) (bLoop & 0x000000ff)) << (8 * i);
		}
		return iOutcome;
	}

	/**
	 * byte数组转换成浮点型
	 * 
	 * @param bytes
	 *            数据
	 * @return 浮点型
	 */
	private static float bytes2float(byte[] bytes) {
		return Float.intBitsToFloat(bytes2int(bytes));
	}
	
	
	private static double byte2Double(byte[] bytes){
		return Double.longBitsToDouble(bytes2int(bytes));
	}
}
