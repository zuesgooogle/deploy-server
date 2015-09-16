package com.simplegame.deploy.utils;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年6月30日 下午3:58:58
 *
 */

public class ChannelAttributeUtil {

    @SuppressWarnings("unchecked")
    public static String attr(Channel channel, String... params) {
        if( params.length == 0 ) {
            return null;
        }
        
        AttributeKey key = AttributeKey.valueOf(params[0]);
        
        if( params.length == 1 ) {
            return (String)channel.attr(key).get();
        } else {
            channel.attr(key).set(params[1]);
            return params[1]; 
        }
    }
    
}
