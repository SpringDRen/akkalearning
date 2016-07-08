#A Simple Cluster Example

##相关文件
  
配置文件：[/src/main/resources/simple.conf](https://github.com/SpringDRen/akkalearning/blob/master/myakka/src/main/resources/simple.conf)  
  
监听集群成员状态的Actor：[SimpleClusterListener.java](https://github.com/SpringDRen/akkalearning/blob/master/myakka/src/main/java/com/rlc/akka/cluster/simple/SimpleClusterListener.java)  
监听成员状态，并打印相关信息  
  
测试类：[SimpleClusterTest.java](https://github.com/SpringDRen/akkalearning/blob/master/myakka/src/main/java/com/rlc/akka/cluster/simple/SimpleClusterTest.java)  
启动集群的主类  
  
辅助测试类：[SimpleClusterTest2.java](https://github.com/SpringDRen/akkalearning/blob/master/myakka/src/main/java/com/rlc/akka/cluster/simple/SimpleClusterTest2.java)  
自己写的测试类，解析字符串作为配置，手动加入集群  


##测试结果

###1、启动顺序测试
分别启动三个节点：  
`java SimpleClusterListener 2553`  
`java SimpleClusterListener 2552`  
`java SimpleClusterListener 2551`  
不按照seed nodes的配置启动，必须等到seed nodes list里配置的第一个2551节点起来，先启动的两个节点才能加入集群  

###2、启动之后关闭seed nodes
依然分别启动3个节点。  
使用jconsole，打开启动的2551跟2552节点，使用akka提供的MBean，向`akka.tcp://ClusterSystem@127.0.0.1:2553`发送leave命令，移除集群里2551、2552节点。或者是直接关闭对应的jvm。  
然后启动2666节点`java SimpleClusterTest2`，向2553节点发送join命令，加入集群成功。  
启动成功后，移除两个配置的seed nodes，其他节点依然可以向现有集群中的其他节点发送join命令加入集群。  
  
###3、测试不配置seed nodes创建集群
注释掉SimpleClusterTest2.java里的46行代码`Cluster.get(system).join(new Address("akka.tcp", "ClusterSystem", "127.0.0.1", 2553))；`  
启动两个节点：  
`java SimpleClusterTest2 2666`  
`java SimpleClusterTest2 2667`  
启动之后输出  
>No seed-nodes configured, manual cluster join required
  
使用jconsole，打开2666节点，使用MBean里的join命令，将自己加入集群`akka.tcp://ClusterSystem@127.0.0.1:2666`。  
方法调用成功，输出：  
>Node [akka.tcp://ClusterSystem@127.0.0.1:2666] is JOINING, roles []
Leader is moving node [akka.tcp://ClusterSystem@127.0.0.1:2666] to [Up]
Member is Up: Member(address = akka.tcp://ClusterSystem@127.0.0.1:2666, status = Up)
  
再打开2667端口，使用join命令加入2666节点`akka.tcp://ClusterSystem@127.0.0.1:2666`。加入集群成功。  
  
