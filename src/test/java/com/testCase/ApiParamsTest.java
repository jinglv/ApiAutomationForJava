package com.testCase;

import com.api.exception.HttpClientException;
import com.api.model.TestCase;
import com.api.utils.*;
import com.github.crab2died.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApiParamsTest {

    @Test(dataProvider = "excelData") // , invocationCount = 5, threadPoolSize = 3
    public void testCase(TestCase testCase) throws HttpClientException {
        System.out.println("Thread----" + Thread.currentThread().getName());
        //进行关联
        PatternUtils.matcher(testCase);
        String result = null;
        if ("get".equalsIgnoreCase(testCase.getType())) {
            result = HttpClientUtils.doGet(testCase.getUrl(), MapUtils.covertStringToMap(testCase.getHeader()));
        } else if ("post".equalsIgnoreCase(testCase.getType())) {
            result = HttpClientUtils.doPost(testCase.getUrl(), MapUtils.covertStringToMap(testCase.getParams(), "&"), MapUtils.covertStringToMap(testCase.getHeader()));
        } else if ("postjson".equalsIgnoreCase(testCase.getType())){
            result = HttpClientUtils.doPostJson(testCase.getUrl(), testCase.getParams(), MapUtils.covertStringToMap(testCase.getHeader()));
        }

        boolean check = CheckPointUtils.checkbyJsonPath(result, testCase.getCheck());

        // 判断检查点，接口通过的进行关联
        if (check) {
            // 有关联值，将值存起来
            SaveParamsUtils.saveMapbyJsonPath(result, testCase.getCorrelation());
        }

        Assert.assertEquals(check, true);
    }

    @DataProvider(name = "excelData")
    public Iterator<Object[]> parameterIntTestProvider(){
        List<Object[]> dataProvider = new ArrayList<Object[]>();
        String path = System.getProperty("user.dir") + File.separator + "Excel" +File.separator;
        try {
            List<TestCase> list = ExcelUtils.getInstance().readExcel2Objects(path + "ApiTest.xlsx", TestCase.class);
            for(TestCase testCase: list){
                if(testCase.getRun()){
                    dataProvider.add(new Object[]{testCase});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataProvider.iterator();
    }
}
