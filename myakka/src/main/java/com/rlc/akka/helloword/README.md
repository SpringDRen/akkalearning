# akka入门

开始使用akka，入门必修，hello world

## Getting Started

### Modules

[文档链接](http://doc.akka.io/docs/akka/2.4.7/intro/getting-started.html#Modules)  
stable modules  

*   `akka-actor` – Classic Actors, Typed Actors, IO Actor etc.
*   `akka-agent` – Agents, integrated with Scala STM
*   `akka-camel` – Apache Camel integration
*   `akka-cluster` – Cluster membership management, elastic routers.
*   `akka-osgi` – utilities for using Akka in OSGi containers
*   `akka-osgi-aries` – Aries blueprint for provisioning actor systems
*   `akka-remote` – Remote Actors
*   `akka-slf4j` – SLF4J Logger (event bus listener)
*   `akka-testkit` – Toolkit for testing Actor systems
  
还有一些非stable，不列举，请参考官方文档  

### Using Akka with Maven

[文档链接](http://doc.akka.io/docs/akka/2.4.7/intro/getting-started.html#Using_Akka_with_Maven)

dependencies  
```xml
<dependency>  
  <groupId>com.typesafe.akka</groupId>  
  <artifactId>akka-actor_2.11</artifactId>  
  <version>2.4.7</version>  
</dependency>  
```
  
releases versions  
```xml
<repository>  
  <id>typesafe</id>  
  <name>Typesafe Repository</name>  
  <url>http://repo.typesafe.com/typesafe/releases/</url>  
</repository>  
```
  
snapshot versions  
```xml
<repository>  
  <id>akka-snapshots</id>  
  <snapshots>  
    <enabled>true</enabled>  
  </snapshots>  
  <url>http://repo.akka.io/snapshots/</url>  
</repository>  
```
  

## Hello World

[文档链接](http://doc.akka.io/docs/akka/2.4.7/java/hello-world.html)  
[Greeter.java](https://github.com/SpringDRen/akkalearning/blob/master/myakka/src/main/java/com/rlc/akka/helloword/Greeter.java)：实际完成输出Hello world的Actor  
[HelloWorld.java](https://github.com/SpringDRen/akkalearning/blob/master/myakka/src/main/java/com/rlc/akka/helloword/HelloWorld.java)：HelloWord监管类，完成输出后停止对应的Actor  
启动方法：  
1、直接使用akka的main方法，akka自动管理system
[HelloWorld.main](https://github.com/SpringDRen/akkalearning/blob/master/myakka/src/main/java/com/rlc/akka/helloword/HelloWorld.java)  
2、手动启动、关闭system，使用Terminator监听HelloWorldActor，收到其停止的消息，则终止system
[Main2.main](https://github.com/SpringDRen/akkalearning/blob/master/myakka/src/main/java/com/rlc/akka/helloword/Main2.java)  

## 简单的system定时器&amp;消息不变性测试

代码示例：[ScheduleHelloTest.java](https://github.com/SpringDRen/akkalearning/blob/master/myakka/src/main/java/com/rlc/akka/helloword/ScheduleHelloTest.java)  

- system定时器：  
1、system().scheduler().scheduleOnce(),指定延迟时间后执行一次  
2、system().scheduler().schedule(延迟时间,时间间隔,执行方法,对应的dispatcher)，指定延迟后，隔一段时间执行一次  
3、停止Acotr不会停止定时器，需要停止system才可以  
4、定时器方法都会返回一个Cancellable对象，调用Cancellable.cancel()方法可以停止定时器  
- 消息不变性：  
1、消息传递之后，改变消息对象的引用，不会影响到已传递给Actor的消息
2、消息对象里的变量如果发生变化，Actor里的消息会受到影响  
3、如果消息为某个自定义的对象，要求其变量均为不可变，比如：使用list，为安全起见，请转换为不可变对象，Collections.unmodifiableList(list)