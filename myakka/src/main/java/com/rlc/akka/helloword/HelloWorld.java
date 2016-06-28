package com.rlc.akka.helloword;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.ActorRef;
import scala.collection.immutable.Iterable;

/**
 * HelloWorld管理节点，任务完成后停止对应的Actor
 */
public class HelloWorld extends UntypedActor {

    @Override
    public void preStart() {
        // create the greeter actor
        final ActorRef greeter = getContext().actorOf(Props.create(Greeter.class), "greeter");
        // tell it to perform the greeting
        greeter.tell(Greeter.Msg.GREET, getSelf());
    }

    @Override
    public void onReceive(Object msg) {
        if (msg == Greeter.Msg.DONE) {
            // when the greeter is done, stop this actor and with it the application
            System.out.println("have done");
            getContext().stop(getSelf());
            //停止自己后，会发布一个停止消息，需要手动接受，并且停止system
//            遍历所有的ActorRef
//            ActorSystem system = getContext().system();
//            Iterable<ActorRef> iterable = getContext().children();
//            scala.collection.Iterator<ActorRef> it = iterable.iterator();
//            while(it.hasNext()){
//                ActorRef ar = it.next();
//                System.out.println(ar.toString());
//                System.out.println(ar.isTerminated());
//            }
        } else
            unhandled(msg);
    }

    public static void main(String[] args) {
        //直接使用Akka的main方法，自动管理
        akka.Main.main(new String[] { HelloWorld.class.getName() });
    }
}
