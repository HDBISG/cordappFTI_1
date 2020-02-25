package com.vcc.corda.eco.server;

public class EcoRpcReplace extends EcoRPC{

    static String docNo = "docNo123C";

    public static void main(String[] args) {

        //new EcoRpcReplace().issueEco ( docNo, docNo + "_XML" );

        new EcoRpcReplace().replaceEco ( docNo, docNo+"_replace",  docNo+"_replace_XML");


    }
}
