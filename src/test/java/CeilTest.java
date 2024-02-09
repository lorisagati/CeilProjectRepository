import org.ceil.model.Ceil;
import org.ceil.model.Point;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CeilTest {

    @Test
    void testCreateCell() {
        Point center = new Point( 0.25, 0.25 );
        Point expected = new Point( 0.25, 0.25 );
        Ceil ceil1 = new Ceil("Ceil1" , center , 10 , 10 );
        assertEquals("Ceil1" , ceil1.getName() );
        assertEquals( expected.getLatitude() , center.getLatitude() );
        assertEquals( expected.getLongitude() , center.getLongitude() );
        assertEquals( 10 , ceil1.getRadius() );
        assertEquals( 10 , ceil1.getPower() );
    }

    @Test
    void getDistanceFromCenterTest() {
        Point center = new Point( 0.0 , 0.0 );
        Point reference = new Point( 0.25, 0.25 );
        Ceil ceil1 = new Ceil("Ceil1" , center , 10 , 0 );
        double distance = ceil1.getDistanceFromCenter(reference);
        // reference value calculated from the Latitude/Longitude Distance Calculator
        // at https://www.nhc.noaa.gov/gccalc.shtml
        assertEquals( 39.0, distance);
    }

    @Test
    void evalForcewithRadiusBeyondBorderTest() {
        Point center = new Point( 0.0 , 0.0 );
        Point reference = new Point( 0.00, 0.10 );
        Ceil ceil1 = new Ceil("Ceil1" , center , 10 , 0 );
        //
        double distance = ceil1.getDistanceFromCenter(reference);
        // reference value calculated from the Latitude/Longitude Distance Calculator
        // at https://www.nhc.noaa.gov/gccalc.shtml
        //distance of 11 km from center
        double force = ceil1.evalForcewithRadius(reference);
        assertEquals( 0 , force );
    }

    @Test
    void evalForcewithRadiusOnBorderTest() {
        Point center = new Point( 0.0 , 0.0 );
        Point reference = new Point( 0.00, 0.09 );
        Ceil ceil1 = new Ceil("Ceil1" , center , 10 , 0 );
        double distance = ceil1.getDistanceFromCenter(reference);
        // reference value calculated from the Latitude/Longitude Distance Calculator
        // at https://www.nhc.noaa.gov/gccalc.shtml
        //distance of 10 km from center = Radius => Force 0 on the border
        double force = ceil1.evalForcewithRadius(reference);
        assertEquals( 0 , force );
    }

    @Test
    void evalForcewithRadiusWithinBorderTest() {
        Point center = new Point( 0.0 , 0.0 );
        Point reference = new Point( 0.00, 0.045 );
        Ceil ceil1 = new Ceil("Ceil1" , center , 10 , 0 );
        double distance = ceil1.getDistanceFromCenter(reference);
        // reference value calculated from the Latitude/Longitude Distance Calculator
        // at https://www.nhc.noaa.gov/gccalc.shtml
        //distance of 5 km from center = Radius => Force 50 % on the border
        double force = ceil1.evalForcewithRadius(reference);
        assertEquals( 50 , force );
    }

    @Test
    void evalForcewithRadiusOnCenterTest() {
        Point center = new Point( 0.0 , 0.0 );
        Point reference = new Point( 0.00, 0.0 );
        Ceil ceil1 = new Ceil("Ceil1" , center , 10 , 0 );
        double distance = ceil1.getDistanceFromCenter(reference);
        // reference value calculated from the Latitude/Longitude Distance Calculator
        // at https://www.nhc.noaa.gov/gccalc.shtml
        //distance of 0 km from center = Radius => Force 100 % on the border
        double force = ceil1.evalForcewithRadius(reference);
        assertEquals( 100 , force );
    }

    @Test
    void evalForceWithPowerOverLimitTest() {
        Point center = new Point( 0.0 , 0.0 );
        //20 km of linear distance from the Center of the Ceil
        Point reference = new Point( 0.00, 0.18 );
        Ceil ceil1 = new Ceil("Ceil1" , center , 10 , 1.25 );
        //The Force calculated with the data: F = 1/(1.25)exp20 => 0.011529. Considered 0 % of Force interaction with the Center
        double forceCalc = ceil1.evalForceWithPower(reference);
        assertEquals( 0.0 , forceCalc );
    }

    @Test
    void evalForceWithPowerWithinLimitTest() {
        Point center = new Point( 0.0 , 0.0 );
        //3.1 km of linear distance from the Center of the Ceil
        Point reference = new Point( 0.00, 0.01 );
        Ceil ceil1 = new Ceil("Ceil1" , center , 10 , 1.25 );
        //The Force calculated with the data: F = 1/(1.25)exp20 => 0.0500. 50,0 % of Force interaction with the Center
        double forceCalc = ceil1.evalForceWithPower(reference);
        assertEquals( 80.0 , forceCalc );
    }

    @Test
    void filterUnorderedListTest() {
        Point reference = new Point( 0.00, 0.00 );
        List<Ceil> unorderedCeils =createUnorderedList();
        int originalSize = unorderedCeils.size();
        //we expect that 2 elements are under the threshold
        int filteredNumber = (int) unorderedCeils
                .stream()
                .filter(el -> el.evalForcewithRadius(reference) > 50).count();
        assertEquals( 2, originalSize - filteredNumber);
    }

    @Test
    void orderFilteredListTest() {

        Point reference = new Point( 0.00, 0.00 );
        List<Ceil> unorderedList = createUnorderedList();
        // 1 : 90 %
        double f1 = unorderedList.get(0).evalForcewithRadius(reference);
        // 2 : 40 %
        double f2 = unorderedList.get(1).evalForcewithRadius(reference);
        // 3 : 70 %
        double f3 = unorderedList.get(2).evalForcewithRadius(reference);
        // 4 : 30 %
        double f4 = unorderedList.get(3).evalForcewithRadius(reference);
        // 5 : 90 %
        double f5 = unorderedList.get(4).evalForcewithRadius(reference);

        //we expect that the unordered, filtered list has the following elements in this order
        // 1 : 90 %
        // 3 : 70 %
        // 5 : 90 %

        List<Ceil> orderedList = unorderedList
                .stream()
                .filter(el -> el.evalForcewithRadius(reference) > 50)
                .sorted(Comparator.comparing(Ceil::getForce).reversed()).toList();
        assertEquals( "Ceil1" , orderedList.get(0).getName());
        assertEquals( "Ceil5" , orderedList.get(1).getName());
        assertEquals( "Ceil3" , orderedList.get(2).getName());

        assertEquals( 90 , orderedList.get(0).getForce());
        assertEquals( 90 , orderedList.get(1).getForce());
        assertEquals( 70 , orderedList.get(2).getForce());
    }

    private List<Ceil> createUnorderedList() {
        List<Ceil> unorderedCeils = new ArrayList<Ceil>();

        // 1 km distance from 0.0  (9/10) * 100 => 90 %
        Point center1 = new Point( 0.0 , 0.01 );
        Ceil ceil1 = new Ceil("Ceil1" , center1 , 10 , 1.25 );
        unorderedCeils.add(ceil1);

        //6 km distance from 0.0 ((10-6)/10) => 40 %  under threshold of 50 %
        Point center2 = new Point( 0.0 , 0.05 );
        Ceil ceil2 = new Ceil("Ceil2" , center2 , 10 , 1.25 );
        unorderedCeils.add(ceil2);

        //3 km distance from 0.0 ((10-3)/10)*100 = 70 %
        Point center3 = new Point( 0.0 , 0.025 );
        Ceil ceil3 = new Ceil("Ceil3" , center3 , 10 , 1.25 );
        unorderedCeils.add(ceil3);

        //7 km distance from 0.0 ((10-7)/10)*100 = 30 % => under threshold of 50 %
        Point center4 = new Point( 0.0 , 0.06 );
        Ceil ceil4 = new Ceil("Ceil4" , center4 , 10 , 1.25 );
        unorderedCeils.add(ceil4);

        // 1 km distance from 0.0  (9/10) * 100 => 90 %
        Point center5 = new Point( 0.0 , 0.01 );
        Ceil ceil5 = new Ceil("Ceil5" , center5 , 10 , 1.25 );
        unorderedCeils.add(ceil5);

        return unorderedCeils;
    }

}
