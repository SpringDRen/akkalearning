package com.rlc.akka.cluster.simple;

import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by renlc on 2016/6/21.
 */
public class SimpleClusterTest {

    public static void main(String[] args) {
        if (args.length == 0)
            startup(new String[] { "2553", "2551", "2552" });
        else
            startup(args);
    }

    public static void startup(String[] ports) {
        for (String port : ports) {
            // Override the configuration of the port
            Config config = ConfigFactory.parseString(
                    "akka.remote.netty.tcp.port=" + port).withFallback(
                    ConfigFactory.load("simple"));

            // Create an Akka system
            ActorSystem system = ActorSystem.create("ClusterSystem", config);

            // Create an actor that handles cluster domain events
            system.actorOf(Props.create(SimpleClusterListener.class),
                    "clusterListener");

            //不按照seed nodes配置的顺序启动，无法加入集群，必须等到配置的第一个seed nodes启动会才可以
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
