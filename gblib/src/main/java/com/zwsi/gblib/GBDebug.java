// Copyright 2018 Louis Perrochon. All rights reserved

// GB Debug filters debug output
// It's a big, manual GBLib integration test. Pass criteria are making sure it finishes, and glance over the output.
//

package com.zwsi.gblib;

import java.lang.String;

class GBDebug {

    private static final int DEBUG_LEVEL = 3;  // 0 quiet, 1 minimal, 2 verbose, 3 everything

    static void l1(String message) {
        if (DEBUG_LEVEL >= 1) {
            System.out.println("-GBDebug: " + message);
        }
    }

    static void l2(String message) {
        if (DEBUG_LEVEL >= 2) {
            System.out.println("--GBDebug: " + message);
        }
    }

    static void l3(String message) {
        if (DEBUG_LEVEL >= 3) {
            System.out.println("---GBDebug: " + message);
        }
    }
}
















