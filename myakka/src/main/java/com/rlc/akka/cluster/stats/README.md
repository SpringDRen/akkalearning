##1. 使用Group Routers
配置文件：stats1.conf  
启动StatsSampleMain.main，创建相应的StatsWorker、StatsService，构成集群。  
配置文件配置了StatsWorker的路由，StatsService直接使用路由给StatsWorker发送消息。  
启动StatsSampleClientMain.main，创建StatsSampleClient。构造方法初始化servicePath，并初始化任务。每2s向自己发送一个"tick"的消息触发任务。  
StatsSampleClient订阅集群消息，当有角色为compute的节点加入时，将其加入自己的节点地址集合里。每次收到任务从现有的节点集合随机找一个节点，然后发送任务。  
StatsService收到任务后，将任务拆分，封装，使用workerRouter将任务和StatsAggregator的引用发送到StatsWorker，StatsWorker处理任务，将结果返回给StatsAggregator进行合并。StatsAggregator最后将结果返回给用户。  

##2. 使用Pool Router
例子中使用Cluster Singleton  
配置文件：stats2.conf  
分别启动三个节点：  
`java StatsSampleOneMasterMain 2551`  
`java StatsSampleOneMasterMain 2552`  
`java StatsSampleOneMasterMain 2553`  
每个系统都分别启动一个ClusterSingletonManager。2551节点的`ClusterSingletonManager`启动成功后，状态变为`Oldest`，其余两个基点启动成功后变为`Younger`。  
启动任务节点：  
`java StatsSampleOneMasterClientMain 0`  
随机抽取2551、2552、2553节点，向3个节点发送消息，消息均会传递到状态为`Oldest`的节点，即2551节点。  
关闭2551节点，2551节点从集群移除，2552节点状态变为`Oldest`，开始接受处理任务。  


