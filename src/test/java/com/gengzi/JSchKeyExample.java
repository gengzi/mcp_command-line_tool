package com.gengzi;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSchKeyExample {
    public static void main(String[] args) {
        JSch jsch = new JSch();
        String privateKeyContent = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
                                   "b3BlbnNzaC1rZXktdjEAAAAACmFlczI1Ni1jdHIAAAAGYmNyeXB0AAAAGAAAABDCdlbvrv\n" +
                                   "BFX2LVDcoH04ByAAAAGAAAAAEAAAAzAAAAC3NzaC1lZDI1NTE5AAAAIFPRyMryyR92LU4e\n" +
                                   "3t5lc+wv6+f+ssPfj2fQ9TzVBQyGAAAAoPtOGKfPm6iyL4jvDqSfBnUYnJgwsLbM6v9+r9\n" +
                                   "C5FQl197rO9nPx6mYN4lg5thcjW3DOhRu8iGoa1tYBymsEmxHA2cNQt6uAdsDpAEAV2amw\n" +
                                   "O02oHKG67eYs2TI2VALHh8zYtbVSOx2ApUMfHuoKtgeF93huJ/HA3jjxHNO538wIvqyrAu\n" +
                                   "NynwGONXxLu7oQrjyir2cB4UwRjN86sJDqQ7Y=\n" +
                                   "-----END OPENSSH PRIVATE KEY-----"; // 完整私钥字符串
        String passphrase = ""; // 密码短语（如有）
        
        try {
            // 使用密钥登录

            // 添加私钥
            // 读取私钥文件内容为字符串（确保是 PEM 格式）
            jsch.addIdentity("/Users/gengshuaijia/.ssh/id_ed25519");
//            String privateKeyContent = new String(Files.readAllBytes(Paths.get(sshConfig.getPrivatekeypath())));
//            // 将私钥内容直接传入（适用于内存中已有私钥字符串的场景）
//            jsch.addIdentity("id_rsa", privateKeyContent.getBytes(), null, null);

            // 后续会话连接逻辑...
        //ssh 142128133dc08216fe81225ae78988d96019b57e-cvcwkd@cvcwkd.ssh.cloudstudio.work
            Session session = jsch.getSession("142128133dc08216fe81225ae78988d96019b57e-cvcwkd", "cvcwkd.ssh.cloudstudio.work", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            session.connect();
            String echoHello = executeCommand(session, "ll");
            System.out.printf("echoHello: %s\n", echoHello);


        } catch (JSchException e) {
            e.printStackTrace();
        }
    }


    public static String executeCommand(Session session,  String command) {
        StringBuilder result = new StringBuilder();
        try {
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            channel.disconnect();
            session.disconnect();
        } catch (JSchException | java.io.IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}