package org.ceil;

import org.ceil.model.CeilEvent;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CeilEventTest {

    /*
    Consideriamo l'evento di cella come lo stato della cella ad ogni secondo, se agente una forza su un punto allora
    lo mettiamo connected altrimenti no
    Prendiamo un flusso di 10 secondi e una serie di celle, e valutiamo per quanto tempo una certa cella
    rimane connessa ad un polo che si muove
     */

    @Test
    void CeilEventTest() {
        CeilEvent ce = new CeilEvent();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ce.setTime(ts);
        ce.setCeilReference( "ceil1" );
        ce.setStatus( CeilEvent.Connection.CONNECTED );
        assertEquals( ts, ce.getTime() );
        assertEquals( "ceil1" , ce.getCeilReference() );
        assertEquals( CeilEvent.Connection.CONNECTED , ce.getStatus() );
    }

    @Test
    void countCeilEventsTest() {
        Map<String, Integer> connectedCeilMap = countCeilEvents();
        assertEquals( 1 , connectedCeilMap.get( "ceil1") );
        assertEquals( 2 , connectedCeilMap.get( "ceil2") );
        assertEquals( 0 , connectedCeilMap.get( "ceil3") );
    }

    @Test
    void orderCountCeilEventsTest() {
        List sortedCeilEvents = orderCountCeilEvents();
        assertEquals( "Ceil2=2" , sortedCeilEvents.get(0).toString());
        assertEquals( "Ceil1=1" , sortedCeilEvents.get(1).toString());
        assertEquals( "Ceil3=0" , sortedCeilEvents.get(2).toString());
    }

    public static Map<String, Integer> countCeilEvents() {
        List<CeilEvent> eventList = createCeilEventList();

        Map<String, List<CeilEvent>> eventsPerCeilReference =
                eventList
                        .stream()
                        .collect(groupingBy(CeilEvent::getCeilReference));

        Map<String, Integer> connectedCeilMap = new HashMap<String , Integer>();
        for (Map.Entry<String, List<CeilEvent>> entry : eventsPerCeilReference.entrySet()) {
            List<CeilEvent> list = entry.getValue();
            Integer connectedEl = (int) list
                    .stream()
                    .filter( ce -> ce.getStatus().equals(CeilEvent.Connection.CONNECTED))
                    .count();
            connectedCeilMap.put(entry.getKey() , connectedEl );
        }
        return connectedCeilMap;
    }

    public static List orderCountCeilEvents() {
        List<CeilEvent> eventList = createCeilEventList();

        Map<String, List<CeilEvent>> eventsPerCeilReference =
                eventList
                        .stream()
                        .collect(groupingBy(CeilEvent::getCeilReference));

        Map<String, Integer> connectedCeilMap = new HashMap<String , Integer>();
        for (Map.Entry<String, List<CeilEvent>> entry : eventsPerCeilReference.entrySet()) {
            List<CeilEvent> list = entry.getValue();
            Integer connectedEl = (int) list
                    .stream()
                    .filter( ce -> ce.getStatus().equals(CeilEvent.Connection.CONNECTED))
                    .count();
            connectedCeilMap.put(entry.getKey() , connectedEl );
        }

        List sortedCeilEvents = connectedCeilMap
                .entrySet()
                .stream()
                .sorted( Map.Entry.<String, Integer>comparingByValue().reversed()).toList();
        return  sortedCeilEvents;
    }

    public static List<CeilEvent> createCeilEventList() {
        //partiamo dal momento corrente a misurare e incrementiamo la valutazione ogni secondo
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        List<CeilEvent> ceilEvents = new ArrayList<>();

        CeilEvent ce1 = new CeilEvent();
        ce1.setStatus(CeilEvent.Connection.CONNECTED);
        ce1.setCeilReference("Ceil1");
        ce1.setTime(ts);
        ceilEvents.add(ce1);

        CeilEvent ce2 = new CeilEvent();
        ce2.setStatus(CeilEvent.Connection.CONNECTED);
        ce2.setCeilReference("Ceil2");
        ce2.setTime(ts);
        ceilEvents.add(ce2);

        CeilEvent ce3 = new CeilEvent();
        ce3.setStatus(CeilEvent.Connection.DISCONNECTED);
        ce3.setCeilReference("Ceil3");
        ce3.setTime(ts);
        ceilEvents.add(ce3);

        //passa un secondo valuto un altro evento di cella
        ts = Timestamp.from(ts.toInstant().plus(1, ChronoUnit.SECONDS));

        CeilEvent ce4 = new CeilEvent();
        ce4.setStatus(CeilEvent.Connection.DISCONNECTED);
        ce4.setCeilReference("Ceil1");
        ce4.setTime(ts);
        ceilEvents.add(ce4);

        CeilEvent ce5 = new CeilEvent();
        ce5.setStatus(CeilEvent.Connection.CONNECTED);
        ce5.setCeilReference("Ceil2");
        ce5.setTime(ts);
        ceilEvents.add(ce5);

        CeilEvent ce6 = new CeilEvent();
        ce6.setStatus(CeilEvent.Connection.DISCONNECTED);
        ce6.setCeilReference("Ceil3");
        ce6.setTime(ts);
        ceilEvents.add(ce6);

        //passa un secondo valuto un altro evento di cella
        ts = Timestamp.from(ts.toInstant().plus(1, ChronoUnit.SECONDS));

        CeilEvent ce7 = new CeilEvent();
        ce7.setStatus(CeilEvent.Connection.DISCONNECTED);
        ce7.setCeilReference("Ceil1");
        ce7.setTime(ts);
        ceilEvents.add(ce7);

        CeilEvent ce8 = new CeilEvent();
        ce8.setStatus(CeilEvent.Connection.DISCONNECTED);
        ce8.setCeilReference("Ceil2");
        ce8.setTime(ts);
        ceilEvents.add(ce8);

        CeilEvent ce9 = new CeilEvent();
        ce9.setStatus(CeilEvent.Connection.DISCONNECTED);
        ce9.setCeilReference("Ceil3");
        ce9.setTime(ts);
        ceilEvents.add(ce9);

        return ceilEvents;
    }


}
