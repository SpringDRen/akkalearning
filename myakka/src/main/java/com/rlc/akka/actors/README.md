# Actors #
[文档链接](http://doc.akka.io/docs/akka/2.4.7/java/untyped-actors.html)

本文档中的示例均是直接使用main方法运行即可。

##1. 定义一个Actor
示例：[MyUntypedActor.java](https://github.com/SpringDRen/akkalearning/blob/master/myakka/src/main/java/com/rlc/akka/actors/MyUntypedActor.java)
继承akka.actor.UntypedActor，实现onReceive方法，方法入参为message

```java
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MyUntypedActor extends UntypedActor {
  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  public void onReceive(Object message) throws Exception {
    if (message instanceof String) {
      log.info("Received String message: {}", message);
      getSender().tell(message, getSelf());
    } else
      unhandled(message);
  }
}
```

##2. Props
示例：[PropsTest.java](https://github.com/SpringDRen/akkalearning/tree/master/myakka/src/main/java/com/rlc/akka/actors/PropsTest.java)
actorof的入参，用来构造不同的Actor  
Props.create(...)  
ActorSystem will create top-level actors

	ActorSystem system = ActorSystem.create("MySystem");
	ActorRef myActor = system.actorOf(Props.create(MyUntypedActor.class), "myactor");

ActorContext will create a child actor

	class A extends UntypedActor {
	  final ActorRef child =
	      getContext().actorOf(Props.create(MyUntypedActor.class), "myChild");
	  // plus some behavior ...
	}

actorof有个可选入参，Actor的名字，不能重复，用来区分不同的Actor，记录日志信息。不能为空格、不能是$开头，可以使用URL  encoded characters  
Actor创建之后会自动异步启动

##3. Dependency Injection

依赖注入框架的例子没看明白。。。。  
与spring集成，依赖注入???暂时未研究

##4. The Inbox
示例：[InboxTest.java](https://github.com/SpringDRen/akkalearning/tree/master/myakka/src/main/java/com/rlc/akka/actors/InboxTest.java)
	
System层级，可以发送消息，watch某个Actor的生命周期 

##5. UntypedActor API

- onReceive(Object message):定义Actor的行为，处理收到的信息，如果不处理此消息，请调用unhandled(message)返回给System
- getSelf():返回当前Actor的引用
- getSender():返回最后收到的消息的发送者
- supervisorStrategy: 子Actor监管策略
- getContext(): 返回上下文
- preStart(): 启动之前的操作
- preRestart(...): 重启之前的一些操作
- postRestart():重启
- postStop():停止

##6. 生命周期

具体细节参考官方文档  
 
- Lifecycle Monitoring aka DeathWatch  
示例[DeathWatchTest.java](https://github.com/SpringDRen/akkalearning/tree/master/myakka/src/main/java/com/rlc/akka/actors/DeathWatchTest.java)
- Start Hook
- Restart Hooks
- Stop Hook  
	
##7. Identifying Actors via Actor Selection
示例：[ActorSelectionsTest.java](https://github.com/SpringDRen/akkalearning/tree/master/myakka/src/main/java/com/rlc/akka/actors/ActorSelectionsTest.java)
	
- 根据Actor的路径查找Actor，可以为绝对路径或者相对路径，或者是远程路径，可以使用通配符*  
- 一般情况都是用ActorRef即可，只有在sending messages using the At-Least-Once Delivery facility 或者是 initiating first contact with a remote system 使用此种方式
- 路径按java.net.URI解析，使用/做分割符。如果以/开头，则表示绝对路径，从root开始查询，否则从当前Actor开始查询。..表示向上一层

```java
// will look up this absolute path
getContext().actorSelection("/user/serviceA/actor");
// will look up sibling beneath same supervisor
getContext().actorSelection("../joe");
// will look all children to serviceB with names starting with worker
getContext().actorSelection("/user/serviceB/worker*");
//Remote actor addresses
getContext().actorSelection("akka.tcp://app@otherhost:1234/user/serviceB");
```

##8. Messages and immutability

messages可以是任何Object，但是必须为不可变对象。Akka不能强制实现此事，所以要遵守约定。  

##9. Send messages  
示例[SendMessageTest.java](https://github.com/SpringDRen/akkalearning/tree/master/myakka/src/main/java/com/rlc/akka/actors/SendMessageTest.java)

- Tell: Fire-forget 异步发送一个消息并立即返回。推荐此种方式，不会阻塞地等待消息，拥有最好的并发性和可扩展性  
- Ask: Send-And-Receive-Future  
akka.pattern.Patterns.ask、akka.pattern.Patterns.pipe配合使用。需要Actor有返回值：getSender().tell(reply, getSelf())。详情参见Futures章节。  
- Forward message：直接转发消息，target.forward(result, getContext());路由、负载均衡等情况会用到  

##10. Receive messages
onReceive方法接受消息

##11. Reply to messages
回复消息，使用getSender().tell(replyMsg, getSelf())  
可以传递ActorRef

##12. Receive timeout
设置接受消息超时，最小单位1millisecond：getContext().setReceiveTimeout(Duration.create("30 seconds"))  
关闭超时：getContext().setReceiveTimeout(Duration.Undefined())

##13. Stopping actors
待研究...

##14. HotSwap
待研究...

##15. Stash
待研究...


##16. Killing an Actor
向对应的Acotr发送一条Kill消息，Actor会抛出ActorKilledException异常，然后挂起，等待监管者处理。

	victim.tell(akka.actor.Kill.getInstance(), ActorRef.noSender());

##17.Actors and exceptions
- 消息会怎样  
如果消息处理过程中（即从邮箱中取出并交给receive后）发生了异常，这个消息将被丢失。必须明白它不会被放回到邮箱中。所以如果你希望重试对消息的处理，你需要自己抓住异常然后在异常处理流程中重试. 请确保你限制重试的次数，因为你不会希望系统产生活锁 (从而消耗大量CPU而于事无补)。

- 邮箱会怎样  
如果消息处理过程中发生异常，邮箱没有任何变化。如果actor被重启，邮箱会被保留。邮箱中的所有消息不会丢失。

- actor会怎样  
如果抛出了异常，actor实例将被丢弃而生成一个新的实例。这个新的实例会被该actor的引用所引用（所以这个过程对开发人员来说是不可见的）。注意这意味着如果你不在preRestart 回调中进行保存，并在postRestart回调中恢复，那么失败的actor实例的当前状态会被丢失。

##18.Initialization patterns
待研究...

 
