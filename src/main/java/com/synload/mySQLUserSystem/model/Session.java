package com.synload.mySQLUserSystem.model;

import com.synload.framework.modules.annotations.sql.BigIntegerColumn;
import com.synload.framework.modules.annotations.sql.HasOne;
import com.synload.framework.modules.annotations.sql.SQLTable;
import com.synload.framework.modules.annotations.sql.StringColumn;
import com.synload.framework.sql.Model;

@SQLTable(name = "Session Model", version = 1.0, description = "keeps login data")
public class Session extends Model {
    @BigIntegerColumn(length = 20, AutoIncrement=true, Key=true)
    public long id;

    @StringColumn(length = 255)
    public String ip;

    @StringColumn(length = 128)
    public String session;

    @HasOne(key="id", of=User.class)
    @BigIntegerColumn(length = 20)
    public long user;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public long getUser() {
		return user;
	}

	public void setUser(long user) {
		this.user = user;
	}
    
}
