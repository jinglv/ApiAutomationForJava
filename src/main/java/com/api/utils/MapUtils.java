package com.api.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数字符串转Map
 */
public class MapUtils {
    /**
     * Map数据转换
     * @param params 参数
     * @param regex 多参数之间的分割符号
     * @return
     */
    public static Map<String, Object> covertStringToMap(String params, String regex) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        if (params != null) {
            String[] strp = params.split( regex );
            for (int i = 0; i < strp.length; i++) {
                String singleparms = strp[i];
                String[] key_values = singleparms.split( "=" ); //单个参数是key=value的形式
                paramsMap.put( key_values[0], key_values[1] );
            }
        }
        return paramsMap;
    }

    /**
     * Map数据转换,默认以;分割
     * @param params
     * @return
     */
    public static Map<String, Object> covertStringToMap(String params) {
        return covertStringToMap( params, ";" );
    }
}
