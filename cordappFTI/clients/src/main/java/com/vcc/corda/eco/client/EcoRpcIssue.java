package com.vcc.corda.eco.client;

public class EcoRpcIssue extends EcoRpc {

    static String docNo = "docNo123B";

    public EcoRpcIssue( EcoRpcEnity rpcEnity ){
        super( rpcEnity );
    }

    public static void main(String[] args) {
        EcoRpcEnity rpcEnity = new EcoRpcEnity();

        rpcEnity.setRpcHost( "localhost:10005", "user1" , "test" );
        rpcEnity.setPartA( "PartyA", "London", "GB" );
        rpcEnity.setPartB( "PartyB", "New York", "US" );

        new EcoRpcIssue( rpcEnity ).issueEco(docNo, docNo +"xml");
    }
}
