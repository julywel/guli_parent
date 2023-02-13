package com.atguigu.guli.service.base.handler;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 统一异常中的异常处理
 * 相当于给接口一个切面
 * 接口通知
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 统一异常处理器
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e){
//        e.printStackTrace();
        //使用工具类将错误信息包装成堆栈跟踪形式
        //通过log打印会输出在logback.xml配置文件中配置的日志文件中
        log.error(ExceptionUtils.getMessage(e));
        return R.error();
    }


    /**
     * 特殊异常处理
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseBody
    public R error(BadSqlGrammarException e){
//        e.printStackTrace();
        log.error(ExceptionUtils.getMessage(e));
        return R.setResult(ResultCodeEnum.BAD_SQL_GRAMMAR);
    }

    /**
     * 处理json异常的处理器
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public R error(HttpMessageNotReadableException e){
        //使用这个会被直接打印在控制台，这样错误日志文件就不会记录错误
//        e.printStackTrace();
        log.error(ExceptionUtils.getMessage(e));
        return R.setResult(ResultCodeEnum.JSON_PARSE_ERROR);
    }


    //捕获的是全局自定义异常
    @ExceptionHandler(GuliException.class)
    @ResponseBody
    public R error(GuliException e){
        //使用这个会被直接打印在控制台，这样错误日志文件就不会记录错误
//        e.printStackTrace();
        log.error(ExceptionUtils.getMessage(e));
        return R.error().message(e.getMessage()).code(e.getCode());
    }
}
