package com.synload.mySQLUserSystem.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Failed;
import com.synload.framework.elements.Success;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.mySQLUserSystem.sql.User;

public class LogoutHandler {
	@Event(name = "Logout", description = "handle a user logout request", trigger = { "get", "logout" }, type = Type.WEBSOCKET)
    public void getLogout(RequestEvent event) throws JsonProcessingException,
            IOException {
        if (event.getSession().getSessionData().containsKey("graphUser")) {
            try {
				((User)event.getSession().getSessionData().get("graphUser"))
				        .deleteUserSession(
				                String.valueOf(event.getSession().session
				                        .getUpgradeRequest().getHeader("X-Real-IP")),
				                event.getRequest().getData().get("sessionid"));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            event.getSession().getSessionData().remove("graphUser");
            event.getSession().send(
                    SynloadFramework.ow
                            .writeValueAsString(new Success("logout")));
        } else {
            event.getSession().send(
                    SynloadFramework.ow
                            .writeValueAsString(new Failed("logout")));
        }
    }
}
