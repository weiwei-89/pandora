package org.edward.pandora.common.model;

public class Response {
    public static final int OK = 200;
    public static final int ERROR = 500;

    private final int code;
    private final String msg;

    public Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

    private Object data;

    public Object getData() {
        return data;
    }
    public Response setData(Object data) {
        this.data = data;
        return this;
    }

    public static Response ok() {
        return new Response(OK, "ok");
    }

    public static Response error() {
        return new Response(ERROR, "error");
    }

    public static Response error(String msg) {
        return new Response(ERROR, msg);
    }
}