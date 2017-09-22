package com.fretron.transporter.GroupManager;

import com.fretron.Context;
import com.fretron.constants.Constants;
import com.fretron.transporter.UserManager.UserManager;

public class GroupManagerDriver {

    public static void main(String args[]) throws Exception {
        Context.init(args);
        final String schema_registry = Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL);
        final String  BOOTSTRAP_SERVERS = Context.getConfig().getString(Constants.KEY_BOOTSTRAP_SERVERS);
        new GroupManager().startStream(schema_registry,BOOTSTRAP_SERVERS).start();
    }
}
