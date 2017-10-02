package org.multieditor.web;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;
import org.multieditor.rest.client.MultieditorJSONMapper;

import javax.ws.rs.ApplicationPath;
import java.util.logging.Logger;

@ApplicationPath("rest")
public class MultieditorRestApplication extends ResourceConfig {

    Logger LOGGER = Logger.getLogger(MultieditorRestApplication.class.getName());

    public MultieditorRestApplication() {

        LOGGER.info("Configuration Multieditor REST Application");

        LOGGER.info("Register Jackson");
        register(JacksonFeature.class);

        LOGGER.info("Register MultieditorJSONMapper");
        register(MultieditorJSONMapper.class);

//        disabled to avoid extra logging
//        LOGGER.info("Register UserCookieFilter");
//        register(UserCookieFilter.class);
//        register(UserAuthenticationFilter.class);

        LOGGER.info("Register packages");
        packages("org.multieditor.web", "org.multieditor.web.service");

        LOGGER.info("Enabling GZip filter");
        EncodingFilter.enableFor(this, GZipEncoder.class);

        // Enable for debugging:
        //LOGGER.info("Enable logging");
        //registerInstances(new LoggingFilter(LOGGER, true));
    }
}