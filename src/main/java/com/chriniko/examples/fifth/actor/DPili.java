package com.chriniko.examples.fifth.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.chriniko.examples.fifth.message.ApprovalOfMeetingPoint;
import com.chriniko.examples.fifth.message.CandidateMeetingPoint;
import com.chriniko.examples.fifth.message.LetsGoForCoffee;
import com.chriniko.examples.fifth.message.WantCoffee;

public class DPili extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    @Override
    public void onReceive(Object message) {

        if (message instanceof WantCoffee) {

            WantCoffee wantCoffee = (WantCoffee) message;
            ActorRef target = wantCoffee.getTarget();

            log.info("actor with path: " + target.path() + ", asks you if you want coffee...");
            target.tell(new LetsGoForCoffee(), self());

        } else if (message instanceof CandidateMeetingPoint) {

            String meetingPlace = ((CandidateMeetingPoint) message).getPlace();

            log.info("actor with path: " + sender().path() + ", wants to meet you on: " + meetingPlace);
            sender().tell(new ApprovalOfMeetingPoint(meetingPlace), self());

        } else {
            unhandled(message);
        }

    }

    public static Props props() {
        return Props.create(DPili.class);
    }
}
