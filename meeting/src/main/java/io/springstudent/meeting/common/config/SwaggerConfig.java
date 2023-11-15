package io.springstudent.meeting.common.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    UiConfiguration uiConfig() {
        return new UiConfiguration(true, false, 1, 1,
                ModelRendering.MODEL, true, DocExpansion.NONE, true, null,
                OperationsSorter.ALPHA, true, false, null,
                UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, "", "http://127.0.0.1:9876/meeting");
    }

    @Bean
    public Docket createDefaultDocker() {
        return new Docket(DocumentationType.OAS_30).apiInfo(apiInfo("1.0.0"))
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.springstudent.meeting"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(requestParameterInfo())
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo(String version) {
        return new ApiInfoBuilder()
                .title("会议系统")
                .description("接口文档")
                .contact(new Contact("", "", ""))
                .version(version)
                .build();
    }

    private List<RequestParameter> requestParameterInfo() {
        return Arrays.asList(new RequestParameterBuilder().name("Token").description("鉴权认证").in(ParameterType.HEADER).build());
    }

    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> securitySchemes = new ArrayList<>();
        securitySchemes.add(new ApiKey("Token", "Token", "header"));
        return securitySchemes;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(SecurityContext.builder()
                .securityReferences(defaultAuth()).build());
        return securityContexts;
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Token", authorizationScopes));
        return securityReferences;
    }

}
