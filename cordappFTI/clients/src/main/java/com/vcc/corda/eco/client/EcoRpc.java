package com.vcc.corda.eco.client;

import com.vcc.corda.eco.flow.EcoCancelFlow;
import com.vcc.corda.eco.flow.EcoIssueFlowInitiator;
import com.vcc.corda.eco.flow.EcoReplaceFlow;
import com.vcc.corda.eco.schema.EcoSchemaV1;
import com.vcc.corda.eco.state.EcoState;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowHandle;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.FieldInfo;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.NetworkHostAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.corda.core.node.services.vault.QueryCriteriaUtils.getField;

public class EcoRpc {
    private static final Logger logger = LoggerFactory.getLogger(EcoRpc.class);

    EcoRpcEnity rpcEnity ;

    public EcoRpc( EcoRpcEnity rpcEnity ){
        this.rpcEnity = rpcEnity;
    }

    private CordaRPCConnection getConnection() {
        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse( rpcEnity.getRpcHostPort() );
        String username = rpcEnity.getRpcUserName();
        String password = rpcEnity.getRpcPassword();

        final CordaRPCClient client = new CordaRPCClient(nodeAddress, CordaRPCClientConfiguration.DEFAULT);

        final CordaRPCConnection connection = client.start(username, password);

        return connection;
    }

    private Party getPartyA( CordaRPCOps proxy ) {

        CordaX500Name partyAName = new CordaX500Name(rpcEnity.getPartAOrganisation(), rpcEnity.getPartALocality(), rpcEnity.getPartACountry() );
        Party partyA = proxy.wellKnownPartyFromX500Name(partyAName);

        return partyA;
    }

    private Party getPartyB( CordaRPCOps proxy ) {

        CordaX500Name partyBName = new CordaX500Name(rpcEnity.getPartBOrganisation(), rpcEnity.getPartBLocality(), rpcEnity.getPartBCountry() );
        Party partyB = proxy.wellKnownPartyFromX500Name(partyBName);

        return partyB;
    }

    public void issueEco( String docNo, String ecoXML ) {

        logger.info("begin");

        final CordaRPCConnection connection = getConnection();
        final CordaRPCOps proxy = connection.getProxy();

        Party partyA = this.getPartyA( proxy );

        Party partyB = this.getPartyB( proxy );

        EcoState ecoState = new EcoState( partyA, partyB, docNo, ecoXML, new UniqueIdentifier( docNo ) );

        System.out.println("issue ecoState=" + ecoState );

        try {
            FlowHandle<SignedTransaction> flowHandle = proxy.startFlowDynamic(EcoIssueFlowInitiator.class, ecoState);
        }catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(proxy.currentNodeTime().toString());

        connection.notifyServerAndClose();

        System.out.println("end issue");
    }

    public void cancelEco( String docNo  ) {

        logger.info("begin");

        final CordaRPCConnection connection = getConnection();

        final CordaRPCOps proxy = connection.getProxy();

        System.out.println("docNo=" + docNo );

        try {
            FlowHandle<SignedTransaction> flowHandle = proxy.startFlowDynamic( EcoCancelFlow.EcoCancelFlowInitiator.class, docNo );
        }catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(proxy.currentNodeTime().toString());

        connection.notifyServerAndClose();

        System.out.println("end cancel");
    }

    public void replaceEco( String oldDocNo, String newDocNo, String newEcoXML  ) {

        logger.info("begin");

        final CordaRPCConnection connection = getConnection();
        final CordaRPCOps proxy = connection.getProxy();

        Party partyA = this.getPartyA( proxy );
        Party partyB = this.getPartyB( proxy );

        EcoState ecoState = new EcoState( partyA, partyB, newDocNo, newEcoXML, new UniqueIdentifier( newDocNo ) );

        System.out.println("update old docNo=" + newDocNo );

        try {
            FlowHandle<SignedTransaction> flowHandle = proxy.startFlowDynamic( EcoReplaceFlow.EcoReplaceFlowInitiator.class, oldDocNo, ecoState );
        }catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(proxy.currentNodeTime().toString());

        connection.notifyServerAndClose();

        System.out.println("end replace");
    }

    public void queryEco( String docNo  ) {

        logger.info("begin");

        final CordaRPCConnection connection = getConnection();

        final CordaRPCOps proxy = connection.getProxy();

        System.out.println("docNo=" + docNo );

        try {
            List<StateAndRef<EcoState>> stateAndRefs = proxy.vaultQuery( EcoState.class ).getStates();
            System.out.println("stateAndRefs= " + stateAndRefs  );
            System.out.println("stateAndRefs= " + stateAndRefs.size() );
        //    System.out.println("stateAndRefs= " + stateAndRefs.get(0).getState().getData().getLinearId().getExternalId() );


/*
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(
                    null, null,
                    ImmutableList.of( "docNo123" ), Vault.StateStatus.UNCONSUMED,null);

            queryCriteria = new QueryCriteria.VaultQueryCriteria();

           // queryCriteria = new QueryCriteria.LinearStateQueryCriteria(  Arrays.asList( uuid ) );
*/
          //  QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria( null, null, null );
            //UUID id = someExternalId;

            // QueryCriteria queryCriteria = new QueryCriteria.VaultQueryCriteria( ); // OK

            QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
            FieldInfo attributeDocNo = getField("docNo", EcoSchemaV1.PersistentEco.class);
            //CriteriaExpression docNoIndex = Builder.like(attributeDocNo, "%");
            CriteriaExpression docNoIndex = Builder.equal(attributeDocNo, docNo );
            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria( docNoIndex );

            QueryCriteria criteria = generalCriteria.and( customCriteria );

            Vault.Page<EcoState> page =  proxy.vaultQueryByCriteria( customCriteria, EcoState.class );
            /*System.out.println("stateAndRefs 0000 = " + attributeDocNo.getName() );
            System.out.println("stateAndRefs 1111 = " + docNoIndex );
            System.out.println("stateAndRefs 2222 = " + customCriteria );
            System.out.println("stateAndRefs 3333 = " + page );*/

            stateAndRefs =  page.getStates();
            System.out.println("stateAndRefs 5555 = " + stateAndRefs.size() );
            // Vault.Page results = getServiceHub().getVaultService().queryBy(EcoState.class, queryCriteria);

        }catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(proxy.currentNodeTime().toString());

        connection.notifyServerAndClose();

        System.out.println("end cancel");
    }
}
