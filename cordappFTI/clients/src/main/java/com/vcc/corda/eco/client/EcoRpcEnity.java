package com.vcc.corda.eco.client;

public class EcoRpcEnity {

    private String rpcHostPort = "";
    private String rpcUserName = "";
    private String rpcPassword = "";

    private String partAOrganisation = "";
    private String partALocality = "";
    private String partACountry = "";

    private String partBOrganisation = "";
    private String partBLocality = "";
    private String partBCountry = "";


    public void setRpcHost(String rpcHostPort, String rpcUserName, String rpcPassword) {
        this.rpcHostPort = rpcHostPort;
        this.rpcUserName = rpcUserName;
        this.rpcPassword = rpcPassword;
    }

    public void setPartA(String partAOrganisation, String partALocality, String partACountry) {
        this.partAOrganisation = partAOrganisation;
        this.partALocality = partALocality;
        this.partACountry = partACountry;
    }

    public void setPartB(String partBOrganisation, String partBLocality, String partBCountry) {
        this.partBOrganisation = partBOrganisation;
        this.partBLocality = partBLocality;
        this.partBCountry = partBCountry;
    }

    public String getRpcHostPort() {
        return rpcHostPort;
    }

    public void setRpcHostPort(String rpcHostPort) {
        this.rpcHostPort = rpcHostPort;
    }

    public String getRpcUserName() {
        return rpcUserName;
    }

    public void setRpcUserName(String rpcUserName) {
        this.rpcUserName = rpcUserName;
    }

    public String getRpcPassword() {
        return rpcPassword;
    }

    public void setRpcPassword(String rpcPassword) {
        this.rpcPassword = rpcPassword;
    }

    public String getPartAOrganisation() {
        return partAOrganisation;
    }

    public void setPartAOrganisation(String partAOrganisation) {
        this.partAOrganisation = partAOrganisation;
    }

    public String getPartALocality() {
        return partALocality;
    }

    public void setPartALocality(String partALocality) {
        this.partALocality = partALocality;
    }

    public String getPartACountry() {
        return partACountry;
    }

    public void setPartACountry(String partACountry) {
        this.partACountry = partACountry;
    }

    public String getPartBOrganisation() {
        return partBOrganisation;
    }

    public void setPartBOrganisation(String partBOrganisation) {
        this.partBOrganisation = partBOrganisation;
    }

    public String getPartBLocality() {
        return partBLocality;
    }

    public void setPartBLocality(String partBLocality) {
        this.partBLocality = partBLocality;
    }

    public String getPartBCountry() {
        return partBCountry;
    }

    public void setPartBCountry(String partBCountry) {
        this.partBCountry = partBCountry;
    }
}
