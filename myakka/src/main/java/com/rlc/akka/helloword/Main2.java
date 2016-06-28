package com.rlc.akka.helloword;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;


public class Main2 {

    public static void main(String[] args) {
        //启动Actor system
        ActorSystem system = ActorSystem.create("Hello");
        //启动helloword的Actor，返回其引用
        ActorRef a = system.actorOf(Props.create(HelloWorld.class), "helloWorld");
        //启动监听停止任务的Acotr
        system.actorOf(Props.create(Terminator.class, a), "terminator");
    }

    public static class Terminator extends UntypedActor {

        private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
        private final ActorRef ref;

        public Terminator(ActorRef ref) {
            this.ref = ref;
            //watch 对应的Actor
            getContext().watch(ref);
        }

        @Override
        public void onReceive(Object msg) {
            if (msg instanceof Terminated) {
                //接受到对应Actor的停止消息后，终止system
                Terminated t = Terminated.class.cast(msg);
                System.out.println(t.toString());
                log.info("{} has terminated, shutting down system", ref.path());
                getContext().system().terminate();
            } else {
                unhandled(msg);
            }
        }

    }
}