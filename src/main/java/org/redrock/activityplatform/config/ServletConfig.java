package org.redrock.activityplatform.config;

import org.hibernate.validator.HibernateValidator;
import org.redrock.activityplatform.core.interceptor.InitInterceptor;
import org.redrock.activityplatform.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by momo on 2018/5/2
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "org.redrock.activityplatform.controller"
})
public class ServletConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private UserService userService;
    /**
     * 文件解析器
     */
//    @Bean
//    public MultipartResolver multipartResolver() {
//        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
//        resolver.setDefaultEncoding("UTF-8");
//        resolver.setMaxUploadSize(10485760000L);
//        resolver.setMaxInMemorySize(40960);
//        return resolver;
//    }

    /**
     * 处理适配器
     * 请求映射处理适配器
     */
//    @Bean
//    public HandlerAdapter requestMappingHandlerAdapter() {
//        List<MediaType> mediaTypes = new ArrayList<>();
//        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
//        mediaTypes.add(MediaType.TEXT_HTML);
//        mediaTypes.add(MediaType.TEXT_PLAIN);
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        List<HttpMessageConverter<?>> converters = new ArrayList<>();
//        converters.add(converter);
//        converter.setSupportedMediaTypes(mediaTypes);
//        RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
//        adapter.setMessageConverters(converters);
//        return adapter;
//    }
    /**
     * 注册全局拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new InitInterceptor(userService)).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    /**
     * 配置静态资源的处理
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /**
     * 配置静态资源路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/res").addResourceLocations("/res/**");
    }

    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        return validatorFactoryBean;
    }

//    /**
//     * 处理AJAX跨域
//     * @param registry
//     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedMethods("POST", "PUT", "OPTIONS", "DELETE", "PATCH")
//                .allowCredentials(true).maxAge(3600);
//    }


}
