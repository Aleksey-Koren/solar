package io.solar.service.mail;

import java.util.Map;

public interface TemplateEmail {

    String getTitle();

    String getSendAddress();

    Map<String, Object> getTemplateVariables();

    String getTemplateFilename();
}
