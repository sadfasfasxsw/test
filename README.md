Agent 实现：
        在 JVM 启动时，JavaAgent 通过 premain 方法加载，并利用 ByteBuddy 拦截目标类的所有方法调用。每次方法调用时，在方法进入前记录方法签名，并将信息存储到线程安全集合中。
输出结果：
        在 JVM 退出时，注册的 Shutdown Hook 将捕获到的 API 信息转换为 JSON 格式并写入 api_calls.json 文件。
