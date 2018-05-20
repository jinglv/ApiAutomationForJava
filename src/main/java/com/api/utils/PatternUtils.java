package com.api.utils;

import com.api.model.TestCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 关联的数据提取后，传递到下一个接口使用
 * 正则表达式提取数据
 */
public class PatternUtils {
    private static Pattern replaceParamPattern = Pattern.compile( "\\$\\{(.*?)\\}" ); // ${(.*?)} ${id} 两个反斜杠\\（转义），在正则表达式中遇到特殊符号，则需要加\\

    public static void matcher(TestCase bean) {
        Matcher matcher = replaceParamPattern.matcher( bean.getUrl() ); //匹配Url
        while (matcher.find()) {
            String value = SaveParamsUtils.get( matcher.group( 1 ) ).toString(); //获取id值
            String newUrl = bean.getUrl().replace( matcher.group(), value ); //替换值
            bean.setUrl( newUrl );
        }
    }

    public static void matcherParams(TestCase bean){
        Matcher matcher = replaceParamPattern.matcher(bean.getParams());
        while (matcher.find()){
            String value = SaveParamsUtils.get(matcher.group(1)).toString();
            String newParam = bean.getUrl().replace(matcher.group(), value);
            bean.setParams(newParam);
        }
    }

    public static String matcher(String url) {
        Matcher matcher = replaceParamPattern.matcher( url ); //匹配Url
        String newUrl = null;
        while (matcher.find()) {
            String value = SaveParamsUtils.get( matcher.group( 1 ) ).toString(); //获取id值
            newUrl = url.replace( matcher.group(), value ); //替换值
        }
        return newUrl;
    }
}
