#  JAVA akka文档示例

官方文档：[http://doc.akka.io/docs/akka/2.4.7/java.html](http://doc.akka.io/docs/akka/2.4.7/java.html "akka官网文档2.4.7 JAVA")


----------
## Actors ##
1. 定义一个Actor  
	> 继承akka.actor.UntypedActor，实现onReceive方法，方法入参为message   
    
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

2. Props
	> actorof的入参，用来构造不同的Actor  
	> Props.create(...)  
	> ActorSystem will create top-level actors

		ActorSystem system = ActorSystem.create("MySystem");
		ActorRef myActor = system.actorOf(Props.create(MyUntypedActor.class), "myactor");

	>ActorContext will create a child actor

		class A extends UntypedActor {
		  final ActorRef child =
		      getContext().actorOf(Props.create(MyUntypedActor.class), "myChild");
		  // plus some behavior ...
		}

	>actorof有个可选入参，Actor的名字，不能重复，用来区分不同的Actor，记录日志信息。不能为空格、不能是$开头，可以使用URL  encoded characters  
	>Actor创建之后会自动异步启动

3. Dependency Injection

	>依赖注入框架的例子没看明白。。。。  
	>与spring集成，依赖注入???暂时未研究

4. The Inbox
	
	>System层级，可以发送消息，watch某个Actor的生命周期 

5. UntypedActor API
	
	>- onReceive(Object message):定义Actor的行为，处理收到的信息，如果不处理此消息，请调用unhandled(message)返回给System
	>- getSelf():返回当前Actor的引用
	>- getSender():返回最后收到的消息的发送者
	>- supervisorStrategy: 子Actor监管策略
	>- getContext(): 返回上下文
	>- preStart(): 启动之前的操作
	>- preRestart(Throwable reason, scala.Option<Object> message): 重启之前的一些操作
	>- postRestart():重启
	>- postStop():停止

6. 生命周期

	> 具体细节参考官方文档  
	> 
	> - Lifecycle Monitoring aka DeathWatch
	> - Start Hook
	> - Restart Hooks
	> - Stop Hook  
	
7. Identifying Actors via Actor Selection
	
	> - 根据Actor的路径查找Actor，可以为绝对路径或者相对路径，或者是远程路径，可以使用通配符*  
	> - 一般情况都是用ActorRef即可，只有在sending messages using the At-Least-Once Delivery facility 或者是 initiating first contact with a remote system 使用此种方式
	> - 路径按java.net.URI解析，使用/做分割符。如果以/开头，则表示绝对路径，从root开始查询，否则从当前Actor开始查询。..表示向上一层
	
		// will look up this absolute path
		getContext().actorSelection("/user/serviceA/actor");
		// will look up sibling beneath same supervisor
		getContext().actorSelection("../joe");
		// will look all children to serviceB with names starting with worker
		getContext().actorSelection("/user/serviceB/worker*");
		//Remote actor addresses 
		getContext().actorSelection("akka.tcp://app@otherhost:1234/user/serviceB");

8. Messages and immutability

	>messages可以是任何Object，但是必须为不可变对象。Akka不能强制实现此事，所以要遵守约定。  

9. 999
 

 
