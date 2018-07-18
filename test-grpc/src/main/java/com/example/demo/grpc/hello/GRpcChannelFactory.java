package com.example.demo.grpc.hello;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by cheney on 2018/7/17.
 */
public class GRpcChannelFactory extends BasePooledObjectFactory<ManagedChannel> {

    private String host;
    private int port;

    public GRpcChannelFactory(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public ManagedChannel create() throws Exception {
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
//        NettyChannelBuilder builder = NettyChannelBuilder.forAddress(host, port);
//        return builder.build();
    }

    @Override
    public PooledObject<ManagedChannel> wrap(ManagedChannel channel) {
        return new DefaultPooledObject<>(channel);
    }

    @Override
    public void destroyObject(PooledObject<ManagedChannel> p) throws Exception {
        System.out.println("close channel ...");
        p.getObject().shutdown().awaitTermination(5, TimeUnit.SECONDS);
        super.destroyObject(p);
    }
}
