package com.fretron.transporter.UserManager;

import com.fretron.Context;
import com.fretron.constants.Constants;

public class UserManagerDriver {


    public static void main(String args[]) throws Exception {
        Context.init(args);
        String schemRegistry = Context.getConfig().getString(Constants.KEY_SCHEMA_REGISTRY_URL);
        String bootStrapUrl = Context.getConfig().getString(Constants.KEY_BOOTSTRAP_SERVERS);

        new UserManager().startStream(bootStrapUrl, schemRegistry).start();
    }
}
