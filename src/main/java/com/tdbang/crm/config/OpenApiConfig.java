/*
 * Copyright © 2025 by tdbang.
 * All rights reserved.
 */

package com.tdbang.crm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        // Security scheme name
        final String securitySchemeName = "oauth2";

        return new OpenAPI()
            .info(new Info()
                .title("CRM Services API")
                .version("v1")
                .description("CRM backend API with OAuth2 secured endpoints"))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .description("OAuth2 Authorization Code flow")
                        .flows(new OAuthFlows()
                            .authorizationCode(new OAuthFlow()
                                .authorizationUrl("http://localhost:8080/oauth2/authorize")
                                .tokenUrl("http://localhost:8080/oauth2/token")
                                .scopes(new Scopes()
                                    .addString("api:read", "Read access to API")
                                    .addString("api:write", "Write access to API")
                                )
                            )
                        )
                )
            );
    }
}
