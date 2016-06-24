package com.rlc.akka.routing;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by renlc on 2016/6/23.
 */
public class SimpleRouterTest {

    static class ParentActor extends UntypedActor {
        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        private Router router;

        public ParentActor() {
            super();
            List<Routee> routees = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                ActorRef r = getContext().actorOf(Props.create(ChildActor.class));
                getContext().watch(r);
                routees.add(new ActorRefRoutee(r));
            }
            router = new Router(new RoundRobinRoutingLogic(), routees);
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                String msg = String.valueOf(message);
                if ("showchild".equals(msg)) {
                    Iterable<ActorRef> it = getContext().getChildren();
                    System.out.println(router.productArity());
                    for (ActorRef ref : it) {
                        System.out.println(ref + " : " + ref.isTerminated());
                    }
                } else if ("hello".equals(msg)) {
                    router.route(message, getSender());
                }
            } else if (message instanceof Terminated) {
                router = router.removeRoutee(((Terminated) message).actor());
                ActorRef r = getContext().actorOf(Props.create(ChildActor.class));
                getContext().watch(r);
                router = router.addRoutee(new ActorRefRoutee(r));
            } else {
                unhandled(message);
            }

        }
    }

    static class ChildActor extends UntypedActor {
        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                log.info(String.valueOf(message));
                if(String.valueOf(getSelf()).indexOf("$a") > 0){
                    getContext().stop(getSelf());
                }
            } else {
                unhandled(message);
            }
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("system");
        ActorRef ref = system.actorOf(Props.create(ParentActor.class), "parent");
        for (int i = 0; i < 20; i++) {
            ref.tell("hello", ActorRef.noSender());
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ref.tell("showchild",ActorRef.noSender());
    }
}
