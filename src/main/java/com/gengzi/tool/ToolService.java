package com.gengzi.tool;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.JschUtil;
import com.gengzi.config.SshConfig;
import com.jcraft.jsch.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ToolService {

    @Autowired
    private SshConfig sshConfig;

    private final  static List<String> com = new ArrayList<String>();

    static {
        com.add("rm");
        com.add("format");
        com.add("del");
        com.add("dd");
        com.add("diskutil");
        com.add("mkfs");
        com.add("fdisk");
        com.add("clean");
        com.add("delete");
    }

    @Tool(description = "获取当前运行环境的操作系统信息（返回值：Windows/Linux/macOS）")
    public String os() {
        return  System.getProperty("os.name");
    }

    @Tool(description = "根据当前操作系统环境执行对应的本地命令")
    public String command(
            @ToolParam(description = "待执行的本地命令（需根据操作系统适配语法，例如Windows使用cmd命令，Linux使用bash命令）") String commandStr){
        if(StrUtil.isBlank(commandStr)){
            return "";
        }
        // 判断是否为不可撤销操作，返回让用户再次确认的信息
        for (String comStr : com) {
            if(StrSplitter.split(commandStr," ",0,true,true).stream().anyMatch(
                    val->{
                       return val.contains(comStr);
                    }
            )){
                return "此命令未执行，因为此命令操作不可撤销，需要让用户再次确认是否执行";
            }
        }
        return RuntimeUtil.execForStr(commandStr);
    }

    @Tool(description = "用户已再次确认执行后,根据当前操作系统环境执行对应的本地命令")
    public String confirmCommand(
            @ToolParam(description = "待执行的本地命令（需根据操作系统适配语法，例如Windows使用cmd命令，Linux使用bash命令）") String commandStr){
        // 判断是否为不可撤销操作，返回让用户再次确认的信息
        for (String comStr : com) {
            if(StrSplitter.split(commandStr," ",0,true,true).stream().anyMatch(
                    val->{
                        return val.contains(comStr);
                    }
            )){
                return "此命令不支持执行，需要用户手动操作执行";
            }
        }
        return RuntimeUtil.execForStr(commandStr);
    }



    @Tool(description = "远程服务器已链接，可以执行命令")
    public String cloudCommand(
            @ToolParam(description = "待执行的远程命令") String commandStr) throws JSchException, IOException {
        Session session = null;
        // 使用密钥登录
        JSch jsch = new JSch();
        if(StrUtil.isNotBlank(sshConfig.getPrivatekeypath()) ) {

            // 添加私钥
            // 读取私钥文件内容为字符串（确保是 PEM 格式）
            jsch.addIdentity(sshConfig.getPrivatekeypath());
//            String privateKeyContent = new String(Files.readAllBytes(Paths.get(sshConfig.getPrivatekeypath())));
//            // 将私钥内容直接传入（适用于内存中已有私钥字符串的场景）
//            jsch.addIdentity("id_rsa", privateKeyContent.getBytes(), null, null);
            session = jsch.getSession(sshConfig.getUsername(), sshConfig.getHost(), sshConfig.getPort());
        }
//        }else{
//            //新建会话
//            session = JschUtil.getSession(
//                    sshConfig.getHost(),
//                    sshConfig.getPort(),
//                    sshConfig.getUsername(),
//                    sshConfig.getPasswd());
//        }
        // 避免第一次连接服务器时需要确认公钥的问题
        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session.connect();
        return executeCommand(session, commandStr);
    }


    public String executeCommand(Session session,  String command) {
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
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }


}
