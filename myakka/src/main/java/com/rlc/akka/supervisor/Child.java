package com.rlc.akka.supervisor;

import akka.actor.UntypedActor;

/**
 * Created by renlc on 2016/6/17.
 */
public class Child extends UntypedActor {
    int state = 0;

    public void onReceive(Object o) throws Exception {
        if (o instanceof Exception) {
            throw (Exception) o;
        } else if (o instanceof Integer) {
            state = (Integer) o;
        } else if (o.equals("get")) {
            getSender().tell(state, getSelf());
        } else {
            unhandled(o);
        }
    }
}
