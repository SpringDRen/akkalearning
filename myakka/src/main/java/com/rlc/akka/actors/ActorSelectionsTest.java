package com.rlc.akka.actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.Timeout;

import java.util.concurrent.TimeUnit;

/**
 * Created by renlc on 2016/6/20.
 */
public class ActorSelectionsTest {

    static class ActorOne extends UntypedActor {

        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object o) throws Exception {
            if (o instanceof String) {
                log.info("ActorOne recieve messages:" + String.valueOf(o));
                //可以使用相对路径
                getContext().actorSelection("../two").tell("hello two", getSelf());
            }
        }
    }

    static class ActorTwo extends UntypedActor {

        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object o) throws Exception {
            if (o instanceof String) {
                log.info("ActorTwo recieve messages:" + String.valueOf(o));
            }
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("system");
        system.actorOf(Props.create(ActorOne.class), "one");
        system.actorOf(Props.create(ActorTwo.class), "two");
        //[akka://system/user/one]
        system.actorSelection("user/one").tell("hello world", ActorRef.noSender());
        //[akka://system/two]
        system.actorSelection("two").tell("hello world", ActorRef.noSender());
        //通配符
        system.actorSelection("user/*").tell("hello every one", ActorRef.noSender());

        //
        system.actorOf(Props.create(ActorTwo.class), "another");
        //发送这个就挂了。。。。不懂new Identify(1)
        system.actorSelection("/user/another").tell(new Identify(1), ActorRef.noSender());
        //system.actorOf(Props.create(Follower.class, ActorRef.noSender()), "another2");
        //
        //system.terminate();
    }


    static class Follower extends UntypedActor {
        final String identifyId = "1";

        {
            ActorSelection selection =
                    getContext().actorSelection("/user/another");
            selection.tell(new Identify(identifyId), getSelf());
        }

        ActorRef another;

        final ActorRef probe;

        public Follower(ActorRef probe) {
            this.probe = probe;
        }

        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object message) {
            log.info("----" + String.valueOf(message));
            if (message instanceof ActorIdentity) {
                ActorIdentity identity = (ActorIdentity) message;
                if (identity.correlationId().equals(identifyId)) {
                    ActorRef ref = identity.getRef();
                    if (ref == null)
                        getContext().stop(getSelf());
                    else {
                        another = ref;
                        getContext().watch(another);
                        probe.tell(ref, getSelf());
                    }
                }
            } else if (message instanceof Terminated) {
                final Terminated t = (Terminated) message;
                if (t.getActor().equals(another)) {
                    getContext().stop(getSelf());
                }
            } else {
                unhandled(message);
            }
        }
    }

}
