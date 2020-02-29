package com.vcc.corda.eco.client;

public class EcoRpcCancel extends EcoRpc {

    static String docNo = "docNo123B";

    public EcoRpcCancel( EcoRpcEnity rpcEnity ){
        super( rpcEnity );
    }

    public static void main(String[] args) {

        EcoRpcEnity rpcEnity = new EcoRpcEnity();

        rpcEnity.setRpcHost( "localhost:10005", "user1" , "test" );
        rpcEnity.setPartA( "PartyA", "London", "GB" );
        rpcEnity.setPartB( "PartyB", "New York", "US" );

        new EcoRpcCancel( rpcEnity ).cancelEco (docNo );
    }
}
