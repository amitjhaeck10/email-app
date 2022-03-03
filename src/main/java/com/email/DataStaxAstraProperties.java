package com.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;


@ConfigurationProperties(prefix = "")
public class DataStaxAstraProperties {

    private File secureConnectBundle;

    public File getSecureConnectBundle() {
        return secureConnectBundle;
    }

    public void setSecureConnectBundle(File getSecureConnectBundle) {
        this.secureConnectBundle = secureConnectBundle;
    }
}
