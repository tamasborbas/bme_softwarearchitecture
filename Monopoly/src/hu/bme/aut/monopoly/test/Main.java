package hu.bme.aut.monopoly.test;

import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.User;


public class Main
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();

        // uj user hozzaadasa
        try
        {
            // mem.addNewUser("anna@gmail.com", "a70f9e38ff015afaa9ab0aacabee2e13", "anna", UserType.user);
            // mem.addNewUser("admin@gmail.com", "21232f297a57a5a743894a0e4a801fc3", "admin", UserType.user);
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // user lekerdezese nev alapjan

//        User user = mem.getUserByEmail("admin@gmail.com");
//        System.out.println(user.getEmail() + " - " + user.getName());
//
//        mem.listAndCreatePlayerTest();
//        mem.addNewPlayerTest();

        mem.closeDB();

    }

}
