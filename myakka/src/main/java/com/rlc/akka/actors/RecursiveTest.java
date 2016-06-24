package com.rlc.akka.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by renlc on 2016/6/23.
 * 递归传递消息性能测试
 */
public class RecursiveTest {

    static class ChildMsg {

    }

    static class ChildActor extends UntypedActor {
        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                Thread.sleep(1000);
                log.info(String.valueOf(message));
                getSender().tell(new ParentMsg(), getSelf());
            } else {
                unhandled(message);
            }
        }
    }

    static class ParentMsg {

    }

    static class ParentActor extends UntypedActor {

        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        ActorRef child = getContext().actorOf(Props.create(ChildActor.class), "child");

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                child.tell("hello", getSelf());
            } else if (message instanceof ParentMsg) {
                getSender().tell("hello", getSelf());
                log.info("abcd");
            } else {
                unhandled(message);
            }
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("system");
        ActorRef ref = system.actorOf(Props.create(ParentActor.class), "parent");
        ref.tell("start", ActorRef.noSender());
    }

}
