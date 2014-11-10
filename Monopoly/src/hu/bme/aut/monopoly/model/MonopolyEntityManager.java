package hu.bme.aut.monopoly.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;


public class MonopolyEntityManager
{

    private static final String PERSISTENCE_UNIT_NAME = "Monopoly";
    private EntityManagerFactory emf;
    private EntityManager em;

    public void initDB()
    {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = emf.createEntityManager();
    }

    public void closeDB()
    {
        em.close();
    }

    public MonopolyEntityManager(EntityManager em)
    {
        this.em = em;
    }

    public MonopolyEntityManager()
    {}

    public void commit(Object o) throws Exception
    {
        em.getTransaction().begin();
        try
        {
            em.persist(o);
            em.getTransaction().commit();
        } catch (Exception e)
        {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public boolean getUserIsRegistered(String email, String hash)
    {

        Query q = em.createNamedQuery("User.getUserByEmailAndPass");
        q.setParameter("emailPattern", email);
        q.setParameter("passHashPattern", hash);
        List<User> result = q.getResultList();
        if (result.size() == 0)
        {
            return false;
        } else
        {
            return true;
        }
    }

    public User getUserByEmail(String email)
    {

        Query q = em.createNamedQuery("User.getUserByEmail");
        q.setParameter("emailPattern", email);

        List<User> result = q.getResultList();
        if (result.size() == 0)
        {
            return null;
        } else
        {
            return result.get(0);
        }
    }

    public void addNewUser(String email, String passwordHash, String name, UserType userType) throws Exception
    {

        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setPasswordHash(passwordHash);
        u.setUserType(userType);

        commit(u);
    }

    public List<Game> getActiveGamesByEmail(String email)
    {
        User user = getUserByEmail(email);

        Query q = em.createNamedQuery("Game.getActiveGamesByEmail");
        q.setParameter("userPattern", user);
        List<Game> result = q.getResultList();

        return result;

    }

    public boolean isUserNameRegistered(String name)
    {
        Query q = em.createNamedQuery("User.getUserByName");
        q.setParameter("namePattern", name);

        List<User> result = q.getResultList();
        if (result.size() == 0)
        {
            return false;
        } else
        {
            return true;
        }
    }

    public boolean isUserEmailRegistered(String email)
    {
        Query q = em.createNamedQuery("User.getUserByEmail");
        q.setParameter("emailPattern", email);

        List<User> result = q.getResultList();
        if (result.size() == 0)
        {
            return false;
        } else
        {
            return true;
        }
    }

    public Game getGameById(int id)
    {

        Query q = em.createNamedQuery("Game.getGameById");
        q.setParameter("idPattern", id);

        List<Game> result = q.getResultList();
        if (result.size() == 0)
        {
            return null;
        } else
        {
            return result.get(0);
        }
    }

    // TESTS
    public void listAndCreatePlayerTest()
    {

        Query q2 = em.createQuery("select p from Player p");
        List<Player> playerList = q2.getResultList();
        for (Player p : playerList)
        {
            System.out.println(p);
        }
        System.out.println("Size: " + playerList.size());

        // create new Player
        em.getTransaction().begin();
        Player p = new Player();

        p.setMoney(12);
        em.persist(p);
        em.getTransaction().commit();

    }

    public void addNewPlayerTest()
    {

        User user = getUserByEmail("admin@gmail.com");
        // System.out.println(user.getEmail() + " - " + user.getName());

        Player player = new Player();
        // TODO erteket kitalalni
        player.setMoney(10000);

        try
        {
            commit(player);
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        user.addGamePlayer(player);
        try
        {
            commit(user);
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
