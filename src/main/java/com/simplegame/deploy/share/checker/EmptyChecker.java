package com.simplegame.deploy.share.checker;

import com.simplegame.deploy.executor.IRuleChecker;

/**
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年9月16日 下午6:44:56
 *
 */

public class EmptyChecker implements IRuleChecker {

    @Override
    public boolean valid(Object rule) {
        return true;
    }

}
