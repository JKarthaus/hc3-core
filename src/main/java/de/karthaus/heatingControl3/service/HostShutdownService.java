package de.karthaus.heatingControl3.service;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import io.micronaut.context.annotation.Value;

@Singleton
public class HostShutdownService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JSch jschSSHChannel = new JSch();

    @Value("${hc3.ssh.username}")
    protected String userName;

    @Value("${hc3.ssh.password}")
    protected String password;

    @Value("${hc3.ssh.host}")
    protected String host;


    public void startShutdownProcess() throws JSchException, IOException {

        logger.info("Login to Host {} with user {} to send shutdown command...", host, userName);
        StringBuilder outputBuffer = new StringBuilder();
        Session sesConnection = jschSSHChannel.getSession(userName, host);
        sesConnection.setPassword(password);

        // UNCOMMENT THIS FOR TESTING PURPOSES, BUT DO NOT USE IN PRODUCTION
        sesConnection.setConfig("StrictHostKeyChecking", "no");
        sesConnection.connect(10000);

        Channel channel = sesConnection.openChannel("exec");
        ((ChannelExec) channel).setCommand("sudo poweroff");
        InputStream commandOutput = channel.getInputStream();
        channel.connect();
        int readByte = commandOutput.read();

        while (readByte != 0xffffffff) {
            outputBuffer.append((char) readByte);
            readByte = commandOutput.read();
        }
        logger.info("poweroff command send Result ->{}", outputBuffer.toString());
        channel.disconnect();
        sesConnection.disconnect();
        logger.info("Disconnected from Host {}", host);
    }

}
