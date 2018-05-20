package com.api.model;

import com.api.convert.BooleanWriteCovert;
import com.api.convert.StringConvertBoolean;
import com.github.crab2died.annotation.ExcelField;
import lombok.Data;

@Data
public class TestCase {

    @ExcelField(title = "是否执行", readConverter = StringConvertBoolean.class, writeConverter = BooleanWriteCovert.class)
    private Boolean run;

    @ExcelField(title = "接口名称")
    private String apiName;

    @ExcelField(title = "描述")
    private String description;

    @ExcelField(title = "类型")
    private String type;

    @ExcelField(title = "地址")
    private String url;

    @ExcelField(title = "参数")
    private String params;

    @ExcelField(title = "头部")
    private String header;

    @ExcelField(title = "测试结果", order = 1)
    private String result;

    @ExcelField(title = "检查点")
    private String check;

    @ExcelField(title = "关联")
    private String correlation;
}
