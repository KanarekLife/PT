package org.example.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Mage {
    @Id
    private String name;
    private int level;
    @ManyToOne
    private Tower tower;

    public Mage(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public Mage() {

    }

    public void setTower(Tower tower) {
        this.tower = tower;
    }

    @Override
    public String toString() {
        return "Mage{" +
                "name='" + name + '\'' +
                ", level=" + level +
                ", tower=" + (tower == null ? "NULL" : tower.getName()) +
                '}';
    }
}
