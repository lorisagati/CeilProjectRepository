package org.ceil;

import org.ceil.model.Ceil;
import org.ceil.model.CeilEvent;
import org.ceil.model.Point;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class Main {

    public static void main(String[] args) {

        /*
        Dato un elenco di celle e un Punto (definito con Latitudine e Longitudine):
        o filtrare quelle che hanno una forza maggiore di un valore dato nel Punto
        o restituire le celle individuate ordinate per forza decrescente
        */

        //PUNTO UNO
        orderFilteredCeils(createUnorderedList(), new Point(0.0, 0.0), 50);

        /*
        Data una serie di Eventi di Cella:
        o estrarre tutte le celle in ordine di frequenza (ovvero dalla più comune alla più rara)

        Assunzioni dalle specifiche riportate nel testo
        Consideriamo l'evento di cella come lo stato della cella ad ogni secondo, se agente una forza su un punto allora
        lo mettiamo connected altrimenti no
        Prendiamo un flusso di 5 secondi e una serie di celle, e valutiamo per quanto tempo una certa cella
        rimane connessa ad un polo che si muove
        */
        // PUNTO DUE
        countCeilEvents();
    }
    private static void orderFilteredCeils(List<Ceil> unorderedList, Point point, double threshold) {

        unorderedList
                .stream()
                .filter(el -> el.evalForcewithRadius(point) > threshold )
                .sorted(Comparator.comparing(Ceil::getForce).reversed())
                .forEach(
                        c -> System.out.println(
                                "The Ceil with Name: " + c.getName()
                                        + " , center with latitude: " + c.getCenter().getLatitude()
                                        + "  and longitude: " + c.getCenter().getLongitude()
                                        + " , Radius: " + c.getRadius()
                                        + " exercise a Force (in %) of " + c.getForce()
                        ));

        unorderedList
                .stream()
                .filter(el -> el.evalForceWithPower(point) > threshold)
                .sorted(Comparator.comparing(Ceil::getForce).reversed())
                .forEach(
                        c -> System.out.println(
                                "The Ceil with Name: " + c.getName()
                                        + " , center with latitude: " + c.getCenter().getLatitude()
                                        + "   and longitude: " + c.getCenter().getLongitude()
                                        + " and Power: " + c.getPower()
                                        + " exercise a Force (in %) " + c.getForce()));
    }
    private static List<Ceil> createUnorderedList() {

        Ceil ceil = null;
        Point center = null;
        List<Ceil> unorderedCeils = new ArrayList<>();

        for(int i=0; i < 5; i++) {
            String ceilName = "Ceil"+i;
            double latitue = 0.0;
            double longitude = 0.1 - i*0.01;
            center = new Point( latitue , longitude );
            double radius = 10;
            double power = 1.25 - i*0.125;
            ceil = new Ceil(ceilName , center , radius, power);
            unorderedCeils.add(ceil);
        }
        return unorderedCeils;
    }

    public static void countCeilEvents() {
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

        System.out.println(sortedCeilEvents);
    }

    public static List<CeilEvent> createCeilEventList() {
        //partiamo dal momento corrente a misurare e incrementiamo la valutazione ogni secondo
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        List<Ceil> ceilList = createUnorderedList();
        List<CeilEvent> ceilEvents = new ArrayList<>();
        //sposto il polo ogni secondo e valuto se c'è interazione tra polo e ogni cella,
        //se sì è connected altrimenti disconnected
        Point pole = new Point(0.0, 0.0);
        Ceil el = null;
        CeilEvent ce = null;
        for(int s = 0; s < 5; s++) {
            for(int i=0; i < ceilList.size(); i++) {
                el  = ceilList.get(0);
                ce = new CeilEvent();
                ce.setCeilReference(el.getName());
                ce.setTime(ts);
                if(el.evalForcewithRadius(pole) > 0) {
                    ce.setStatus(CeilEvent.Connection.CONNECTED);
                } else {
                    ce.setStatus(CeilEvent.Connection.DISCONNECTED);
                }
                ceilEvents.add(ce);
            }
            ts = Timestamp.from(ts.toInstant().plus(1, ChronoUnit.SECONDS));
            pole.setLatitude(pole.getLatitude()+0.01);
            pole.setLongitude(pole.getLongitude()+0.01);

        }
        return ceilEvents;
    }
}