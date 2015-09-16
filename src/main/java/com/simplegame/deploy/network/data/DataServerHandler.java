package com.simplegame.deploy.network.data;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSONArray;
import com.simplegame.core.SpringApplicationContext;
import com.simplegame.core.message.Message;
import com.simplegame.core.message.Message.DestType;
import com.simplegame.core.message.Message.FromType;
import com.simplegame.deploy.bus.agent.command.AgentCommands;
import com.simplegame.deploy.io.IoConstants;
import com.simplegame.deploy.io.swap.IoMsgSender;
import com.simplegame.deploy.share.service.IChannelService;
import com.simplegame.deploy.utils.ChannelAttributeUtil;
import com.simplegame.protocol.proto.Message.Request;

/**
 * 
 * @Author zeusgooogle@gmail.com
 * @sine 2015年9月14日 下午3:29:54
 * 
 */
public class DataServerHandler extends SimpleChannelInboundHandler<Request> {

    private Logger LOG = LogManager.getLogger(getClass());

    private IChannelService channelService;
    
    private IoMsgSender msgSender;

    public DataServerHandler() {
        ApplicationContext ctx = SpringApplicationContext.getApplicationContext();
        
        channelService = ctx.getBean(IChannelService.class);
        msgSender = ctx.getBean(IoMsgSender.class);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = address.getHostName();
        
        ChannelAttributeUtil.attr(ctx.channel(), IoConstants.IP_KEY, ip);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Request msg) throws Exception {
        LOG.info("receive data message: {}", msg.toString());

        JSONArray array = JSONArray.parseArray(msg.getData());
        if (null == array) {
            array = new JSONArray();
        }

        String command = msg.getCommand();
        switch (command) {
        case AgentCommands.REGISTER:
            channelService.addChannel(ctx.channel());
            break;

        case AgentCommands.LOGIN:
            String agentId = array.getString(0);
            ChannelAttributeUtil.attr(ctx.channel(), IoConstants.AGENT_KEY, agentId);

            channelService.addChannel(agentId, ctx.channel());
            break;
        }

        String agentId = ChannelAttributeUtil.attr(ctx.channel(), IoConstants.AGENT_KEY);
        String ip = ChannelAttributeUtil.attr(ctx.channel(), IoConstants.IP_KEY);
        
        Message message = new Message(command, array.toArray(), FromType.CLIENT, DestType.BUS, agentId, null, ctx.channel().toString(), ip);
        msgSender.swap(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

}
