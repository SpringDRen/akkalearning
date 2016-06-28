#Networking

网络及集群

##1. Cluster Specification
[文档链接](http://doc.akka.io/docs/akka/2.4.7/common/cluster.html)  
集群的一些概念。待研究...

###Seed Nodes
Seed Nodes是新节点加入集群的配置入口。当一个新节点启动时，会向所有的seed nodes发送消息，然后会向第一个回应的seed node发送join命令。  
seed nodes的配置对于集群的正常运行并没有任何影响，它只与新节点加入集群有关，它帮助新节点找到向集群发送join命令的接入点，新成员可以向集群里的任意当前有效的成员发送此命令，不仅仅是seed nodes。

##2. Cluster Usage
[文档链接](http://doc.akka.io/docs/akka/2.4.7/java/cluster-usage.html)  
###maven配置
```xml
<dependency>
  <groupId>com.typesafe.akka</groupId>
  <artifactId>akka-cluster_2.11</artifactId>
  <version>2.4.7</version>
</dependency>
```
###A Simple Cluster Example
一个简单的集群示例：[点击这里](https://github.com/SpringDRen/akkalearning/tree/master/myakka/src/main/java/com/rlc/akka/cluster/simple)
  
####Joining to Seed Nodes
你需要确定加入集群的方式，是手动或者自动配置初始连接点，即所谓的seed nodes。当一个新节点启动时，它会向所有的seed nodes发送消息，然后向最先响应的节点发送join命令。如果没有一个seed nodes响应（也许是还未启动），将重复此流程直至成功或者系统关闭。  
  

seed nodes可以以任意顺序启动，而且不需要所有的seed nodes都运行起来，但是当初始化一个集群时，`seed-nodes`配置列表中的第一个节点必须先启动，否则其他seed-nodes无法完成初始化，其他节点也不能加入到集群中来。第一个seed nodes特殊设计的原因是避免从一个空集群启动时会形成separated islands。最快的方式是在同一时间启动所有的seed nodes（顺序无所谓），否则需要等到配置的`seed-node-timeout`时间后，其他节点才能加入。  
  
超过2个seed nodes启动后，关闭第一个seed node不会有影响。当第一个seed node重启后，它会首先向现在集群中存活的seed nodes发送join命令。  
  
如果你没有配置seed nodes，你需要编程或者手动加入集群。  
  
手动加入集群参考文档[Command Line Management](http://doc.akka.io/docs/akka/2.4.7/java/cluster-usage.html#cluster-command-line-java)。程序中可以使用`Cluster.get(system).join`来实现。尝试加入集群失败，会自动根据配置的`retry-unsuccessful-join-after`时间间隔重新尝试。可以将属性值设为`off`来关闭这一特性。  
  
。。。。。。  
  
一个actor system只能加入集群一次。再次尝试加入集群将会被忽略。如果已经成功加入到某个集群，则必须重启才能加入另一个集群或者是重新加入同一个集群。重启之后可以使用相同的地址和端口，当它成为已经存在该成员的集群的一个新的实例时，尝试重新加入，那么已经存在的会被从集群中移除，然后才能重新加入。  
  
>集群中所有的ActorSystem的name必须相同。name是在ActorSystem启动的时候初始化的。  
  
。。。。