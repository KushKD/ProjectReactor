package com.kushkumardhawan.reactive.test;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@Slf4j
public class FluxTest {


    @Test
    public void FluxPublisherString(){
        String[] name = {"Kush Kumar Dhawan","Testing"};
        Flux<String> string_ = Flux.just(name).log();
        log.info("We are here in Flux Publisher");
        string_.subscribe();

        log.info("==========  Verify  ================= ");
        StepVerifier.create(string_).expectNext(name).verifyComplete();

    }


    @Test
    public void FluxPublisherNumbers(){
        Integer[] num = {1,2,3,4,5};
        Flux<Integer> integer_ = Flux.just(num).log();
        log.info("We are here in Flux Publisher");
        integer_.subscribe();

        log.info("==========  Verify  ================= ");
        StepVerifier.create(integer_).expectNext(num).verifyComplete();

    }


    @Test
    public void FluxSubscriberFromList(){
        //We are not looking for this
        Flux<List<Integer>> integer_ = Flux.just(List.of(1,2,3,4,5)).log();
        log.info("We are here in Flux Publisher");
        integer_.subscribe();

        log.info("==========  Verify  ================= ");
        StepVerifier.create(integer_).expectNext(List.of(1,2,3,4,5)).verifyComplete();
        log.info("We are not looking for this");

        log.info("We are  looking for this");
        Flux<Integer> integer__ = Flux.fromIterable(List.of(1,2,3,4,5,6)).log();
        integer__.subscribe();

        log.info("==========  Verify  ================= ");
        StepVerifier.create(integer__).expectNext(1,2,3,4,5,6).verifyComplete();
        log.info("We are not looking for this");



    }


    @Test
    public void FluxSubscriberNumberError(){


        Flux<Integer> integer__ = Flux.range(1,5).log()
                .map( (i)-> {
               if(i==4){
                   throw new IndexOutOfBoundsException("Index Error") ;
               }

            return i;
        } );

        integer__.subscribe(
                (i) -> log.info("Value of number {}",i),
                Throwable::printStackTrace,
                () -> log.info("Done"),
                subscription -> subscription.request(3));

        log.info("==========  Verify  ================= ");
        StepVerifier.create(integer__)
                .expectNext(1,2,3)
                .expectError(IndexOutOfBoundsException.class)
        .verify();




    }



    @Test
    public void FluxSubscriberNumberUglyBackPressure(){


        Flux<Integer> integer__ = Flux.range(1,10).log();


        integer__.subscribe(new Subscriber<Integer>() {

            private int count;
            private Subscription subscription;
            private int requestCount = 3;
            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(requestCount);
            }

            @Override
            public void onNext(Integer integer) {
                count++;
                if(count>=requestCount){
                    count = 0;
                    subscription.request(requestCount);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        log.info("==========  Verify  ================= ");
        StepVerifier.create(integer__)
                .expectNext(1,2,3,4,5,6,7,8,9,10)
                .verifyComplete();




    }



    @Test
    public void FluxSubscriberNumberNotSoUglyBackPressure(){


        Flux<Integer> integer__ = Flux.range(1,10).log();


        integer__.subscribe(new BaseSubscriber<Integer>() {
            //Inside BaseSubscriber there is already a subscription
            private int count;
            private final int requestCount = 2;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                //super.hookOnSubscribe(subscription);
                request(requestCount);
            }

            @Override
            protected void hookOnNext(Integer value) {
                log.info("value is {}",value);
                //super.hookOnNext(value);
                count++;
                log.info("Count ++ is {}",count);
                if(count>=requestCount){
                    count = 0;
                    request(requestCount);
                }
            }
        });

        log.info("==========  Verify  ================= ");
        StepVerifier.create(integer__)
                .expectNext(1,2,3,4,5,6,7,8,9,10)
                .verifyComplete();




    }

    //Intervals : How can we schedule publisher after predefined time
    @Test
    public void FluxSubscriberInterval () throws Exception{

        Flux interval = Flux.interval(Duration.ofMillis(100)).take(10).log();
        interval.subscribe(  (i) -> log.info("Number is {}",i));

        Thread.sleep(3000);
    }

    //Limi Tate Cheat
    @Test
    public void FluxSubscriberLimitRate (){

       Flux<Integer> integer_ = Flux.range(1,10).limitRate(2).log();
       integer_.subscribe(integer -> log.info("Value is:- {}",integer));
    }



    @Test
    public void FluxSubscriberIntervalTwo () throws Exception{

         Flux<Long> interval = createInterval();

        StepVerifier.withVirtualTime(this::createInterval)
                .expectSubscription()
                .expectNoEvent(Duration.ofHours(24))
                .thenAwait(Duration.ofDays(2))
                .expectNext(0L)
                .thenAwait(Duration.ofDays(2))
                .expectNext(1L)
                .thenCancel()
                .verify();
    }

    private Flux<Long> createInterval(){
        return Flux.interval(Duration.ofDays(2)).log();
    }


    //Connectable Flux Hot
    @Test
    public void connectableFlux() throws Exception{
        ConnectableFlux<Integer> connectableFlux = Flux.range(1,20)
                                                      //  .log()
                                                        .delayElements(Duration.ofMillis(100))
                                                        .publish();
        connectableFlux.connect();

        log.info("Thread Sellepng 300ms");
        Thread.sleep(300);
        connectableFlux.subscribe(integer -> log.info("Sub 1 Integer is {}",integer));

        log.info("Thread Sellepng 200ms");
        Thread.sleep(200);
        connectableFlux.subscribe(integer -> log.info(" Sub 2 Integer is {}",integer));
    }

}
