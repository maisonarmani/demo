package teamapt.application.ui.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

@Configuration
public class SandBoxConfig {


    @Value("${olympus.lead.url:http://localhost:90/}")
    private String url;

    @Bean("olympusLead")
    public HashMap<String, String> olympusLead(){
        HashMap<String, String> map = new HashMap();
        map.put("url",url);
        return map;
    }

    @Bean("fileFinder")
    public Object fileFinder(){
        String  filePath  = "sync-ho.csv";

        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String commaSeparator = ",";
            reader.readLine();
        }catch (Exception expe){
            System.out.println(expe.getMessage());
            return null;
        }

        return null;
    }


    public static PropertySourcesPlaceholderConfigurer loadProperties(){
        PropertySourcesPlaceholderConfigurer propertySPC =
                new PropertySourcesPlaceholderConfigurer();
        ClassPathResource[] resources = new ClassPathResource[]{
                new ClassPathResource( "*.properties" )
        };
        propertySPC.setLocations(resources);
        propertySPC.setIgnoreUnresolvablePlaceholders( true );
        return propertySPC;
    }

}
