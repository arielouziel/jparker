package com.aouziel.jparker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
@Slf4j
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ServletContext servletContext;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.address}")
    private String serverAddress;


    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        servletContext.getContextPath();

        log.info("");
        log.info("Aloha, JParker is now ready on http://{}:{}{}", serverAddress, serverPort, servletContext.getContextPath());
        log.info("To shut it down, press <CTRL> + C at any time.");
        log.info("");
        log.info("-------------------------------------------------------");
        log.info("API Docs     : http://{}:{}{}/v2/api-docs", serverAddress, serverPort, servletContext.getContextPath());
        log.info("-------------------------------------------------------");
        log.info("");
    }
}
