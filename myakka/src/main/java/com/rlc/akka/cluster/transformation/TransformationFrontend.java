package com.rlc.akka.cluster.transformation;

import static com.rlc.akka.cluster.transformation.TransformationMessages.BACKEND_REGISTRATION;

import java.util.ArrayList;
import java.util.List;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.rlc.akka.cluster.transformation.TransformationMessages.JobFailed;
import com.rlc.akka.cluster.transformation.TransformationMessages.TransformationJob;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

//#frontend
public class TransformationFrontend extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    List<ActorRef> backends = new ArrayList<ActorRef>();
    int jobCounter = 0;

    @Override
    public void onReceive(Object message) {
        if ((message instanceof TransformationJob) && backends.isEmpty()) {
            TransformationJob job = (TransformationJob) message;
            getSender().tell(
                    new JobFailed("Service unavailable, try again later", job),
                    getSender());

        } else if (message instanceof TransformationJob) {
            TransformationJob job = (TransformationJob) message;
            jobCounter++;
            log.info(backends.size() + ":" + backends.get(jobCounter % backends.size()).path());
            backends.get(jobCounter % backends.size())
                    .forward(job, getContext());

        } else if (message.equals(BACKEND_REGISTRATION)) {
            getContext().watch(getSender());
            backends.add(getSender());

        } else if (message instanceof Terminated) {
            Terminated terminated = (Terminated) message;
            backends.remove(terminated.getActor());

        } else {
            unhandled(message);
        }
    }

}
//#frontend
