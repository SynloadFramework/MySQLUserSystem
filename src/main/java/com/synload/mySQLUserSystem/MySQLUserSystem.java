package com.synload.mySQLUserSystem;

import com.synload.framework.Log;
import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.annotations.Module;

@Module(author="Nathaniel Davidson", name="MySQLUserSystem", version="1.0")
public class MySQLUserSystem extends ModuleClass {

	@Override
	public void initialize() {
		Log.info("Loaded MySQL User Database", MySQLUserSystem.class);
	}

	@Override
	public void crossTalk(Object... obj) {
		
	}

}
