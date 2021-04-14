package com.kushkumardhawan.reactive.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.IntStream;

@Slf4j
/**
 * Reactive Streams
 * 1. Asyncronous
 * 2. Non Blocking
 * 3. BackPressure
 *
 * Publisher  == Creating the Events
 * Publisher <- (Suscribe) Suscriber
 * Subscription is created   (Subscription is created by Publisher)
 * Publisher (onSubscribe with the subscription) -> subscriber
 * Subscription can be used to handle the back pressure <- (request N subscriber)
 * Publisher -> onNext from the Subscriber
 * until:
 * 1). Publisher sends all the object requested.
 * 2). Publisher sends all the objects it has onCompleted :: Subscriber and the subscription will be cancelled.
 * 3). If we have an error, onError will be called :: Subscriber and Subscription will be cancelled.
 *
 *
 * MONO
 *  is a single object or void  (either you send an object or nothing.)
 *
 */
public class MonoTest {

    @Test
    public void monoSubscriber(){
        log.info("Everything is working fine.");
        String name = "Kush Kumar Dhawan";
        //This mono is a Publisher. If we want to see what is inside this publisher we need to subscribe
        //Operators of Mono
        // Log

        Mono<String> mono =Mono.just(name).log();
        mono.subscribe();

        log.info("============== This is the Test ==================");
        StepVerifier.create(mono)
                    .expectNext(name)
                    .verifyComplete();

        log.info(String.valueOf(mono));

    }

    @Test
    public void monoSubscriberConsumer(){
        log.info("Everything is working fine. inside Mono Subscriber Consumer");

        String name = "Luv Kumar Dhawan";
        // Mono<String> mono = Mono.just(name).log();
        Mono<String> mono = Mono.just(name).log();
        mono.subscribe( (s) -> log.info("value {} ",  s));

        //Test the Mono
        log.info("============== This is the Test ==================");
        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();



    }


    @Test
    public void monoSubscriberConsumerError(){
        log.info("Everything is working fine. inside Mono Subscriber Consumer");

        String name = "Luv Kumar Dhawan";
       // Mono<String> mono = Mono.just(name).log();
        Mono<String> mono = Mono.just(name)
                                .map(
                                      s-> {throw  new RuntimeException("Testing momo with example!");}
                                        );
                                            ;
        mono.subscribe( (s) -> log.info("value {} ",  s) , (s) -> log.info("Something Bad Happened"));
        mono.subscribe( (s) -> log.info("value {} ",  s) ,  Throwable:: printStackTrace);

        //Test the Mono
        log.info("============== This is the Test ==================");
        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();



    }

    @Test
    public void monoSubscriberConsumerCmplete(){
        log.info("Everything is working fine. inside Mono Subscriber Consumer");

        String name = "luv kumar dhawan";
        // Mono<String> mono = Mono.just(name).log();
        Mono<String> mono = Mono.just(name)
                                .log()
                                .map(String::toUpperCase);
        mono.subscribe(
                (s) -> log.info("value {} ",  s),
                Throwable::printStackTrace ,
                ()->log.info("Task Completed."));

        //Test the Mono
        log.info("============== This is the Test ==================");
        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();



    }


    @Test
    public void monoSubscriberConsumerSubscription(){
        log.info("Everything is working fine. inside Mono Subscriber Consumer Subscription");

        String name = "luv kumar dhawan";
        // Mono<String> mono = Mono.just(name).log();
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);
        mono.subscribe(
                (s) -> log.info("value {} ",  s),
                Throwable::printStackTrace ,
                ()->log.info("Task Completed."),
                Subscription::cancel
        );

        //Test the Mono
        log.info("============== This is the Test ==================");
        StepVerifier.create(mono)
                .expectNext(name.toUpperCase())
                .verifyComplete();



    }

    @Test
    public void monoSubscriberConsumerSubscriptionNumber(){
        log.info("Everything is working fine. inside Mono Subscriber Consumer Subscription");

        String name = "luv kumar dhawan";
        // Mono<String> mono = Mono.just(name).log();
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);
        mono.subscribe(
                (s) -> log.info("value {} ",  s),
                Throwable::printStackTrace ,
                ()->log.info("Task Completed."),
                subscription -> subscription.request(5)
        );
       }


    @Test
    public void monoDoOnMethords(){
        log.info("Everything is working fine. inside Mono Do OnMethords");
        String name = "luv kumar dhawan";
        Mono<Object> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subscribed."))
                .doOnRequest(longNumber -> log.info("Request Received. Started Doing Something."))
                .doOnNext(s -> log.info("value is Here. Executing doNext {}",s))
                .map(String::length)
                .doOnNext(s -> log.info("value is Here. Executing doNext for the second Time {}",s))
                .flatMap(s->Mono.empty())
                .doOnNext(s -> log.info("value is Here. Executing doNext for the Third Time {}",s))
                .doOnSuccess(s -> log.info("Do On Success Executed."));

        mono.subscribe(
                (s) -> log.info("value {} ",  s),
                Throwable::printStackTrace ,
                ()->log.info("Task Completed.")
        );
    }


    @Test
    public void monoDoOnError(){
        log.info("Everything is working fine. inside Mono Do OnError");
        String name = "luv kumar dhawan";
        Mono<Object> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subscribed."))
                .doOnRequest(longNumber -> log.info("Request Received. Started Doing Something."))
                .doOnNext(s -> log.info("doNext {}", s))
                .map(String::length)
                .doOnNext(s -> log.info("Do Next {}", s))

                .map(Integer::toBinaryString)
                .doOnNext(s -> log.info("Do Next{}", s))

                .map(String::toCharArray)
                .doOnNext(s -> log.info("Do Next{}", s))

                .map(char[]::toString)
                .doOnNext(s -> log.info("Do Next{}", s))

                .map(String:: toLowerCase)
                .doOnNext(s -> log.info("Do Next{}", s))


                .flatMap(s -> Mono.empty())
                .doOnNext(s -> log.info("Do Next{}", s))
                .doOnSuccess(s -> log.info("Do On Success Executed."));

        mono.subscribe(
                (s) -> log.info("value {} ",  s),
                Throwable::printStackTrace ,
                ()->log.info("Task Completed.")
        );
    }

}
