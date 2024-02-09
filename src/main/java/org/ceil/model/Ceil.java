package org.ceil.model;

import lombok.Getter;

@Getter
public class Ceil {

    private String name;
    private Point center;
    private double radius;
    private double power;
    private double force;

    public Ceil(String name, Point center, double radius, double power) {
        this.name = name;
        this.center = center;
        this.radius = radius;
        this.power = power;
    }

    public double getDistanceFromCenter(Point point) {
        //distance from angular to linear using haversin formula
        double theta = point.getLongitude() - this.center.getLongitude();
        double distance = 60 * 1.1515 * (180 / Math.PI) * Math.acos(
                Math.sin(point.getLatitude() * (Math.PI / 180)) * Math.sin(center.getLatitude() * (Math.PI / 180)) +
                        Math.cos(point.getLatitude() * (Math.PI / 180)) * Math.cos(center.getLatitude() * (Math.PI / 180)) * Math.cos(theta * (Math.PI / 180))
        );
        //unit of distance is kilometers, rounded to the next int value
        return Math.round(distance * 1.609344);
    }

    public double evalForcewithRadius(Point point) {
        double distance = getDistanceFromCenter(point);
        double force = 0.0;

        if(distance < this.radius) {
            // Force percentage evaluate by the distance of the point respect to the ceil center
            force = Math.round((this.radius - distance)/this.radius*100);
        }
        return this.force = force;
    }
    public double evalForceWithPower(Point point) {
        //F = 1/(Power)exp(Distance);
        double force = 0;
        double distance = getDistanceFromCenter(point);
        force = (1/Math.pow(power,distance)) ;
        if(force > 0.01153) {
            if(force <= 0.8) {
                // Force in percentage respect to the center
                force = force*100;
            } else force = 100.0;
            //within a very short distance of 1 km from the center
            // and a calculated value of more than 0.8
            // we consider the interaction with center of a 100 % Force
        }  else force = 0.0; // under a threshold of 0.01 calculated value we consider no interaction with the center
        return this.force = force;
   }

}
