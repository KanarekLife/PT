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
        var tower1 = new Tower("Orthanc", 100);
        tower1.addMage(mage1);
        mage1.setTower(tower1);
        tower1.addMage(mage2);
        mage2.setTower(tower1);
        var tower2 = new Tower("Minas Morgul", 100);
        session.persist(mage1);
        session.persist(mage2);
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
                System.out.println("strong-mages - list all mages with level > 50");
                System.out.println("exit - exit the program");
                var command = scanner.nextLine();
                if (command.equals("exit")) {
                    shouldRun = false;
                }else if (command.equals("list")) {
                    var session = sessionFactory.openSession();
                    var mages = session.createQuery("from Mage", Mage.class).list();
                    var towers = session.createQuery("from Tower", Tower.class).list();
                    System.out.println("Mages: ");
                    mages.forEach(System.out::println);
                    System.out.println("Towers: ");
                    towers.forEach(System.out::println);
                    session.close();
                } else if (command.equals("add-mage")) {
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
                } else if (command.equals("add-tower")) {
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
                } else if (command.equals("add-mage-to-tower")) {
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
                } else if (command.equals("remove-mage-from-tower")) {
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
                } else if (command.equals("strong-mages")){
                    var session = sessionFactory.openSession();
                    session.beginTransaction();
                    session.createQuery("from Mage where level > 50", Mage.class).list().forEach(System.out::println);
                }
                else {
                    System.out.println("Unknown command");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}