package org.example.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.LinkedList;
import java.util.List;

@Entity
public class Tower {
    @Id
    private String name;
    private int height;
    @OneToMany
    private List<Mage> mages;

    public Tower(String name, int height) {
        this.name = name;
        this.height = height;
        this.mages = new LinkedList<>();
    }

    public Tower() {

    }

    public void addMage(Mage mage) {
        this.mages.add(mage);
    }

    public void removeMage(Mage mage) {
        this.mages.remove(mage);
    }

    public List<Mage> getMages() {
        return this.mages;
    }

    public String getName() {
        return this.name;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public String toString() {
        return "Tower{" +
                "name='" + name + '\'' +
                ", height=" + height +
                ", mages=" + mages +
                '}';
    }
}
