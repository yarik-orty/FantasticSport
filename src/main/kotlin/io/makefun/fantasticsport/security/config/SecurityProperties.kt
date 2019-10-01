package io.makefun.fantasticsport.security.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@Configuration
@ConfigurationProperties(prefix = "security.auth")
class SecurityProperties {

    @NotBlank
    lateinit var clientId: String
    @NotBlank
    lateinit var clientSecret: String

    lateinit var grants: Array<String>
    lateinit var scopes: Array<String>

    var accessTokenTime: Int = 0
    var refreshTokenTime: Int = 0
}