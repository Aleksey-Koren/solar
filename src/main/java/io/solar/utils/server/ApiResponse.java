package io.solar.utils.server;

public class ApiResponse {
    private Object body;
    private int code;

    public ApiResponse() {
        code = 200;
    }
    public ApiResponse(Object body) {
        this();
        this.body = body;
    }

    public ApiResponse(Object body, int code) {
        this(body);
        this.code = code;
    }


    public Object getBody() {
        return body;
    }

    public int getCode() {
        return code;
    }
}
