package com.synload.mySQLUserSystem.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Failed;
import com.synload.framework.elements.RegisterBox;
import com.synload.framework.elements.Success;
import com.synload.framework.ws.annotations.WSEvent;
import com.synload.mySQLUserSystem.Authentication;

public class RegisterHandler {
	@WSEvent(name="User Register", description="handle user registrations", method="get", action="register", enabled=true)
    public void getRegisterBox(RequestEvent event)
            throws JsonProcessingException, IOException {
        event.getSession().send(
                SynloadFramework.ow.writeValueAsString(new RegisterBox(event
                        .getRequest().getTemplateCache())));
    }
    @WSEvent(name = "Register", description = "", method="action", action="register", enabled=true)
    public void getRegister(RequestEvent event) throws JsonProcessingException, IOException {
        List<String> flags = new ArrayList<String>();
        flags.add("r");
        boolean authedUser = Authentication.create(event.getRequest().getData()
                .get("username").toLowerCase(), event.getRequest().getData()
                .get("password"), event.getRequest().getData().get("email"),
                flags, 0);
        if (authedUser) {
            event.getSession().send(SynloadFramework.ow.writeValueAsString(new Success("register")));
            // Should redirect to login box!
        } else {
            event.getSession().send(SynloadFramework.ow.writeValueAsString(new Failed("register")));
        }
    }
}
