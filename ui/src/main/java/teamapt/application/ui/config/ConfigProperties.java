package teamapt.application.ui.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@PropertySource("classpath:ftp.properties")
@ConfigurationProperties(prefix = "ftp")
public class ConfigProperties {

    String host;
    Integer port;
    String username;
    String password;
    String fromFileDirectory;
    String toFileDirectory;
    String fileName;
    String proxyHost;
    Integer proxyPort;
    Boolean useProxy;


    public Boolean getUseProxy() {
        return useProxy;
    }

    public void setUseProxy(Boolean useProxy) {
        this.useProxy = useProxy;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getFromFileDirectory() {
        return fromFileDirectory;
    }

    public void setFromFileDirectory(String fromFileDirectory) {
        this.fromFileDirectory = fromFileDirectory;
    }

    public String getToFileDirectory() {
        return toFileDirectory;
    }

    public void setToFileDirectory(String toFileDirectory) {
        this.toFileDirectory = toFileDirectory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String usernmae) {
        this.username = usernmae;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties(){
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        Resource[] resources = new ClassPathResource[]{ new ClassPathResource("ftp.properties") };
        pspc.setLocations( resources );
        pspc.setIgnoreUnresolvablePlaceholders( true );
        return pspc;
    }
}
