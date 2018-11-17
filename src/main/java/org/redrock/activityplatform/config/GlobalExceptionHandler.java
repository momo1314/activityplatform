package org.redrock.activityplatform.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;

/**
 * Created by momo on 2018/8/14
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ResponseBody
    //@ExceptionHandler(value={RuntimeException.class,Exception.class})
    @ExceptionHandler(Exception.class)//RuntimeException的父类
    public Object handleException(Exception e) {
        logger.error(ExceptionUtils.getFullStackTrace(e));  // 记录错误信息
        String msg = e.getMessage();
        if (msg == null || msg.equals("")) {
            msg = "server error!";
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", msg);
        return jsonObject;
    }
}
