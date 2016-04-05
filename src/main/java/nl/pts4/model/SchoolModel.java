package nl.pts4.model;

import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class SchoolModel {
    int id;
    String name;
    String location;
    String country;

    public SchoolModel(int id, String name, String location, String country) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString(){
        return id + " - " + name;
    }
}
