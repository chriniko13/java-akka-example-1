package com.chriniko.examples_scala.second.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import com.chriniko.examples.second.actor.AkkademyDb;
import com.chriniko.examples.second.message.SetRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AkkademyDbTest {

    private ActorSystem actorSystem = ActorSystem.create();

    @Test
    public void it_should_place_key_value_from_SetRequest_message_into_map() {

        //given...
        TestActorRef<AkkademyDb> actorRef = TestActorRef.create(actorSystem, Props.create(AkkademyDb.class));


        //when...
        actorRef.tell(new SetRequest("key", "value"), ActorRef.noSender());


        //then...
        AkkademyDb akkademyDb = actorRef.underlyingActor();
        assertEquals(akkademyDb.getDb().get("key"), "value");

    }
}