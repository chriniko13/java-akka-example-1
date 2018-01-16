package com.chriniko.examples.fifth.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.chriniko.examples.fifth.message.ApprovalOfMeetingPoint;
import com.chriniko.examples.fifth.message.CandidateMeetingPoint;
import com.chriniko.examples.fifth.message.LetsGoForCoffee;

public class NChristi extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    @Override
    public void onReceive(Object message) {

        if (message instanceof LetsGoForCoffee) {

            log.info("actor with path: " + sender().path() + ", want to go for coffee...");
            sender().tell(new CandidateMeetingPoint("bus-station"), self());

        } else if (message instanceof ApprovalOfMeetingPoint) {

            ApprovalOfMeetingPoint approvalOfMeetingPoint = (ApprovalOfMeetingPoint) message;
            log.info("actor with path: " + sender().path() + ", heading to place = " + approvalOfMeetingPoint.getPlace());

        } else {
            unhandled(message);
        }
    }

    public static Props props() {
        return Props.create(NChristi.class);
    }
}
