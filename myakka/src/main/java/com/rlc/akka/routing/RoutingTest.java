package com.rlc.akka.routing;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.RoundRobinGroup;
import akka.routing.RoundRobinPool;

import java.util.*;

/**
 * Created by renlc on 2016/6/23.
 */
public class RoutingTest {

    static class Worker extends UntypedActor {

        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                //log.info(String.valueOf(message));
                if (String.valueOf(getSelf()).indexOf("$a") > 0
                        || String.valueOf(getSelf()).indexOf("workerA") > 0) {
                    getContext().stop(getSelf());
                } else {
                    getSender().tell(String.valueOf(getSelf()), getSelf());
                }
            } else {
                unhandled(message);
            }
        }
    }

    static class Watcher extends UntypedActor {
        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        Map<String, Integer> map = new HashMap<>();

        static class Count {

        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                String actor = String.valueOf(message);
                if (map.containsKey(actor)) {
                    map.put(actor, map.get(actor) + 1);
                } else {
                    map.put(actor, 1);
                }
            } else if (message instanceof Count) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    log.info(key + ":" + map.get(key));
                }
            } else {
                unhandled(message);
            }
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("Router");
        ActorRef watch = system.actorOf(Props.create(Watcher.class), "watch");
        //RoundRobinPool方式
        ActorRef routerRef = system.actorOf(new RoundRobinPool(4).props(Props.create(Worker.class)), "worker");
        //RoundRobinGroup 方式
        system.actorOf(Props.create(Worker.class),"workerA");
        system.actorOf(Props.create(Worker.class),"workerB");
        system.actorOf(Props.create(Worker.class),"workerC");
        system.actorOf(Props.create(Worker.class),"workerD");
        List<String> paths = new ArrayList<>();
        paths.add("/user/workerA");
        paths.add("/user/workerB");
        paths.add("/user/workerC");
        paths.add("/user/workerD");
        ActorRef routerRef2 = system.actorOf(new RoundRobinGroup(paths).props(), "workers");

        for (int i = 0; i < 103; i++) {
            routerRef.tell("hello world", watch);
            routerRef2.tell("hello world", watch);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        watch.tell(new Watcher.Count(), ActorRef.noSender());
        system.terminate();
    }

}
