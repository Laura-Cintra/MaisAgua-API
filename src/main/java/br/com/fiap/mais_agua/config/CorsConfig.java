package br.com.fiap.fin_money_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// um bean é um objeto que é instanciado, configurado e gerenciado pelo contêiner Spring IoC
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST","DELETE", "PUT")
                .allowedHeaders("*");
    }
}

// registry.addMapping("/*"); -> /categories | /transcations | registry.addMapping("/**"); -> /categories/add