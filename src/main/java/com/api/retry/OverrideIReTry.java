package com.api.retry;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.HashMap;
import java.util.Map;

/**
 * retry规则
 */
public class OverrideIReTry implements IRetryAnalyzer {

    public int maxReTryNum=3;

    //字符串统计
    static Map<String,Integer> countmp = new HashMap<String,Integer>();

    @Override
    public boolean retry(ITestResult iTestResult) {
        Object testcase = iTestResult.getParameters()[0];
        int initReTryNum = getTestCaseErrorCount(testcase.toString());
        System.out.println(testcase + " ---- " + initReTryNum);
        if(initReTryNum<=maxReTryNum){
            String message="方法<"+iTestResult.getName()+">执行失败，重试第"+initReTryNum+"次";
            System.out.println(message);
            Reporter.setCurrentTestResult(iTestResult);
            Reporter.log(message);
            //initReTryNum++;
            return true;
        }
        return false;
    }

    //对象的统计，Map的key是不可重复的
    private int getTestCaseErrorCount(String key) {
        if(countmp.containsKey(key)) {
            int count = countmp.get(key);
            count++;
            countmp.put(key, count);
        }else {
            countmp.put(key, 1);
        }
        return countmp.get(key);
    }
}
