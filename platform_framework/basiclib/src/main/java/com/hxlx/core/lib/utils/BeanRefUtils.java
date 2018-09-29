package com.hxlx.core.lib.utils;

import com.google.gson.Gson;
import com.hxlx.core.lib.utils.common.utils.DateUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoujinjie
 * @date 2018/5/10
 * @description 利用反射给相同键值对bean赋值
 */
public class BeanRefUtils {

  private static final String TYPE_DATE = "Date";
  private static final String TYPE_STRING = "String";
  private static final String TYPE_INTEGER = "Integer";
  private static final String TYPE_INT = "int";
  private static final String TYPE_LONG = "Long";
  private static final String TYPE_DOUBLE = "Double";
  private static final String TYPE_BOOLEAN = "Boolean";
  private static final String KEY_TYPE_GET = "get";
  private static final String KEY_TYPE_SET = "set";

  /**
   * 取Bean的属性和值对应关系的MAP
   *
   * @param bean
   * @return Map
   */
  private static Map<String, String> getFieldValueMap(Object bean) {
    Class<?> cls = bean.getClass();
    Map<String, String> valueMap = new HashMap<String, String>();
    // 取出bean里的所有方法
    Method[] methods = cls.getDeclaredMethods();
    Field[] fields = cls.getDeclaredFields();

    for (Field field : fields) {
      try {
        String fieldType = field.getType().getSimpleName();
        String fieldGetName = parGetName(field.getName());
        if (!checkGetMet(methods, fieldGetName)) {
          continue;
        }
        Method fieldGetMet = cls.getMethod(fieldGetName);
        Object fieldVal = fieldGetMet.invoke(bean);
        String result = null;
        if (TYPE_DATE.equals(fieldType)) {
          result =
              DateUtil.date2str((Date) fieldVal, DateUtil.Format.YEAR_MOUTH_DAY_HOUR_MINUTE_SECOND);
        } else {
          if (null != fieldVal) {
            result = String.valueOf(fieldVal);
          }
        }
        valueMap.put(field.getName(), result);
      } catch (Exception e) {
        continue;
      }
    }
    return valueMap;

  }

  /**
   * set属性的值到Bean
   *
   * @param bean
   * @param valMap
   */
  private static void setFieldValue(Object bean, Map<String, String> valMap) {
    Class<?> cls = bean.getClass();
    // 取出bean里的所有方法
    Method[] methods = cls.getDeclaredMethods();
    Field[] fields = cls.getDeclaredFields();

    for (Field field : fields) {
      try {
        String fieldSetName = parSetName(field.getName());
        if (!checkSetMet(methods, fieldSetName)) {
          continue;
        }
        Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());
        String value = valMap.get(field.getName());
        if (null != value && !"".equals(value)) {
          String fieldType = field.getType().getSimpleName();
          if (TYPE_STRING.equals(fieldType)) {
            fieldSetMet.invoke(bean, value);
          } else if (TYPE_DATE.equals(fieldType)) {
            Date temp = DateUtil.str2dateRef(value);
            fieldSetMet.invoke(bean, temp);
          } else if (TYPE_INTEGER.equals(fieldType) || TYPE_INT.equals(fieldType)) {
            Integer intval = Integer.parseInt(value);
            fieldSetMet.invoke(bean, intval);
          } else if (TYPE_LONG.equalsIgnoreCase(fieldType)) {
            Long temp = Long.parseLong(value);
            fieldSetMet.invoke(bean, temp);
          } else if (TYPE_DOUBLE.equalsIgnoreCase(fieldType)) {
            Double temp = Double.parseDouble(value);
            fieldSetMet.invoke(bean, temp);
          } else if (TYPE_BOOLEAN.equalsIgnoreCase(fieldType)) {
            Boolean temp = Boolean.parseBoolean(value);
            fieldSetMet.invoke(bean, temp);
          } else {
            System.out.println("not supper type" + fieldType);
          }
        }
      } catch (Exception e) {
        continue;
      }
    }
  }

  /**
   * 判断是否存在某属性的 set方法
   *
   * @param methods
   * @param fieldSetMet
   * @return boolean
   */
  private static boolean checkSetMet(Method[] methods, String fieldSetMet) {
    for (Method met : methods) {
      if (fieldSetMet.equals(met.getName())) {
        return true;
      }
    }
    return false;
  }

  /**
   * 判断是否存在某属性的 get方法
   *
   * @param methods
   * @param fieldGetMet
   * @return boolean
   */
  private static boolean checkGetMet(Method[] methods, String fieldGetMet) {
    for (Method met : methods) {
      if (fieldGetMet.equals(met.getName())) {
        return true;
      }
    }
    return false;
  }

  /**
   * 拼接某属性的 get方法
   *
   * @param fieldName
   * @return String
   */
  private static String parGetName(String fieldName) {
    if (null == fieldName || "".equals(fieldName)) {
      return null;
    }
    return KEY_TYPE_GET + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }

  /**
   * 拼接在某属性的 set方法
   *
   * @param fieldName
   * @return String
   */
  private static String parSetName(String fieldName) {
    if (null == fieldName || "".equals(fieldName)) {
      return null;
    }
    return KEY_TYPE_SET + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }

  public static void copyProperties(Object newBean, Object oldBean) {
    setFieldValue(newBean, getFieldValueMap(oldBean));
  }

  public static <A, T> T modelConver(A oldModel, Class<T> nClass) {
    try {
      Gson gson = new Gson();
      String gsonA = gson.toJson(oldModel);
      T instanceB = gson.fromJson(gsonA, nClass);
      return instanceB;
    } catch (Exception e) {
      return null;
    }
  }
}
