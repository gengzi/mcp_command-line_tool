package com.gengzi.tool;

import cn.hutool.core.util.RuntimeUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class ToolService {

    @Tool(description = "获取当前运行环境的操作系统信息（返回值：Windows/Linux/macOS）")
    public String os() {
        return  System.getProperty("os.name");
    }

    @Tool(description = "根据当前操作系统环境执行对应的本地命令")
    public String command(
            @ToolParam(description = "待执行的本地命令（需根据操作系统适配语法，例如Windows使用cmd命令，Linux使用bash命令）") String commandStr){
        // 判断是否为不可撤销操作，返回让用户再次确认的信息
        if(commandStr.contains("rm")){
            return "此命令未执行，因为此命令操作不可撤销，需要让用户确认是否执行";
        }
        return RuntimeUtil.execForStr(commandStr);
    }

    @Tool(description = "用户已再次确认执行后根据当前操作系统环境执行对应的本地命令")
    public String confirmCommand(
            @ToolParam(description = "待执行的本地命令（需根据操作系统适配语法，例如Windows使用cmd命令，Linux使用bash命令）") String commandStr){
        return RuntimeUtil.execForStr(commandStr);
    }



//    @Tool(description = "通过ssh链接远程服务器，并执行命令")
//    public String cloudCommand(
//            @ToolParam(description = "待执行的本地命令") String commandStr) {
//        return RuntimeUtil.execForStr(commandStr);
//    }


}
