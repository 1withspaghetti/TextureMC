package me.the1withspaghetti.texturemc.frontend;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	private final String filePath = System.getProperty("user.dir")+File.separator;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	String staticFilePath = "file:"+this.filePath+"static"+File.separator;
    	String assetFilePath = "file:"+this.filePath+"assets"+File.separator;
    	registry.addResourceHandler("/assets/**")
			.addResourceLocations(assetFilePath)
			.setCachePeriod(1800);
        registry.addResourceHandler("/**")
			.addResourceLocations(staticFilePath)
			.setCachePeriod(60);
        System.out.println("Serving static files from: "+staticFilePath);
        System.out.println("Serving asset files from: "+assetFilePath);
    }
    
    @Bean
    @Description("Thymeleaf template resolver serving HTML 5")
    public FileTemplateResolver templateResolver() {

        var tr = new FileTemplateResolver();
        tr.setPrefix(filePath+"static"+File.separator);
        tr.setCacheable(false);
        tr.setSuffix("/index.html");
        tr.setTemplateMode("HTML");
        tr.setCharacterEncoding("UTF-8");
        return tr;
    }

    @Bean
    @Description("Thymeleaf template engine with Spring integration")
    public SpringTemplateEngine templateEngine() {

        var templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());

        return templateEngine;
    }

    @Bean
    @Description("Thymeleaf view resolver")
    public ViewResolver viewResolver() {

        var viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");

        return viewResolver;
    }
}