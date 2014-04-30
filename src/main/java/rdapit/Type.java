/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rdapit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Class for instances of the middle layer of the structural hierarchy (Properties - Types - Profiles).
 * 
 */
public class Type {

    private ArrayList<Property<?>> properties;
    private PID pid;

    public Type(ArrayList<Property<?>> properties, PID pid) {
        this.properties = properties;
        this.pid = pid;
    }

    public Type(PID pid) {
        this.pid = pid;
    }

    public Iterator<Property<?>> getPropertyIterator() {
        return properties.iterator();
    }

    public void addAllProperties(Collection<? extends Property<?>> properties) {
        this.properties.addAll(properties);
    }

    public PID getPid() {
        return pid;
    }        

}
