package com.fretron.transporter.TransporterManager;

import com.fretron.Context;

/**
 * Created by anurag on 14-Sep-17.
 */
public class TransporterManagerDriver {

    public static void main(String[] args) throws Exception {
        Context.init(args);
        new TransporterManager().createStream();

    }
}
