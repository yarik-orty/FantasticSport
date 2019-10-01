package io.makefun.fantasticsport.security.config

import io.makefun.fantasticsport.core.user.UserService
import io.makefun.fantasticsport.security.DefaultUserDetailsService
import io.makefun.fantasticsport.security.MongoTokenStore
import io.makefun.fantasticsport.security.Security
import io.makefun.fantasticsport.security.UserRole
import io.makefun.fantasticsport.security.repository.AccessTokenRepository
import io.makefun.fantasticsport.security.repository.RefreshTokenRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore

@Configuration
@EnableAuthorizationServer
class AuthServerConfig(private val service: DefaultUserDetailsService,
                       private val userService: UserService,
                       private val properties: SecurityProperties,
                       private val authenticationManager: AuthenticationManager,
                       private val accessTokenRepository: AccessTokenRepository,
                       private val refreshTokenRepository: RefreshTokenRepository,
                       private val encoder: PasswordEncoder,
                       private val security: Security) : AuthorizationServerConfigurerAdapter() {

    override fun configure(oauthServer: AuthorizationServerSecurityConfigurer) {
        oauthServer.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
    }

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
                .withClient(properties.clientId)
                .authorizedGrantTypes(*properties.grants)
                .authorities(UserRole.USER.role)
                .secret(encoder.encode(properties.clientSecret))
                .scopes(*properties.scopes)
                .resourceIds(Security.RESOURCE_ID)
                .accessTokenValiditySeconds(properties.accessTokenTime)
                .refreshTokenValiditySeconds(properties.refreshTokenTime)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(tokenStore())
                .authenticationManager(authenticationManager)
                .userDetailsService(service)
    }

    @Bean
    fun tokenStore(): TokenStore {
        return MongoTokenStore(accessTokenRepository, refreshTokenRepository, userService, security)
    }

    @Bean
    @Primary
    fun tokenServices(): DefaultTokenServices {
        val tokenServices = DefaultTokenServices()
        tokenServices.setTokenStore(tokenStore())
        tokenServices.setSupportRefreshToken(true)
        tokenServices.setAuthenticationManager(authenticationManager)
        return tokenServices
    }

//    @Bean
//    fun authenticationManager(): AuthenticationManager {
//        return ProviderManager(providers)
//    }

//    @Bean
//    @Primary
//    fun tokenServices(): DefaultTokenServices {
//        val tokenServices = DefaultTokenServices()
//        tokenServices.setTokenStore(tokenStore())
//        tokenServices.setSupportRefreshToken(true)
//        tokenServices.setAuthenticationManager(authenticationManager())
//        return tokenServices
//    }
}
