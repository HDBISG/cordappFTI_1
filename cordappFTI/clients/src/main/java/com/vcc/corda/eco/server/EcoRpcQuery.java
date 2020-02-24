package com.vcc.corda.eco.server;

public class EcoRpcQuery extends EcoRPC{

    static String docNo = "docNo123ABCxml";

    public static void main(String[] args) {

        new EcoRpcQuery().queryEco (docNo );
    }
}
