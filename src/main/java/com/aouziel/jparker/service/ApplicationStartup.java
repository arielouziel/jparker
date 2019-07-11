package com.aouziel.jparker.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;

@Component
@Slf4j
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private BuildProperties buildProperties;

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
        log.info("Version      : {}", buildProperties.getVersion());
        log.info("");
        log.info("API Docs     : http://{}:{}{}/v2/api-docs", serverAddress, serverPort, servletContext.getContextPath());
        log.info("-------------------------------------------------------");
        log.info("");
    }
}
