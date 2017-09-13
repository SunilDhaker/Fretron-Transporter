package com.transporter.UserManager;

import com.transporter.Context;

public class UserManagerDriver {
    public static void main(String args[]) throws Exception {
        Context.init(args);

        new UserManager().startStream().start();
    }
}
