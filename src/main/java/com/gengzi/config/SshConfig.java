package com.gengzi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SshConfig {

    @Value("${ssh.host}")
    private String host;

    @Value("${ssh.port}")
    private int port;

    @Value("${ssh.username}")
    private String username;

    @Value("${ssh.passwd}")
    private String passwd;


    @Value("${ssh.privatekeypath}")
    private String privatekeypath;

    public String getPrivatekeypath() {
        return privatekeypath;
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswd() {
        return passwd;
    }
}
