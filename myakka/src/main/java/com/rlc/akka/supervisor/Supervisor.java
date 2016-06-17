package com.rlc.akka.supervisor;

import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

import static akka.actor.SupervisorStrategy.*;

/**
 * Created by renlc on 2016/6/17.
 */
public class Supervisor extends UntypedActor {

    /**
     * 监管策略
     */
    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"), throwable -> {
                if (throwable instanceof ArithmeticException) {
                    //恢复子Actor，保持内部状态
                    return resume();
                } else if (throwable instanceof NullPointerException) {
                    //重启子Actor，清除内部状态
                    return restart();
                } else if (throwable instanceof IllegalArgumentException) {
                    //停止子Actor
                    return stop();
                } else {
                    //向上抛出
                    return escalate();
                }
            });

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Props) {
            System.out.println("receive props....");
            getSender().tell(getContext().actorOf((Props) o), getSelf());
        } else {
            unhandled(o);
        }
    }
}
