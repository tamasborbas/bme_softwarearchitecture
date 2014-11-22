package hu.bme.aut.monopoly.rest;

import hu.bme.aut.monopoly.model.MonopolyEntityManager;
import hu.bme.aut.monopoly.model.UserType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.JResponse;


@Path("/userapi")
public class UserApi
{
    @Path("/x")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayPlainTextHello()
    {
        return "Hello Jersey";
    }

    @Path("/Login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public HttpServletResponse login(String json, @Context
    HttpServletRequest request, @Context
    HttpServletResponse response)
    {
        System.out.println("Login start: " + json + "itt");

        JSONArray jsonTomb;
        String userName = null;
        String password = null;
        try
        {
            jsonTomb = new JSONArray(json);
            userName = jsonTomb.getJSONObject(0).getString("userName");
            password = jsonTomb.getJSONObject(0).getString("password");
        } catch (JSONException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("loggedInUser", "");

        String passwordHash = "";

        // MD5 hash alkalmazása a jelszora
        passwordHash = encodePassword(password);

        System.out.println(userName + " - " + passwordHash);
        MonopolyEntityManager mem = new MonopolyEntityManager();
        mem.initDB();

        if (mem.getUserIsRegistered(userName, passwordHash))
        {
            session.setAttribute("loggedInUser", mem.getUserByName(userName).getEmail());

            System.out.println("Login okay");
        } else
        {
            System.out.println("Login NOT okay");
        }

        mem.closeDB();
        return response;
    }

    @Path("/Logout")
    @GET
    public HttpServletResponse logout(@Context
    HttpServletRequest request, @Context
    HttpServletResponse response)
    {
        System.out.println("logout");
        HttpSession session = request.getSession(true);
        session.invalidate();

        System.out.println("logout");
        return response;
    }

    @Path("/Registration")
    @POST
    public JResponse<JSONObject> addUser(String json, @Context
    HttpServletRequest request, @Context
    HttpServletResponse response) throws JSONException
    {

        // session, helyes regisztracional be is leptetjük.
        HttpSession session = request.getSession(true);
        session.setAttribute("loggedInUser", "");

        JSONArray jsonTomb;
        String email = null;
        String password = null;
        String name = null;
        try
        {

            jsonTomb = new JSONArray(json);
            email = jsonTomb.getJSONObject(0).getString("email");
            password = jsonTomb.getJSONObject(0).getString("password");
            name = jsonTomb.getJSONObject(0).getString("name");
        } catch (JSONException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String passwordHash = null;
        passwordHash = encodePassword(password);

        int errorCode = 0;

        if (null != email && null != passwordHash && null != name)
        {
            MonopolyEntityManager mem = new MonopolyEntityManager();

            try
            {
                mem.initDB();
                if (mem.isUserEmailRegistered(email))
                {
                    errorCode = 2;
                } else if (mem.isUserNameRegistered(name))
                {
                    errorCode = 1;
                } else
                {
                    mem.addNewUser(email, passwordHash, name, UserType.user);
                    mem.closeDB();
                }

            } catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mem = new MonopolyEntityManager();
            mem.initDB();
            if (mem.getUserIsRegistered(email, passwordHash))
            {
                session.setAttribute("loggedInUser", email);
            }
            mem.closeDB();
        }

        JSONObject responseJsonObject = new JSONObject();
        responseJsonObject.put("errorCode", errorCode);

        return JResponse.ok(responseJsonObject).build();
    }

    @Path("/Remind")
    @POST
    public JResponse<JSONObject> remind(String json) throws JSONException
    {
        boolean success = true;

        // TODO reminder

        JSONObject responseJsonObject = new JSONObject();
        try
        {
            responseJsonObject.put("success", success);
        } catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return JResponse.ok(responseJsonObject).build();
    }

    private String encodePassword(String password)
    {
        String passwordHash = null;

        try
        {
            byte[] bytesOfMessage = null;

            bytesOfMessage = password.getBytes("UTF-8");

            MessageDigest md;
            md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytesOfMessage);
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);

            passwordHash = hashtext;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return passwordHash;
    }

}
