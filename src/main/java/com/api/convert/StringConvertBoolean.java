package com.api.convert;

import com.github.crab2died.converter.ReadConvertible;

public class StringConvertBoolean implements ReadConvertible{
    @Override
    public Object execRead(String s) {
        return "是".equalsIgnoreCase(s);
    }
}
