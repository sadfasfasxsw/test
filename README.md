api_calls.json为结果
首先，因为我们要输出API信息，所以我不用改源码，我想用一种注入的方式。我刚开始想到的是Spring AOP，但是由于项目里有任意类，所以我想到用JavaAgent。因为它可以在JVM启动时附加，并不需要修改业务代码，并且它是动态控制采集策略。

然后，在做字节码增强的时候，我一开始考虑过ASM，但是它太麻烦，书写效率不高。所以最终选择ByteBuddy，因为它不用手写字节码，对ASM进行了封装，并且项目推荐它与JavaAgent结合。

接着，当Agent开发完成后，我需要把它打包成一个独立的JAR文件，能直接用-javaagent方式加载。我用的是Maven Shade Plugin，它的好处是能把所有依赖都打进去，生成一个可直接运行的Fat JAR。

最后，在实现API监控时，我尝试拦截所有包中以org.javaweb开头的方法调用，结果记录了大量无关的API信息。为了提高分析效率，我考虑过只拦截Controller层的方法，但为了全面捕获目标服务的API调用信息，我仍拦截所有包。

整个过程中，我用 Postman 构造了 SQL 注入、命令执行等 Payload，手动触发 API，验证 JavaAgent 是否正确记录了调用信息。


遇到的问题：一开始直接hook了所有public方法，结果Spring里有的拦截不到，所以针对@RequestMapping注解的方法做了增强。

