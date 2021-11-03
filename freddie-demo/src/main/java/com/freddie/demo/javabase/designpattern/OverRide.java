package com.freddie.demo.javabase.designpattern;

import java.util.List;

public class OverRide {


    public int doSomething() {
        return 0;
    }
    // 输入参数不同，意味着方法签名不同，重载的体现
    public int doSomething(List<String> strs) {
        return 0;
    }
    // return类型不一样，编译不能通过
    /*public short doSomething() {
        return 0;
    }*/

}
