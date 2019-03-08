package com.thebund1st.daming.boot.jwt;

import com.thebund1st.daming.boot.aliyun.oss.OssJwtKeyConfiguration;
import com.thebund1st.daming.boot.jwt.key.file.FileJwtKeyConfiguration;
import com.thebund1st.daming.jwt.SmsVerifiedJwtIssuer;
import com.thebund1st.daming.jwt.key.JwtKeyLoader;
import com.thebund1st.daming.jwt.key.JwtPrivateKeyLoader;
import com.thebund1st.daming.jwt.key.KeyBytesLoader;
import com.thebund1st.daming.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@RequiredArgsConstructor
@Configuration
@Import({FileJwtKeyConfiguration.class, OssJwtKeyConfiguration.class})
public class JwtConfiguration {

    @ConfigurationProperties(prefix = "daming.jwt")
    @Bean
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @ConditionalOnMissingBean(JwtKeyLoader.class)
    @Bean
    public JwtPrivateKeyLoader jwtPrivateKeyLoader(KeyBytesLoader keyBytesLoader) {
        return new JwtPrivateKeyLoader(keyBytesLoader);
    }

    @ConditionalOnMissingBean(SmsVerifiedJwtIssuer.class)
    @Bean
    public SmsVerifiedJwtIssuer smsVerifiedJwtIssuer(Clock clock, JwtPrivateKeyLoader jwtPrivateKeyLoader) {
        SmsVerifiedJwtIssuer issuer = new SmsVerifiedJwtIssuer(clock, jwtPrivateKeyLoader.getKey());
        issuer.setExpires(jwtProperties().getExpires());
        return issuer;
    }
}