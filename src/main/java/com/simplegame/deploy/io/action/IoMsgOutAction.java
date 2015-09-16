package com.simplegame.deploy.io.action;

import io.netty.channel.Channel;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.simplegame.core.action.annotation.ActionMapping;
import com.simplegame.core.action.annotation.ActionWorker;
import com.simplegame.deploy.io.message.IoMessage;
import com.simplegame.deploy.share.service.IChannelService;
import com.simplegame.protocol.proto.Message.Response;

/**
 * 
 * @Author zeusgooogle@gmail.com
 * @sine 2015年5月19日 下午5:02:38
 * 
 */
@ActionWorker
public class IoMsgOutAction {

    private Logger LOG = LogManager.getLogger(getClass());

    @Resource
    private IChannelService channelService;

    @ActionMapping(mapping = IoMessage.IO_MSG_OUT_CMD)
    public void out(IoMessage message) {
        LOG.info("message out: {}", message.toString());

        Response.Builder builder = Response.newBuilder();
        builder.setCommand(message.getRealCommand()).setData(message.toData());

        int route = message.getRoute();
        switch (route) {
        case 1: // one player
            Channel channel = null;
            String sessionId = message.getSessionId();
            if (null != sessionId) {
                channel = channelService.getChannel(sessionId);
            }

            String agentId = message.getRoleId();
            if (null != agentId) {
                channel = channelService.getChannel(agentId);
            }

            if (null != channel) {
                channel.writeAndFlush(builder);
            }

            break;
        case 2: // mutile player
            break;
        case 3: // all player
            break;
        }

    }

}
