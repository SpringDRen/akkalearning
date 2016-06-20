package com.rlc.akka.actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by renlc on 2016/6/20.
 */
public class DeathWatchTest {

    /**
     * 子节点
     */
    static class ChildActor extends UntypedActor {
        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object o) throws Exception {
            log.info(o.getClass().toString());
            log.info(String.valueOf(o));
        }
    }

    /**
     * 监视节点
     */
    static class WatchActor extends UntypedActor {

        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        final ActorRef child = this.getContext().actorOf(Props.create(ChildActor.class), "child");
        {
            this.getContext().watch(child); // <-- the only call needed for registration
        }
        ActorRef lastSender = getContext().system().deadLetters();
//        ActorRef lastSender = null;

        @Override
        public void onReceive(Object message) {
            if (message instanceof String) {
                if ("kill".equals(String.valueOf(message))) {
                    log.info("stop child");
                    getContext().stop(child);
                    lastSender = getSender();
                }
            } else if (message instanceof Terminated) {
                final Terminated t = (Terminated) message;
                //输出详细信息
                log.info(String.valueOf(t));
                if (t.getActor() == child) {
                    //是否终止
                    System.out.println(t.getActor().isTerminated());
                    log.info("----child finish itself");
                    //child已经终止。。。报错，出现dead letters
                    lastSender.tell("finished", getSelf());
                }
            } else {
                unhandled(message);
            }
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("system");
        ActorRef watcher = system.actorOf(Props.create(WatchActor.class), "watcher");
        watcher.tell("kill", ActorRef.noSender());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println(system.deadLetters());
        system.terminate();
    }
}
