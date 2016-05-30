package com.synload.mySQLUserSystem;

import com.synload.framework.Log;
import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.annotations.Module;

@Module(author="Nathaniel Davidson", depend = { "" }, log = Module.LogLevel.INFO, name="MySQLUserSystem", version="1.1")
public class MySQLUserSystem extends ModuleClass {

	@Override
	public void initialize() {
		Log.info("Loaded MySQL User Database", MySQLUserSystem.class);
	}

	@Override
	public void crossTalk(Object... obj) {
		
	}

}
