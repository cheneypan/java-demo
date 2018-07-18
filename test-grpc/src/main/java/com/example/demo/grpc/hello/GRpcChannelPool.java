package com.example.demo.grpc.hello;

import io.grpc.ManagedChannel;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by cheney on 2018/7/17.
 */
public class GRpcChannelPool implements Closeable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private GenericObjectPool<ManagedChannel> channelGenericObjectPool;

    private String host;
    private int port;

    public GRpcChannelPool(GenericObjectPoolConfig poolConfig, String host, int port) {
        logger.info("MaxIdle = {}", poolConfig.getMaxIdle());
        logger.info("MaxTotal = {}", poolConfig.getMaxTotal());
        this.host = host;
        this.port = port;
        channelGenericObjectPool = new GenericObjectPool<>(new GRpcChannelFactory(host, port), poolConfig);
    }

    public ManagedChannel borrowChannel(){
        try {
            ManagedChannel channel = channelGenericObjectPool.borrowObject();
            logger.info("channel thread count[host={}, port={}]: {}", this.host, this.port, channelGenericObjectPool.getCreatedCount());
            return channel;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        ManagedChannel channel = null;
//        try {
//            channel = new GRpcChannelFactory(host, port).create();
//            logger.info("channel thread count[error]: {}", channelGenericObjectPool.getCreatedCount());
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
        return channel;
    }

    public void returnChannel(ManagedChannel channel){
        channelGenericObjectPool.returnObject(channel);
    }


    @Override
    public void close() throws IOException {
        logger.info("close pool ... ...");
        channelGenericObjectPool.close();
    }
}
