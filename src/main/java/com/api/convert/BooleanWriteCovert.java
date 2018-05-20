package com.api.convert;

import com.github.crab2died.converter.WriteConvertible;

public class BooleanWriteCovert implements WriteConvertible {
    @Override
    public Object execWrite(Object o) {
        boolean b = (Boolean)o;
        return !b ? "未执行" : "是";
    }
}
