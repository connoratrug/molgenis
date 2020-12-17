package org.molgenis.api.notification;

import org.molgenis.util.i18n.PropertiesMessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiNotificationL10nConfig {
  public static final String NAMESPACE = "api-notification";

  @Bean
  public PropertiesMessageSource apiNotificationMessageSource() {
    return new PropertiesMessageSource(NAMESPACE);
  }
}
