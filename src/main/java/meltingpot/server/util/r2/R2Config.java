package meltingpot.server.util.r2;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class R2Config {
    @Value("${cloud.r2.accessKey}")
    private String accessKey;
    @Value("${cloud.r2.secretKey}")
    private String secretKey;
    @Value("${cloud.r2.url}")
    private String url;

    @Bean
    @Primary
    public BasicAWSCredentials r2CredentialsProvider(){
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public AmazonS3 cloudflareR2() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AmazonS3ClientBuilder.EndpointConfiguration(url, "apac"))
                .withCredentials(new AWSStaticCredentialsProvider(r2CredentialsProvider()))
                .build();
    }
}