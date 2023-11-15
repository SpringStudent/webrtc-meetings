package io.springstudent.meeting.common.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseResult<T> {

    private int code;
    private String msg;
    private T result;

    private static final Integer HTTP_OK = 200;

    private static final Integer HTTP_INTERNAL_SERVER_ERROR = 500;

    private static final Integer HTTP_REQUEST_TIMEOUT = 408;

    public static <T> ResponseResult<T> success() {
        return new ResponseResultBuilder<T>().code(HTTP_OK).msg("success").build();
    }

    public static <T> ResponseResult<T> success(T result) {
        return new ResponseResultBuilder<T>().code(HTTP_OK).msg("success").result(result).build();
    }

    public static <T> ResponseResult<T> success(T result, String msg) {
        return new ResponseResultBuilder<T>().code(HTTP_OK).msg(msg).result(result).build();
    }

    public static <T> ResponseResult<T> fail() {
        return new ResponseResultBuilder<T>().code(HTTP_INTERNAL_SERVER_ERROR).build();
    }

    public static <T> ResponseResult<T> fail(Exception e) {
        return new ResponseResultBuilder<T>().code(HTTP_INTERNAL_SERVER_ERROR).msg(e.getMessage().toString()).build();
    }

    public static <T> ResponseResult<T> fail(String msg) {
        return new ResponseResultBuilder<T>().code(HTTP_INTERNAL_SERVER_ERROR).msg(msg).build();
    }

    public static <T> ResponseResult<T> fail(Integer code, String msg) {
        return new ResponseResultBuilder<T>().code(code).msg(msg).build();
    }

    public static String buildSuccessResultStr(Object result) {
        if (null != result && result instanceof String) {
            String res = (String) result;
            String replaceAllStr = res.replaceAll("\"", "\\\\\"");
            return "{\"msg\": \"success\",\"code\": " + HTTP_OK + ",\"result\": \"" + replaceAllStr.replaceAll("\"", "\\\"") + "\"}";
        }
        if (null == result) {
            return "{\"msg\": \"success\",\"code\": " + HTTP_OK + ",\"result\":  " + result + "}";
        }
        return "{\"msg\": \"success\",\"code\": " + HTTP_OK + ",\"result\": \"" + result.toString().replaceAll("\"", "\\\"") + "\"}";
    }
}
