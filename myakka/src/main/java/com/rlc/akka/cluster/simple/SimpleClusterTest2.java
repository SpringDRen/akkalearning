package com.rlc.akka.cluster.simple;

import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by renlc on 2016/6/21.
 */
public class SimpleClusterTest2 {


    public static void main(String[] args) {
        String port = "2666";
        if (args.length != 0)
            port = args[0];

        //解析str作为配置
        Config config = ConfigFactory.parseString("akka {\n" +
                "  actor {\n" +
                "    provider = \"akka.cluster.ClusterActorRefProvider\"\n" +
                "  }\n" +
                "  remote {\n" +
                "    log-remote-lifecycle-events = off\n" +
                "    netty.tcp {\n" +
                "      hostname = \"127.0.0.1\"\n" +
                "      port = " + port + "\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "akka.cluster.metrics.enabled=off\n" +
                "akka.extensions=[\"akka.cluster.metrics.ClusterMetricsExtension\"]");

        // Create an Akka system
        ActorSystem system = ActorSystem.create("ClusterSystem", config);

        // Create an actor that handles cluster domain events
        system.actorOf(Props.create(SimpleClusterListener.class),
                "clusterListener");

        //手动向已有集群发送join命令，不一定要发送到seed nodes
        //等价于jconsole MBean里的join命令
        //Cluster.get(system).join(new Address("akka.tcp", "ClusterSystem", "127.0.0.1", 2553));
    }


}
