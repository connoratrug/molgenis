package org.molgenis.api.notification.v1;

import org.molgenis.api.notification.NotificationApiConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Import(NotificationApiConfig.class)
@Configuration
public class NotificationApiV3Config implements WebMvcConfigurer {}
