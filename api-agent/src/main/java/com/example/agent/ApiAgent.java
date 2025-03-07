package com.example.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ApiAgent {

    // 存储 API 调用信息，格式："类名#方法名"
    private static Set<String> apiCalls = ConcurrentHashMap.newKeySet();

    // Agent 的入口方法，JVM 启动时调用
    public static void premain(String agentArgs, Instrumentation inst) {
        new AgentBuilder.Default()
                .ignore(ElementMatchers.nameStartsWith("net.bytebuddy."))
                .type(ElementMatchers.nameStartsWith("org.javaweb"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.method(ElementMatchers.any())
                                .intercept(Advice.to(ApiAdvice.class))
                ).installOn(inst);


        // 注册 JVM 退出钩子，写入 API 调用信息到 JSON 文件
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writerWithDefaultPrettyPrinter().writeValue(new File("api_calls.json"), apiCalls);
                System.out.println("API 信息已写入 api_calls.json");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    // 记录 API 调用信息
    public static void recordApi(String info) {
        apiCalls.add(info);
    }

    // Advice 类，在方法执行前调用，记录方法签名
    public static class ApiAdvice {
        @Advice.OnMethodEnter
        public static void onEnter(@Advice.Origin String method) {
            recordApi(method);
        }
    }
}
