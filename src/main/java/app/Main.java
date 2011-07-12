package app;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static app.Main.RelationShips.*;
import static org.neo4j.cypher.javacompat.CypherParser.parseConsole;

/**
 * @author tbaum
 * @since 12.07.11
 */
public class Main {
// ------------------------------ FIELDS ------------------------------

    private static EmbeddedGraphDatabase db;
    private static ExecutionEngine executionEngine;

// -------------------------- ENUMERATIONS --------------------------

    public enum RelationShips implements RelationshipType {
        MEMBERS, MEMBER, LOCATIONS, LOCATION, ACTIVITIES, ACTIVITY, HAPPENS_IN, DOES
    }

// --------------------------- main() method ---------------------------

    public static void main(String[] args) throws IOException {
        String storeDir = "/Users/tbaum/Downloads/neo4j-community-1.4/data/graph.db";
        FileUtils.deleteRecursively(new File(storeDir));

        db = new EmbeddedGraphDatabase(storeDir);
        Transaction tx = db.beginTx();

        Node referenceNode = db.getReferenceNode();

        Node members = createNode("all c-base members");
        referenceNode.createRelationshipTo(members, MEMBERS);

        Node memberStevie = createNode("Stevie");
        members.createRelationshipTo(memberStevie, MEMBER);

        Node memberMacro = createNode("Macro");
        members.createRelationshipTo(memberMacro, MEMBER);

        Node memberPirat = createNode("Kristall-Pirat");
        members.createRelationshipTo(memberPirat, MEMBER);


        Node locations = createNode("all locations");
        referenceNode.createRelationshipTo(locations, LOCATIONS);


        Node locationMainhall = createNode("c-base: mainhall");
        locations.createRelationshipTo(locationMainhall, LOCATION);

        Node locationShop = createNode("c-base: shop");
        locations.createRelationshipTo(locationShop, LOCATION);

        Node locationBar = createNode("c-base: bar");
        locations.createRelationshipTo(locationBar, LOCATION);


        Node activities = createNode("all activities");
        referenceNode.createRelationshipTo(activities, ACTIVITIES);

        Node activityFreifunk = createNode("Freifunk");
        activities.createRelationshipTo(activityFreifunk, ACTIVITY);

        locationShop.createRelationshipTo(activityFreifunk, HAPPENS_IN);
        memberPirat.createRelationshipTo(activityFreifunk, DOES);
        memberMacro.createRelationshipTo(activityFreifunk, DOES);

        Node activityHackAndTell = createNode("Hack & Tell");
        activityHackAndTell.setProperty("category", "event-planing");
        activities.createRelationshipTo(activityHackAndTell, ACTIVITY);

        locationMainhall.createRelationshipTo(activityHackAndTell, HAPPENS_IN);
        memberPirat.createRelationshipTo(activityHackAndTell, DOES);
        memberMacro.createRelationshipTo(activityHackAndTell, DOES);
        memberStevie.createRelationshipTo(activityHackAndTell, DOES);

        Node activityGtug = createNode("GTUG");
        activityGtug.setProperty("category", "event-planing");
        activities.createRelationshipTo(activityGtug, ACTIVITY);
        locationMainhall.createRelationshipTo(activityGtug, HAPPENS_IN);
        memberPirat.createRelationshipTo(activityGtug, DOES);
        memberStevie.createRelationshipTo(activityGtug, DOES);

        Node activityBardienst = createNode("Bar-Dienst");
        activities.createRelationshipTo(activityBardienst, ACTIVITY);
        locationBar.createRelationshipTo(activityBardienst, HAPPENS_IN);
        memberMacro.createRelationshipTo(activityBardienst, DOES);


        tx.success();
        tx.finish();

        executionEngine = new ExecutionEngine(db);

        System.out.println("our activities");

        for (Map<String, Object> row : executionEngine.execute(parseConsole("START n=(0) " +
                "MATCH n-[:ACTIVITIES]-()-[:ACTIVITY]->a " +
                "RETURN a.name"))) {
            System.out.println(row);
        }
        System.out.println();
        System.out.println("members in activities that have category 'event-planing'");

        for (Map<String, Object> row : executionEngine.execute(parseConsole("START n=(0) " +
                "MATCH n-[:ACTIVITIES]-()-[:ACTIVITY]->a, m-[:DOES]->a  where a.category AND a.category='event-planing' " +
                "RETURN m.name, a.name"))) {
            System.out.println(row);
        }

        db.shutdown();
    }

    private static Node createNode(String nameValue) {
        Node activities = db.createNode();
        activities.setProperty("name", nameValue);
        return activities;
    }
}
