package com.i5018.beetl.autoconfigure;

import com.i5018.beetl.autoconfigure.properties.BeetlProperties;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author limk
 * @date 2019/6/14 15:20
 */
@Configuration
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
@EnableConfigurationProperties(BeetlProperties.class)
public class BeetlAutoConfiguration {

    private final BeetlProperties beetlProperties;

    @Autowired
    public BeetlAutoConfiguration(BeetlProperties beetlProperties) {
        this.beetlProperties = beetlProperties;
    }

    @Bean(name = "beetlConfig", initMethod = "init")
    public BeetlGroupUtilConfiguration beetlConfig() {
        BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = BeetlAutoConfiguration.class.getClassLoader();
        }
        beetlGroupUtilConfiguration.setConfigProperties(beetlProperties.getProperties());
        ClasspathResourceLoader cploder = new ClasspathResourceLoader(loader,
                beetlProperties.getRoot());
        beetlGroupUtilConfiguration.setResourceLoader(cploder);
        beetlGroupUtilConfiguration.init();
        //如果使用了优化编译器，涉及到字节码操作，需要添加ClassLoader
        beetlGroupUtilConfiguration.getGroupTemplate().setClassLoader(loader);
        return beetlGroupUtilConfiguration;

    }

    @Bean(name = "beetlViewResolver")
    public BeetlSpringViewResolver getBeetlSpringViewResolver(BeetlGroupUtilConfiguration beetlConfig) {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setSuffix(beetlProperties.getSuffix());
        beetlSpringViewResolver.setContentType(beetlProperties.getContentType());
        beetlSpringViewResolver.setOrder(beetlProperties.getOrder());
        beetlSpringViewResolver.setConfig(beetlConfig);
        return beetlSpringViewResolver;
    }

    @Bean
    public GroupTemplate groupTemplate(BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
        return beetlGroupUtilConfiguration.getGroupTemplate();
    }

}
