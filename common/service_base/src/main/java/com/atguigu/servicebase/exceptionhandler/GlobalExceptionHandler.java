package com.atguigu.servicebase.exceptionhandler;


import com.atguigu.commonutils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    //因为他不在Controller中。没有@RestController，所以数据不会返回，需要加@ResponeseBody返回数据
    public R error(Exception e){
        e.printStackTrace();
        return R.error().message("执行了全局异常...");
    }

    //特定异常
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public R error(ArithmeticException e){
        e.printStackTrace();
        return R.error().message("执行了ArithmeticException异常....");
    }

    @ExceptionHandler
    @ResponseBody
    public R error(GuliException e){
        e.printStackTrace();
        log.error(e.getMsg());
        return R.error().message(e.getMsg()).code(e.getCode());
    }
}
