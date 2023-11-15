package io.springstudent.meeting.common.handler;

import cn.hutool.core.util.StrUtil;
import io.springstudent.meeting.common.exception.DataNotFoundException;
import io.springstudent.meeting.common.exception.ParamInvalidException;
import io.springstudent.meeting.common.exception.ResultException;
import io.springstudent.meeting.common.http.ResponseResult;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseResult<Object> exceptionHandler(HttpServletRequest req, Exception e) {
        if (StrUtil.isNotEmpty(e.getMessage()) && e.getMessage().indexOf("提示") != -1) {
            return ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
        return ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务内部错误");
    }


    @ExceptionHandler(IncorrectCredentialsException.class)
    @ResponseBody
    public ResponseResult UnAuthorizedExceptionHandler(IncorrectCredentialsException e) {
        return ResponseResult.fail(HttpStatus.UNAUTHORIZED.value(), "账号或密码不正确");
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public ResponseResult UnauthorizedException(UnauthorizedException e) {
        return ResponseResult.fail(HttpStatus.UNAUTHORIZED.value(), "用户登录未被授权");
    }

    @ExceptionHandler(UnauthenticatedException.class)
    @ResponseBody
    public ResponseResult UnauthenticatedException(UnauthenticatedException e) {
        return ResponseResult.fail(HttpStatus.UNAUTHORIZED.value(), "用户未登录");
    }

    @ExceptionHandler(value = ParamInvalidException.class)
    @ResponseBody
    public ResponseResult<Object> paramInvalidExceptionHandler(HttpServletRequest req, ParamInvalidException e) throws Exception {
        return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = DataNotFoundException.class)
    @ResponseBody
    public ResponseResult<Object> dataNotFoundExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        return ResponseResult.fail(HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage());
    }

    @ExceptionHandler(value = ResultException.class)
    @ResponseBody
    public ResponseResult<Object> resultExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        return ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

}

