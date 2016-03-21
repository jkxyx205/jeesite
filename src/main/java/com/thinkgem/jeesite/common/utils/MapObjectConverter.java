package com.thinkgem.jeesite.common.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rick.Xu on 2016/03/21.
 */
public class MapObjectConverter {

    private static String dateFormat = "yyyyMMdd";

    public static <T> T Map2Bean(Map<String,Object> map,Class<T> clazz) throws Exception {
        T t = clazz.newInstance();

        if (map == null || org.apache.commons.collections.MapUtils.isEmpty(map)) {
            return t;
        }
        BeanUtils.populate(t, map);
        return t;
    }

    public static Map<String,Object> bean2Map(Object object)  {
        return bean2Map(object,dateFormat);
    }


    public static Map<String,Object> bean2Map(Object object , String dateFormat)  {
        if(object == null)
            return Collections.emptyMap();

        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(object.getClass());
        Map<String,Object> objectMap = new HashMap<String,Object>(pds.length);

        try {
            for(PropertyDescriptor pd: pds) {
                String name = pd.getName();

                Class<?> type = pd.getPropertyType();

                if (!(String[].class == type || String.class == type
                        ||Integer.class == type || Integer[].class == type
                        || Long.class == type || Long[].class == type
                        || Double.class == type || Double[].class == type
                        || Date.class == type)) {
                    continue;
                }

                Object value = null;

                value = PropertyUtils.getProperty(object, name);


                if(value == null)
                    continue;

                if (String[].class == type) {
                    String[] arr = (String[])value;
                    StringBuilder sb = new StringBuilder();
                    for(String s : arr) {
                        sb.append(s).append(";");
                    }
                    objectMap.put(name,sb);
                } else if (Double[].class == type) {
                    Double[] arr = (Double[])value;
                    StringBuilder sb = new StringBuilder();
                    for(Double s : arr) {
                        sb.append(s).append(";");
                    }
                    objectMap.put(name,sb);
                } else if (Float[].class == type) {
                    Float[] arr = (Float[])value;
                    StringBuilder sb = new StringBuilder();
                    for(Float s : arr) {
                        sb.append(s).append(";");
                    }
                    objectMap.put(name,sb);
                } else if (Long[].class == type) {
                    Long[] arr = (Long[])value;
                    StringBuilder sb = new StringBuilder();
                    for(Long s : arr) {
                        sb.append(s).append(";");
                    }
                    objectMap.put(name,sb);
                } else if (Date.class == type) {
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                    objectMap.put(name,sdf.format(value));
                } else {
                    objectMap.put(name,value);
                }
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return objectMap;
    }
}