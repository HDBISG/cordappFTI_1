package com.example.server;

import com.example.flow.EcoCancelFlow;
import com.example.flow.EcoIssueFlowInitiator;
import com.example.schema.EcoSchemaV1;
import com.example.state.EcoState;
import com.google.common.collect.ImmutableList;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.corda.core.node.services.vault.QueryCriteriaUtils.getField;

public class EcoRPC {
    private static final Logger logger = LoggerFactory.getLogger(EcoRPC.class);

    private CordaRPCConnection getConnection() {
        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse( "localhost:10005" );
        String username = "user1";
        String password = "test";

        final CordaRPCClient client = new CordaRPCClient(nodeAddress, CordaRPCClientConfiguration.DEFAULT);

        final CordaRPCConnection connection = client.start(username, password);

        return connection;
    }

    public void issueEco( String docNo, String ecoXML ) {

        logger.info("begin");

        final CordaRPCConnection connection = getConnection();

        final CordaRPCOps proxy = connection.getProxy();

        //cordaRPCOperations.startFlowDynamic( )
        CordaX500Name partyBName = new CordaX500Name("PartyA", "London", "GB");
        Party partyA = proxy.wellKnownPartyFromX500Name(partyBName);

        partyBName = new CordaX500Name("PartyB", "New York", "US");
        Party partyB = proxy.wellKnownPartyFromX500Name(partyBName);

        EcoState ecoState = new EcoState( partyA, partyB, docNo, ecoXML, new UniqueIdentifier( docNo ) );

        System.out.println("ecoState=" + ecoState );

        try {
            FlowHandle<SignedTransaction> flowHandle1 = proxy.startFlowDynamic(EcoIssueFlowInitiator.class, ecoState);
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
            FlowHandle<SignedTransaction> flowHandle1 = proxy.startFlowDynamic( EcoCancelFlow.EcoCancelFlowInitiator.class, docNo );
        }catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(proxy.currentNodeTime().toString());

        connection.notifyServerAndClose();

        System.out.println("end cancel");
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
