package com.example.server;

public class EcoRpcIssue extends EcoRPC {

    static String docNo = "docNo123";

    public static void main(String[] args) {

        new EcoRpcIssue().issueEco(docNo, docNo +"xml");
    }
}
