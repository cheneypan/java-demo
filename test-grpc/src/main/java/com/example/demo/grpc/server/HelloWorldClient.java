package com.example.demo.grpc.server;

import com.example.demo.grpc.hello.GreeterGrpc;
import com.example.demo.grpc.hello.HelloReply;
import com.example.demo.grpc.hello.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by cheney on 2018/7/13.
 */
public class HelloWorldClient {
    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;


    public HelloWorldClient(String host,int port){
        channel = ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext()
                .build();

        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public  void greet(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response = blockingStub.sayHello(request);
        System.out.println(response.getMessage());
    }

    public void chat(){
        GreeterGrpc.GreeterStub stub =  GreeterGrpc.newStub(channel);
        final CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<HelloRequest> requestStreamObserver = stub.chat(new StreamObserver<HelloReply>(){
            @Override
            public void onNext(HelloReply helloReply) {
                System.out.println("Reply >>> " + helloReply.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error: " + throwable.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Completed ...");
                finishLatch.countDown();
            }
        });
        Random random = new Random();
        for(int i=0; i<6; i++){
            System.out.println("index >>> " + i);
            requestStreamObserver.onNext(HelloRequest.newBuilder().setName("name_" + i).setSex("" + i).build());
            try {
                Thread.sleep(random.nextInt(1000) + 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (finishLatch.getCount() == 0) {
                return;
            }
        }
        //标识已经写完
        requestStreamObserver.onCompleted();
    }

    public static void main(String[] args) throws InterruptedException {
        HelloWorldClient client = new HelloWorldClient("127.0.0.1",50051);
//        for(int i=0;i<5;i++){
//            client.greet("world:"+i);
//        }
        client.chat();
        client.shutdown();
    }
}
