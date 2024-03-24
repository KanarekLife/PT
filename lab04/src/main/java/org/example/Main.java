package org.example;

import org.example.models.Mage;
import org.example.models.Tower;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;

public class Main {

    private static SessionFactory getHibernateSessionFactory(StandardServiceRegistry registry) {
        return new MetadataSources(registry)
                .addAnnotatedClasses(Mage.class, Tower.class)
                .buildMetadata()
                .buildSessionFactory();
    }

    private static void seedData(SessionFactory sessionFactory) {
        var session = sessionFactory.openSession();
        session.beginTransaction();
        var mage1 = new Mage("Gandalf", 100);
        var mage2 = new Mage("Saruman", 100);
        var mage3 = new Mage("John", 2137);
        var mage4 = new Mage("Mike", 10);
        var tower1 = new Tower("Orthanc", 100);
        var tower2 = new Tower("Minas Morgul", 100);
        tower1.addMage(mage1);
        mage1.setTower(tower1);
        tower1.addMage(mage2);
        mage2.setTower(tower1);
        tower1.addMage(mage4);
        mage4.setTower(tower1);
        tower2.addMage(mage3);
        mage3.setTower(tower2);
        session.persist(mage1);
        session.persist(mage2);
        session.persist(mage3);
        session.persist(mage4);
        session.persist(tower1);
        session.persist(tower2);
        session.getTransaction().commit();
        session.close();
    }

    public static void main(String[] args) {
        final var registry = new StandardServiceRegistryBuilder()
                .build();
        try (var sessionFactory = getHibernateSessionFactory(registry)){
            seedData(sessionFactory);

            var shouldRun = true;
            var scanner = new java.util.Scanner(System.in);
            while(shouldRun) {
                System.out.println("Enter a command: ");
                System.out.println("list - list all mages and towers");
                System.out.println("add-mage - add a mage");
                System.out.println("add-tower - add a tower");
                System.out.println("add-mage-to-tower - add a mage to a tower");
                System.out.println("remove-mage-from-tower - remove a mage from a tower");
                System.out.println("remove-mage - remove a mage");
                System.out.println("remove-tower - remove a tower");
                System.out.println("strong-mages-from-tower - list all mages with level > 50 from tower");
                System.out.println("exit - exit the program");
                var command = scanner.nextLine();
                switch (command) {
                    case "exit" -> shouldRun = false;
                    case "list" -> {
                        var session = sessionFactory.openSession();
                        var mages = session.createQuery("from Mage", Mage.class).list();
                        var towers = session.createQuery("from Tower", Tower.class).list();
                        System.out.println("Mages: ");
                        mages.forEach(System.out::println);
                        System.out.println("Towers: ");
                        towers.forEach(System.out::println);
                        session.close();
                    }
                    case "add-mage" -> {
                        var session = sessionFactory.openSession();
                        session.beginTransaction();
                        System.out.println("Enter mage name: ");
                        var mageName = scanner.nextLine();
                        System.out.println("Enter mage level: ");
                        var mageLevel = scanner.nextInt();
                        scanner.nextLine();
                        var mage = new Mage(mageName, mageLevel);
                        session.persist(mage);
                        session.getTransaction().commit();
                        session.close();
                    }
                    case "add-tower" -> {
                        var session = sessionFactory.openSession();
                        session.beginTransaction();
                        System.out.println("Enter tower name: ");
                        var towerName = scanner.nextLine();
                        System.out.println("Enter tower height: ");
                        var towerHeight = scanner.nextInt();
                        scanner.nextLine();
                        var tower = new Tower(towerName, towerHeight);
                        session.persist(tower);
                        session.getTransaction().commit();
                        session.close();
                    }
                    case "add-mage-to-tower" -> {
                        var session = sessionFactory.openSession();
                        session.beginTransaction();
                        System.out.println("Enter mage name: ");
                        var mageName = scanner.nextLine();
                        var mage = session.get(Mage.class, mageName);
                        System.out.println("Enter tower name: ");
                        var towerName = scanner.nextLine();
                        var tower = session.get(Tower.class, towerName);
                        tower.addMage(mage);
                        mage.setTower(tower);
                        session.persist(tower);
                        session.getTransaction().commit();
                        session.close();
                    }
                    case "remove-mage-from-tower" -> {
                        var session = sessionFactory.openSession();
                        session.beginTransaction();
                        System.out.println("Enter mage name: ");
                        var mageName = scanner.nextLine();
                        var mage = session.get(Mage.class, mageName);
                        System.out.println("Enter tower name: ");
                        var towerName = scanner.nextLine();
                        var tower = session.get(Tower.class, towerName);
                        tower.removeMage(mage);
                        mage.setTower(null);
                        session.persist(tower);
                        session.getTransaction().commit();
                        session.close();
                    }
                    case "remove-mage" -> {
                        System.out.println("Enter mage name: ");
                        var mageName = scanner.nextLine();
                        var session = sessionFactory.openSession();
                        session.beginTransaction();
                        var mage = session.get(Mage.class, mageName);
                        var tower = mage.getTower();
                        if (tower != null) {
                            tower.removeMage(mage);
                            session.persist(tower);
                        }
                        session.remove(mage);
                        session.getTransaction().commit();
                        session.close();
                    }
                    case "remove-tower" -> {
                        System.out.println("Enter tower name: ");
                        var towerName = scanner.nextLine();
                        var session = sessionFactory.openSession();
                        session.beginTransaction();
                        var tower = session.get(Tower.class, towerName);
                        for (var mage: tower.getMages()) {
                            mage.setTower(null);
                            session.persist(mage);
                        }
                        session.remove(tower);
                        session.getTransaction().commit();
                        session.close();
                    }
                    case "strong-mages-from-tower" -> {
                        System.out.println("Enter tower name: ");
                        var towerName = scanner.nextLine();
                        System.out.println("Min level:");
                        var level = Integer.parseInt(scanner.nextLine());
                        var session = sessionFactory.openSession();
                        session.beginTransaction();
                        var tower = session.get(Tower.class, towerName);
                        session.createQuery("from Mage where level > :level and tower = :searched_tower", Mage.class)
                                .setParameter("level", level)
                                .setParameter("searched_tower", tower)
                                .list()
                                .forEach(System.out::println);
                        session.close();
                    }
                    default -> System.out.println("Unknown command");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}