package com.rlc.akka.cluster.transformation;

import static com.rlc.akka.cluster.transformation.TransformationMessages.BACKEND_REGISTRATION;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.rlc.akka.cluster.transformation.TransformationMessages.TransformationJob;
import com.rlc.akka.cluster.transformation.TransformationMessages.TransformationResult;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;
import akka.cluster.MemberStatus;

//#backend
public class TransformationBackend extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    Cluster cluster = Cluster.get(getContext().system());

    //subscribe to cluster changes, MemberUp
    @Override
    public void preStart() {
        cluster.subscribe(getSelf(), MemberUp.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof TransformationJob) {
            TransformationJob job = (TransformationJob) message;
            TransformationResult result = new TransformationResult(job.getText().toUpperCase());
            getSender().tell(result, getSelf());
            log.info(result.toString());
        } else if (message instanceof CurrentClusterState) {
            log.info("CurrentClusterState .....");
            CurrentClusterState state = (CurrentClusterState) message;
            for (Member member : state.getMembers()) {
                if (member.status().equals(MemberStatus.up())) {
                    register(member);
                }
            }

        } else if (message instanceof MemberUp) {
            MemberUp mUp = (MemberUp) message;
            Member member = mUp.member();
            log.info("MemberUp ....." + member.address() + " " + member.roles());
            register(member);
        } else {
            unhandled(message);
        }
    }

    void register(Member member) {
        System.out.println(member.address());
        if (member.hasRole("frontend"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    BACKEND_REGISTRATION, getSelf());
    }
}
//#backend
