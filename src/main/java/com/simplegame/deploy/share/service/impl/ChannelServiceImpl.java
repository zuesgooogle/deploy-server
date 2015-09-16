package com.simplegame.deploy.share.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import io.netty.channel.Channel;

import com.simplegame.deploy.share.service.IChannelService;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年9月16日 下午5:32:33
 *
 */
@Service
public class ChannelServiceImpl implements IChannelService {

    /**
     * key  :   agentId / channel.id().toString()
     * value:   Channel
     */
    private final ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
    
    @Override
    public void addChannel(Channel channel) {
        channels.put(channel.id().toString(), channel);
    }

    @Override
    public void addChannel(String agentId, Channel channel) {
        channels.remove(channel.id().toString());
        
        channels.put(agentId, channel);
    }

    @Override
    public void removeChannel(String agentId, Channel channel) {
        channels.remove(channel.id().toString());
        channels.remove(agentId);
    }

    @Override
    public Channel getChannel(String id) {
        return channels.get(id);
    }

}
