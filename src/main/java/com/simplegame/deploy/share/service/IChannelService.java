package com.simplegame.deploy.share.service;

import io.netty.channel.Channel;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年9月16日 下午5:23:48
 *
 */

public interface IChannelService {
    
    /**
     * 
     * 增加 channel
     * 
     * @param channel
     */
    public void addChannel(Channel channel);
    
    /**
     * 增加 channel
     * 
     * channel.getId() 如果存在，先删除
     * 
     * 使用 agentId 作为 key addChannel
     * 
     * @param agentId
     * @param channel
     */
    public void addChannel(String agentId, Channel channel);

    /**
     * 删除 channel
     * 
     * @param agentId
     * @param channel
     */
    public void removeChannel(String agentId, Channel channel);
    
    /**
     * 获取 channel
     * <p> channel.id() OR agentId;
     * 
     * @param id
     * @return
     */
    public Channel getChannel(String id);

}
