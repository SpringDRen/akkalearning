package com.rlc.akka.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;


/**
 * Created by renlc on 2016/6/22.
 */
public class DeadLoopActorTest {


    static class DeadLoopAcotr extends UntypedActor {

        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        public DeadLoopAcotr() {
            super();
            //getContext().setReceiveTimeout(Duration.create("1 second"));
        }

        @Override
        public void onReceive(Object message) throws Exception {
            log.info("DeadLoopAcotr receive message:" + String.valueOf(message));
            int i = 0;
            //如果一个Actor执行一个完不成的死循环，那么后续的消息永远在队列中，无法处理到
            while (true) {
                i++;
                if (i > 10000) {
                    i = 0;
                }
            }
        }
    }

    static class Wathcer extends UntypedActor {
        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        ActorRef dead = getContext().actorOf(Props.create(DeadLoopAcotr.class), "Dead");

        @Override
        public void onReceive(Object message) throws Exception {
            log.info(String.valueOf(message));
            if(message instanceof String){
                if(String.valueOf(message).startsWith("loop")){
                    dead.tell(message, getSender());
                }
            }
        }
    }

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("dead");
        ActorRef ref2 = system.actorOf(Props.create(Wathcer.class), "Watch");
        ref2.tell("loop1", ActorRef.noSender());
        System.out.println(222);
        ref2.tell("loop2", ActorRef.noSender());
        System.out.println(333);

    }
}

