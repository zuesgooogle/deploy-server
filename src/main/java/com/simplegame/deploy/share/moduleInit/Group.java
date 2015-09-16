package com.simplegame.deploy.share.moduleInit;


/**
 *
 * Module 所属分组
 *
 * @Author zeusgooogle@gmail.com
 * @sine   2015年9月10日 下午5:14:07
 *
 */
public enum Group {

    IO("io", 0),
    
    BUS("bus", 1),
    
    SYSTEM("system", 2),
    
    ;

    public final String name;
    
    public final int value;

    private Group(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }
    
    public int getValue() {
        return this.value;
    }

    public static Group find(int group) {
        switch (group) {
        case 0: return IO;
        case 1: return BUS;
        case 2: return SYSTEM;
        default:
            throw new IllegalArgumentException("invalid group.");
        }
    }
}
