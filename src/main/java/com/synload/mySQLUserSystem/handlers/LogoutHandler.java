package com.synload.mySQLUserSystem.handlers;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Failed;
import com.synload.framework.elements.Success;
import com.synload.framework.ws.annotations.WSEvent;
import com.synload.mySQLUserSystem.model.User;

public class LogoutHandler {
	@WSEvent(name= "Logout", description= "handle a user logout request", method="get", action="logout", enabled=true)
    public void getLogout(RequestEvent event){
		try {
			if (event.getSession().getSessionData().containsKey("graphUser")) {
				((User)event.getSession().getSessionData().get("graphUser")).deleteUserSession(
						String.valueOf(event.getSession().session.getUpgradeRequest().getHeader("X-Real-IP")),
						event.getRequest().getData().get("sessionid")
				);
				event.getSession().getSessionData().remove("graphUser");
				event.getSession().send(SynloadFramework.ow.writeValueAsString(new Success("logout")));
			} else {
				event.getSession().send(SynloadFramework.ow.writeValueAsString(new Failed("logout")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
