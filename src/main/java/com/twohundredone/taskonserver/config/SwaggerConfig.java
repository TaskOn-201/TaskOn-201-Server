package com.twohundredone.taskonserver.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "TaskOn API 명세서",
                version = "v1",
                description = "TaskOn 프로젝트 API 문서입니다."
        )
)
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@SecurityScheme(
        name = "RefreshTokenCookie",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "refreshToken"
        )
public class SwaggerConfig {

        // ⭐ Swagger 서버 주소 자동 감지 설정
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .components(new Components())
                        // swagger servers 를 "/" 로 설정 → https 도메인 자동 인식됨
                        .addServersItem(new Server().url("/"));
        }
}
