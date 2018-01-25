package com.chriniko.examples.third.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.testkit.TestKit;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.*;

import static scala.compat.java8.FutureConverters.toJava;

public class JavaPongActorTest {

    private static ActorSystem system;
    private static ActorRef actorRef;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();

        actorRef = system.actorOf(JavaPongActor.props());
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system, Duration.create("10 seconds"), true);
        system = null;
    }

    @Test
    public void should_reply_to_ping_message_with_pong_message() throws InterruptedException, ExecutionException, TimeoutException {

        //when...
        Future<Object> resultWhenWeSentPingMessage_Scala = Patterns.ask(actorRef, "ping", 5000);

        //then...
        CompletionStage<Object> resultWhenWeSentPingMessage_Java = toJava(resultWhenWeSentPingMessage_Scala);

        CompletableFuture<Object> cs = (CompletableFuture<Object>) resultWhenWeSentPingMessage_Java;

        Assert.assertEquals("pong", cs.get(5000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void should_reply_to_unknown_message_with_failure() {

        //when...
        Future<Object> resultWhenWeSentUnknownMessage_Scala = Patterns.ask(actorRef, "___wrong___", 5000);

        //then...
        CompletionStage<Object> resultWhenWeSentUnknownMessage_Java = toJava(resultWhenWeSentUnknownMessage_Scala);

        CompletableFuture<Object> cs = (CompletableFuture<Object>) resultWhenWeSentUnknownMessage_Java;


        try {

            cs.get(5000, TimeUnit.MILLISECONDS);

        } catch (Exception error) {

            Assert.assertThat(error, CoreMatchers.is(CoreMatchers.instanceOf(ExecutionException.class)));

            Assert.assertThat(error.getCause(), CoreMatchers.is(CoreMatchers.instanceOf(IllegalStateException.class)));

            Assert.assertThat(error.getMessage(), CoreMatchers.is("java.lang.IllegalStateException: unknown message"));
        }

    }

    @Test
    public void print_to_console() throws InterruptedException {

        //when-then...
        askPongActor("ping")
                .thenCombine(askPongActor("ping"), (r1, r2) -> (String) r1 + (String) r2)
                .whenComplete((_result, _error) -> System.out.println("[print_to_console]result = " + _result + ", error = " + _error));

        //Note: the above should print: pongpong

        Thread.sleep(7000);
    }

    @Test
    public void print_to_console_2() throws InterruptedException {

        //when-then...
        askPongActor("wrong")
                .handle((result, error) -> {

                    if (error != null) {
                        System.out.println("[print_to_console_2] error = " + error);
                        return "default-fallback";
                    } else {
                        System.out.println("[print_to_console_2] result = " + result);
                        return result;
                    }
                })
                .thenAccept(_res -> System.out.println("final result -> " + _res));

        Thread.sleep(7000);
    }

    @Test
    public void print_to_console_3() throws InterruptedException {

        //when-then...
        askPongActor("wrong")
                .exceptionally(_error -> {
                    System.out.println("[print_to_console_3] error = " + _error);
                    return "default-fallback";
                })
                .thenAccept(_res -> System.out.println("final result -> " + _res));

        Thread.sleep(7000);
    }

    @Test
    public void print_to_console_4() throws InterruptedException {

        //when-then...
        askPongActor("wrong")
                .handle((_res, _error) -> {
                    if (_error ==null) {
                        return CompletableFuture.completedFuture(_res);
                    } else {
                        return askPongActor("ping"); // Note: do retry.
                    }
                })
                .thenCompose(x -> x) // Note: like flatten in Scala.
                .whenComplete((_res, _error) -> {
                    System.out.println("[print_to_console]result = " + _res + ", error = " + _error);
                });


        Thread.sleep(7000);
    }

    private CompletionStage<Object> askPongActor(String message) {
        Future<Object> result = Patterns.ask(actorRef, message, 5000);
        return toJava(result);
    }
}