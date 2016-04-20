package com.synload.mySQLUserSystem.handlers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Failed;
import com.synload.framework.elements.LoginBox;
import com.synload.framework.elements.Success;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.mySQLUserSystem.Authentication;
import com.synload.mySQLUserSystem.model.User;

public class LoginHandler {
	@Event(name = "user login", description = "handle user logins", trigger = { "get", "login" }, type = Type.WEBSOCKET)
    public void getLoginBox(RequestEvent event) throws JsonProcessingException,
            IOException {
        event.getSession().send(
                SynloadFramework.ow.writeValueAsString(new LoginBox(event
                        .getRequest().getTemplateCache())));
    }
	@Event(name = "Login", description = "another login handler", trigger = { "action", "login" }, type = Type.WEBSOCKET)
    public void getLogin(RequestEvent event) throws JsonProcessingException,
            IOException {
        User authedUser = Authentication.login(event.getSession(), event.getRequest().getData()
                .get("username").toLowerCase(), event.getRequest().getData()
                .get("password"));
        if (authedUser != null) {
            String uuid = UUID.randomUUID().toString();
            event.getSession().getSessionData().put("graphUser", authedUser);
            try {
				((User)event.getSession().getSessionData().get("graphUser"))
				        .saveUserSession(
				                String.valueOf(event.getSession().session
				                        .getUpgradeRequest().getHeader("X-Real-IP")),
				                uuid);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Success authResponse = new Success("login");
            Map<String, String> userData = new HashMap<String, String>();
            userData.put("id",
                    String.valueOf(((User)event.getSession().getSessionData().get("graphUser")).getId()));
            userData.put("session", uuid);
            if (authedUser.getFlags() != null) {
                userData.put("flags", SynloadFramework.ow
                        .writeValueAsString(authedUser.getFlags()));
            }
            userData.put("name", ((User)event.getSession().getSessionData().get("graphUser")).getUsername());
            authResponse.setData(userData);
            event.getSession().send(
                    SynloadFramework.ow.writeValueAsString(authResponse));
        } else {
            event.getSession()
                    .send(SynloadFramework.ow.writeValueAsString(new Failed(
                            "login")));
        }
    }
	@Event(name = "Session Login", description = "handle already logged in users", trigger = { "get", "sessionlogin" }, type = Type.WEBSOCKET)
    public void getSessionLogin(RequestEvent event)
            throws JsonProcessingException, IOException {
        User authedUser = Authentication.session(event.getSession(), String.valueOf(event
                .getSession().session.getUpgradeRequest()
                .getHeader("X-Real-IP")),
                event.getRequest().getData().get("sessionid"));
        if (authedUser != null) {
        	event.getSession().getSessionData().put("graphUser", authedUser);
            Success authResponse = new Success("session");
            Map<String, String> userData = new HashMap<String, String>();
            userData.put("id",
                    String.valueOf(((User)event.getSession().getSessionData().get("graphUser")).getId()));
            userData.put("session",
                    event.getRequest().getData().get("sessionid"));
            if (authedUser.getFlags() != null) {
                userData.put(
                    "flags",
                    SynloadFramework.ow.writeValueAsString(((User)event.getSession().getSessionData().get("graphUser")).getFlags())
                );
            }
            userData.put("name", ((User)event.getSession().getSessionData().get("graphUser")).getUsername());
            authResponse.setData(userData);
            event.getSession().send(
                    SynloadFramework.ow.writeValueAsString(authResponse));
        } else {
            event.getSession().send(
                    SynloadFramework.ow
                            .writeValueAsString(new Failed("session")));
        }
    }
}
