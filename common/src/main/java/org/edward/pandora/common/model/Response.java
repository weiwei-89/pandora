package org.edward.pandora.common.model;

public class Response {
    private final int code;
    private final Object msg;

    public Response(int code, Object msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }
    public Object getMsg() {
        return msg;
    }

    private Object data;

    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
}