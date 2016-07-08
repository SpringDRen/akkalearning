package com.rlc.akka.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by renlc on 2016/7/6.
 */
public class StopTest {

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("system");
        ActorRef parent = system.actorOf(Props.create(Parent.class), "parent");
        //启动两个任务
        parent.tell("child1", ActorRef.noSender());
        parent.tell("child2", ActorRef.noSender());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //10S后终止任务1
        parent.tell("stop_child1", ActorRef.noSender());
        //sopt某个Actor或者是system  terminate，都必须要等到Actor任务终止才可以
        //也就是说如果Actor执行的任务是死循环，是无法完成终止操作的

    }

    static class Parent extends UntypedActor {

        Map<String, ActorRef> map = new HashMap<>();

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                String str = String.valueOf(message);
                if (str.startsWith("stop")) {
                    System.out.println("停止应用" + str);
                    getContext().stop(map.get(str.split("_")[1]));
                    getContext().system().terminate();
                } else {
                    if (map.containsKey(str)) {
                        System.out.println("已经在运行。。。。");
                    } else {
                        System.out.println("开始新任务。。。。" + str);
                        ActorRef ref = getContext().actorOf(Props.create(Child.class));
                        ref.tell(str, getSelf());
                        map.put(str, ref);
                    }
                }
            } else {
                unhandled(message);
            }
        }
    }

    static class Child extends UntypedActor {

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                int count = 0;
                while (true) {
                    Thread.sleep(2000);
                    System.out.println("child...." + message);
                    count++;
                    if(count > 10){
                        break;
                    }
                }
            } else {
                unhandled(message);
            }
        }
    }

}
