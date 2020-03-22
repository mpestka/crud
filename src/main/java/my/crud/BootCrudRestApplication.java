package my.crud;

import java.time.Clock;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BootCrudRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootCrudRestApplication.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
