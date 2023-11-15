package io.springstudent.meeting.common.exception;

public class ParamInvalidException extends ApplicationException{
    public ParamInvalidException() {
        super();
    }

    public ParamInvalidException(String message) {
        super(message);
    }

    public ParamInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamInvalidException(Throwable cause) {
        super(cause);
    }

    public ParamInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
