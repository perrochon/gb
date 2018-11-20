// Copyright 2018 Louis Perrochon. All rights reserved

// GB Debug filters debug output
// It's a big, manual GBLib integration test. Pass criteria are making sure it finishes, and glance over the output.
//

package com.zwsi.gblib

internal object GBDebug {

    val DEBUG_LEVEL = 0  // 0 quiet, 1 minimal, 2 verbose, 3 everything

    fun l1(message: String) {
        if (DEBUG_LEVEL >= 1) {
            println("-GBDebug: $message")
        }
    }

    fun l2(message: String) {
        if (DEBUG_LEVEL >= 2) {
            println("--GBDebug: $message")
        }
    }

    fun l3(message: String) {
        if (DEBUG_LEVEL >= 3) {
            println("---GBDebug: $message")
        }
    }
}
















