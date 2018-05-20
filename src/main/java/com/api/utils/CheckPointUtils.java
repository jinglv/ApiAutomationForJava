package com.api.utils;

import com.googlecode.aviator.AviatorEvaluator;
import com.jayway.jsonpath.JsonPath;

import java.util.HashMap;
import java.util.Map;

/**
 * 检查点校验
 * JsonPath进行取值
 * 结合AviatorEvaluator表达式进行判断
 */
public class CheckPointUtils {
    /**
     * json的数据转换,提取,表达式比较
     * @param json 返回的json字符串
     * @param params Excel中传入"检查点"字段
     * @return
     */
    public static boolean checkbyJsonPath(String json, String params) {
        //参数非空判断
        if (params != null && !"".equals(params) && !"null".equals(params)) {
            String[] ps = params.split(";");  //多个参数，以逗号分割
            for (String p : ps) {
                String[] values = p.split("=|>|<|>=|<=");   // | 或符号
                params = params.replace(values[0], "data"); // value[0]是jsonPath表达式，值替换为data,获取的value值的JSONPath有$，AviatorEvaluator不支持$，则需要进行替换
                Map<String, Object> env = new HashMap<String, Object>();
                //提取数据
                Object value = JsonPath.read(json, values[0]); //根据jsonPath的表达式提取值
                if(value instanceof String) { //判断Object是String类型的，字符串的比较处理
                    params = params.replace(values[1], covertToAviatorString(values[1])); //字符串是需要'号，则进行替换
                    params = params.replace("=", "=="); //AviatorEvaluator框架中字符串的相等判断 ==
                }
                env.put("data", value);
                Boolean result = (Boolean) AviatorEvaluator.execute(params, env);
                return result;
            }
        }
        // 不进行比较则返回true
        return true;
    }

    /**
     * 数据添加单引号，AviatorEvaluator中的字符串需要''引起来
     * @param value
     * @return
     */
    private static String covertToAviatorString(String value) {
        return "'"+value+"'";
    }
}
