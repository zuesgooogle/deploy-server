package com.simplegame.deploy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年9月16日 下午3:10:52
 *
 */

@SpringBootApplication
@ImportResource(value="classpath:/spring/application-context.xml")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
