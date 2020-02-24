package com.example.server;

public class EcoRpcCancel extends EcoRPC{

    static String docNo = "docNo123B";

    public static void main(String[] args) {

        new EcoRpcCancel().cancelEco (docNo );
    }
}
