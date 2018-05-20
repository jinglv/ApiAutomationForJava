package com.api.exception;

public class HttpClientException extends Exception {
    /**
     * 序列号
     * 有一些对象需要在网络传输，网络中传输序列化就是唯一标识
     */
    private static final long serialVersionUID = 7273239360057790288L;

    public HttpClientException(String msg) {
        super(msg);
    }
}
