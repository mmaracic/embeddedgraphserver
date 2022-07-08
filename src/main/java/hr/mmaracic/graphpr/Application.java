package hr.mmaracic.graphpr;

import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.connectors.HttpConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.time.Duration;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

@SpringBootApplication
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private static void registerShutdownHook(final DatabaseManagementService managementService) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                managementService.shutdown();
            }
        });
    }

    @Bean
    public DatabaseManagementService getDatabaseManagementService() {
        DatabaseManagementService managementService = new DatabaseManagementServiceBuilder(Path.of("graphdb2"))
                .setConfig( GraphDatabaseSettings.mode, GraphDatabaseSettings.Mode.SINGLE )
                .setConfig( GraphDatabaseSettings.default_advertised_address, new SocketAddress( "localhost" ) )
                .setConfig( GraphDatabaseSettings.default_listen_address, new SocketAddress( "localhost" ) )
                .setConfig( BoltConnector.enabled, true )
                .setConfig( HttpConnector.enabled, true )
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.transaction_timeout, Duration.ofSeconds(60))
                .setConfig(GraphDatabaseSettings.preallocate_logical_logs, true).build();
        GraphDatabaseService graphDb = managementService.database(DEFAULT_DATABASE_NAME);
        registerShutdownHook(managementService);
        return managementService;
    }
}
