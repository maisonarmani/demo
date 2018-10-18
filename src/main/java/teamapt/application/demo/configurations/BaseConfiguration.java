package teamapt.application.demo.configurations;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import teamapt.application.demo.domains.Base;
import teamapt.application.demo.services.Service;

@Configuration
public class BaseConfiguration {
    @Autowired
    Service service;


    @Bean(name = "base")
    Base getBase(){
        return  service.getBase();
    }


}
