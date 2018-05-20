package com.testCase;

import com.api.model.TestCase;
import com.api.utils.*;
import com.github.crab2died.ExcelUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ApiTest {

    public static void main(String[] args) {
        //定时执行
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        scheduledExecutorService.scheduleAtFixedRate(() -> testCase(),0, 60, TimeUnit.SECONDS);
    }

    private static void testCase(){
        String path = System.getProperty("user.dir") + File.separator + "Excel" + File.separator;
        try {
            List<TestCase> list = ExcelUtils.getInstance().readExcel2Objects(path + "ApiTest.xlsx", TestCase.class);
            for(TestCase testCase : list){
                if(testCase.getRun()) {
                    //进行关联
                    PatternUtils.matcher(testCase);
                    System.out.println(testCase.toString());
                    String result = null;
                    if ("get".equalsIgnoreCase(testCase.getType())) {
                        result = HttpClientUtils.doGet(testCase.getUrl(), MapUtils.covertStringToMap(testCase.getHeader()));
                    } else if ("post".equalsIgnoreCase(testCase.getType())) {
                        result = HttpClientUtils.doPost(testCase.getUrl(), MapUtils.covertStringToMap(testCase.getParams(), "&"), MapUtils.covertStringToMap(testCase.getHeader()));
                    } else if ("postjson".equalsIgnoreCase(testCase.getType())){
                        result = HttpClientUtils.doPostJson(testCase.getUrl(), testCase.getParams(), MapUtils.covertStringToMap(testCase.getHeader()));
                    }
                    System.out.println("返回结果：" + result);

                    boolean check = CheckPointUtils.checkbyJsonPath(result, testCase.getCheck());
                    System.out.println("check: " + check);

                    // 判断检查点，接口通过的进行关联
                    if (check) {
                        // 有关联值，将值存起来
                        SaveParamsUtils.saveMapbyJsonPath(result, testCase.getCorrelation());
                        testCase.setResult("测试通过");
                    }else{
                        testCase.setResult("测试失败");
                    }
                }else{
                    testCase.setResult("未测试");
                }
            }

            //回写结果
            String path_result = System.getProperty("user.dir") + File.separator + "Excel" + File.separator + "result_" + DateUtils.getCurrentTime() + ".xlsx";
            ExcelUtils.getInstance().exportObjects2Excel( list, TestCase.class, path_result );

            // 发送邮件
            //EmailUtils.sendEmailsWithAttachments("接口测试结果", "请查收, 邮件自动发送，请勿回复！", path_result);

            // 多个附件的邮件发送
            EmailUtils.sendEmailsWithAttachments("接口测试结果", "请查收, 邮件自动发送，请勿回复！", path_result, path_result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
