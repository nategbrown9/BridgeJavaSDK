package org.sagebionetworks.bridge.sdk;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

final class Config {

    Utilities utils = Utilities.getInstance();

    private static final String DEFAULT_CONFIG = "bridge-sdk.properties";

    private static final String[] properties = new String[17];

    private static final String PARTICIPANT_EMAIL = properties[0] = "PARTICIPANT_EMAIL";
    private static final String PARTICIPANT_PASSWORD = properties[1] = "PARTICIPANT_PASSWORD";

    private static final String ADMIN_EMAIL = properties[2] = "ADMIN_EMAIL";
    private static final String ADMIN_PASSWORD = properties[3] = "ADMIN_PASSWORD";

    private static final String HOST = properties[4] = "HOST";

    private static final String AUTH_API = properties[5] = "AUTH_API";
    private static final String PROFILE_API = properties[6] = "PROFILE_API";
    private static final String CONSENT_API = properties[7] = "CONSENT_API";
    private static final String STUDY_CONSENT_API = properties[8] = "STUDY_CONSENT_API";
    private static final String SCHEDULE_PLANNING_API = properties[9] = "SCHEDULE_PLANNING_API";
    private static final String SCHEDULES_API = properties[10] = "SCHEDULES_API";
    private static final String ACTIVITIES_API = properties[11] = "ACTIVITIES_API";
    private static final String SURVEYS_API = properties[12] = "SURVEYS_API";
    private static final String SURVEY_RESPONSE_API = properties[13] = "SURVEY_RESPONSE_API";
    private static final String TRACKER_API = properties[14] = "TRACKER_API";
    private static final String HEALTH_DATA_API = properties[15] = "HEALTH_DATA_API";
    private static final String UPLOAD_API = properties[16] = "UPLOAD_API";

    private Configuration config;

    private Config(String configPath) {
        assert configPath != null;
        assert configPath.endsWith(".properties");

        // Set with config file.
        try {
            config = new PropertiesConfiguration(configPath);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        // Override any options with environment variables.
        overrideWithEnvironmentVariables(config);

        assertAllPropertiesPresent();
        assertHostAndEmailValid();
    }

    static Config valueOf(String configPath) {
        return new Config(configPath);
    }

    static Config valueOfDefault() {
        return new Config(DEFAULT_CONFIG);
    }

    String getParticipantEmail() { return config.getString(PARTICIPANT_EMAIL); }
    String getParticipantPassword() { return config.getString(PARTICIPANT_PASSWORD); }
    String getAdminEmail() { return config.getString(ADMIN_EMAIL); }
    String getAdminPassword() { return config.getString(ADMIN_PASSWORD); }
    String getHost() { return config.getString(HOST); }
    String getAuthApi() { return config.getString(AUTH_API); }
    String getProfileApi() { return config.getString(PROFILE_API); }
    String getConsentApi() { return config.getString(CONSENT_API); }
    String getStudyConsentApi() { return config.getString(STUDY_CONSENT_API); }
    String getSchedulePlanningApi() { return config.getString(SCHEDULE_PLANNING_API); }
    String getSchedulesApi() { return config.getString(SCHEDULES_API); }
    String getActivitiesApi() { return config.getString(ACTIVITIES_API); }
    String getSurveyApi() { return config.getString(SURVEYS_API); }
    String getSurveyResponsesApi() { return config.getString(SURVEY_RESPONSE_API); }
    String getTrackerApi() { return config.getString(TRACKER_API); }
    String getHealthDataApi() { return config.getString(HEALTH_DATA_API); }
    String getUploadApi() { return config.getString(UPLOAD_API); }

    private void overrideWithEnvironmentVariables(Configuration config) {
        String value;
        for (String property : properties) {
            value = System.getenv(property);
            if (value != null) {
                config.setProperty(property, value);
            }
        }
    }

    private void assertAllPropertiesPresent() {
        for (String property : properties) {
            assert config.getString(property) != null : property + " property is not present.";
        }
    }

    private void assertHostAndEmailValid() {
        if (!utils.isValidEmail(getParticipantEmail())) {
            throw new IllegalArgumentException(getParticipantEmail() + " is not a valid email address.");
        } else if (!utils.isValidEmail(getAdminEmail())) {
            throw new IllegalArgumentException(getAdminEmail() + " is not a valid email address.");
        } else if (!utils.isValidUrl(getHost()) || !getHost().endsWith("/")) {
            throw new IllegalArgumentException(getHost()
                    + " is not a valid URL. Needs to be of the form http://www.sagebase.org/");
        }
        // BridgePF is not currently pingable, so commenting out for now.
        // else if (!isConnectableUrl(getHost(), 1000)) {
        // throw new IllegalArgumentException("Could not connect to the URL: " + getHost());
        // }
    }

    @Override
    public String toString() {
        assert config != null;
        assertAllPropertiesPresent();

        StringBuilder builder = new StringBuilder();
        builder.append("Config[");
        for (String property : properties) {
            builder.append(property + "=" + config.getString(property) + ", ");
        }
        builder.delete(builder.length() - 2, builder.length()); // remove last ", "
        builder.append("]");
        return builder.toString();
    }
}