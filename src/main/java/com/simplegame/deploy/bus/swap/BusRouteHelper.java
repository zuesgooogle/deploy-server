package com.simplegame.deploy.bus.swap;

import com.simplegame.core.message.Message;
import com.simplegame.deploy.executor.Route;
import com.simplegame.deploy.message.IRouteHelper;

/**
 * 
 * @Author zeusgooogle@gmail.com
 * @sine 2015年6月19日 下午6:18:36
 * 
 */

public class BusRouteHelper implements IRouteHelper {

    @Override
    public Route getRoute(Message message) {
        Route route = null;
        
        switch (message.getDest().getValue()) {
        case 1:
        case 2:
        case 5:
            route = new Route("bus_cache");
            route.setData(message.getRoleId());
            break;
        case 7:
            route = new Route("system");
            route.setData(message.getRoleId());
            break;
        }
        return route;
    }

}
