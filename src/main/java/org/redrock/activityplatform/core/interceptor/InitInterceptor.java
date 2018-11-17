package org.redrock.activityplatform.core.interceptor;

import io.jsonwebtoken.Claims;
import org.apache.commons.codec.digest.DigestUtils;
import org.redrock.activityplatform.core.annotation.BackJwt;
import org.redrock.activityplatform.core.annotation.NeedJwt;
import org.redrock.activityplatform.core.util.JwtHelper;
import org.redrock.activityplatform.data.domain.User;
import org.redrock.activityplatform.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by momo on 2018/5/2
 */

public class InitInterceptor implements HandlerInterceptor {
    public final static String ACCESS_TOKEN = "authorization";
    //固化用户(可以写到库里然后改库就不用经常重新编译，这里需求是这些就直接写死了)
    //public final static String[] ACCESS_USER = {"Redrock","dayishutuan","kelian","xueshenghui","shelian","bangongshi", "xuanchuanbu", "zuzhibu","qingxie"};
    public final static String[] ACCESS_USER = {"红岩网校工作站","大学生艺术团","学生科技联合会","校学生会","学生社团联合会","校团委办公室", "校团委宣传部", "校团委组织部","青年志愿者协会","勤工助学中心","重邮就业中心","重邮e站微+平台"};

    private UserService userService;

    public InitInterceptor(UserService userService){
        this.userService = userService;
    }

    /**
     *  在请求处理之前进行调用（方法调用之前）
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        NeedJwt methodAnnotation = method.getAnnotation(NeedJwt.class);
        // 有 @NeedJwt 注解，需要认证
        BackJwt back = method.getAnnotation(BackJwt.class);
        if (methodAnnotation != null) {
            // 判断是否存在令牌信息，如果存在，则验证jwt
            String accessToken = request.getHeader(ACCESS_TOKEN);
            if (null == accessToken) {
                throw new RuntimeException("无jwt，请重新进入");
            }
            Claims claims = JwtHelper.parseJWT(accessToken);
            String openid = claims.getId();
            //String openid = request.getParameter("openid");
            User user = userService.findByOpenid(openid);
            if (user == null) {
                throw new RuntimeException("jwt校验错误，请重新登录");
            }
            if(!openid.equals(user.getOpenid())){
                throw new RuntimeException("jwt校验错误，请重新登录");
            }
            //验证通过
            return true;
        }else if(back != null){
            String accessToken = request.getHeader(ACCESS_TOKEN);
            if (null == accessToken) {
                throw new RuntimeException("无jwt，请重新进入");
            }
            Claims claims = JwtHelper.parseJWT(accessToken);
            String check = claims.getId();
            List<String> list= Arrays.asList(ACCESS_USER);
            if(list.indexOf(check) == -1){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（方法调用之后）
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

//    public static void main(String[] args) {
//
//        System.out.println(list.indexOf("dayishutuan"));
//    }

}