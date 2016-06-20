package com.rlc.akka.actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by renlc on 2016/6/20.
 */
public class InboxTest {

    static class HelloWorldActor extends UntypedActor {

        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                log.info(String.valueOf(message));
                if ("hello".equals(String.valueOf(message))) {
                    getSelf().tell("world", getSelf());
                } else {
                    getContext().stop(getSelf());
                }
            } else {
                unhandled(message);
            }
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("system");
        ActorRef target = system.actorOf(Props.create(HelloWorldActor.class), "hello");
        //Inbox 在System层级，可以发送消息，接受系统的一些消息
        final Inbox inbox = Inbox.create(system);
        //给某个Actor发送消息
        inbox.send(target, "hello");
        //watch对应Actor的生命周期
        inbox.watch(target);
        try {
            System.out.println(inbox.receive(Duration.create(2, TimeUnit.SECONDS)) instanceof Terminated);
        } catch (java.util.concurrent.TimeoutException e) {
            // timeout
            e.printStackTrace();
        }
        //停止系统
        system.terminate();
    }
}
