package com.synload.mySQLUserSystem.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Failed;
import com.synload.framework.elements.LoginBox;
import com.synload.framework.elements.Success;
import com.synload.framework.ws.annotations.WSEvent;
import com.synload.mySQLUserSystem.Authentication;
import com.synload.mySQLUserSystem.model.User;

public class LoginHandler {
	@WSEvent(name = "user login", description = "handle user logins", method="get", action="login", enabled=true)
    public void getLoginBox(RequestEvent event) throws JsonProcessingException, IOException {
        event.getSession().send(SynloadFramework.ow.writeValueAsString(new LoginBox(event.getRequest().getTemplateCache())));
    }
	@WSEvent(name= "Login", description= "another login handler", method="action", action="login", enabled=true)
    public void getLogin(RequestEvent event) throws JsonProcessingException, IOException {
        User authedUser = Authentication.login(event.getSession(), event.getRequest().getData().get("username").toLowerCase(), event.getRequest().getData().get("password"));
        if (authedUser != null) {
            String uuid = UUID.randomUUID().toString();
            event.getSession().getSessionData().put("graphUser", authedUser);
            try {
				((User)event.getSession().getSessionData().get("graphUser")).saveUserSession(String.valueOf(event.getSession().session.getUpgradeRequest().getHeader("X-Real-IP")), uuid);
			} catch (Exception e) {
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
            event.getSession().send(SynloadFramework.ow.writeValueAsString(authResponse));
        } else {
            event.getSession().send(SynloadFramework.ow.writeValueAsString(new Failed("login")));
        }
    }
	@WSEvent(name = "Session Login", description = "handle already logged in users", method="get", action="sessionlogin", enabled=true)
    public void getSessionLogin(RequestEvent event)
            throws JsonProcessingException, IOException {
        User authedUser = Authentication.session(event.getSession(), String.valueOf(event.getSession().session.getUpgradeRequest().getHeader("X-Real-IP")), event.getRequest().getData().get("sessionid"));
        if (authedUser != null) {
        	event.getSession().getSessionData().put("graphUser", authedUser);
            Success authResponse = new Success("session");
            Map<String, String> userData = new HashMap<String, String>();
            userData.put("id", String.valueOf(((User)event.getSession().getSessionData().get("graphUser")).getId()));
            userData.put("session", event.getRequest().getData().get("sessionid"));
            if (authedUser.getFlags() != null) {
                userData.put(
                    "flags",
                    SynloadFramework.ow.writeValueAsString(((User)event.getSession().getSessionData().get("graphUser")).getFlags())
                );
            }
            userData.put("name", ((User)event.getSession().getSessionData().get("graphUser")).getUsername());
            authResponse.setData(userData);
            event.getSession().send(SynloadFramework.ow.writeValueAsString(authResponse));
        } else {
            event.getSession().send(SynloadFramework.ow.writeValueAsString(new Failed("session")));
        }
    }
}
