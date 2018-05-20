package com.api.utils;

import com.jayway.jsonpath.JsonPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关联
 * 接口之间有数据的引用，将数据提取
 */
public class SaveParamsUtils {
    private static Map<String, Object> saveMap = new HashMap<String, Object>();

    /**
     * 关联表达式的处理
     * @param json 返回的json字符串
     * @param save Excel中传入"关联"字段
     */
    public static void saveMapbyJsonPath(String json, String save){
        Map<String,Object> map = MapUtils.covertStringToMap( save );  //表达式进行处理，转为Map
        //非空判断
        if(map!=null){
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                // 以*结尾的是数组，关联数组，格式示例：name_*=$..name
                if(key.endsWith( "*" )){ //key 以*结尾的为list
                    String before = key.split( "_" )[0];
                    List<Object> list = JsonPath.read( json, entry.getValue().toString() );
                    for(int i=0; i<list.size(); i++){
                        saveMap.put( before + "_" + i, list.get( i ) );
                    }
                }else{
                    //提取JsonPath数据, 单个参数
                    saveMap.put(key, JsonPath.read(json, entry.getValue().toString()));
                }
            }
        }
    }

    /**
     * 数据获取
     * @param key
     * @return
     */
    public static Object get(String key) {
        return saveMap.get(key);
    }
}
