// Copyright 2018 Louis Perrochon. All rights reserved

// GB Debug filters debug output
//
//

/*
Log.e: This is for when bad stuff happens. Use this tag in places like inside a catch statement. You know that an error
has occurred and therefore you're logging an error.

Log.w: Use this when you suspect something shady is going on. You may not be completely in full on error mode, but maybe
you recovered from some unexpected behavior. Basically, use this to log stuff you didn't expect to happen but isn't
necessarily an error. Kind of like a "hey, this happened, and it's weird, we should look into it."

Log.i: Use this to post useful information to the log. For example: that you have successfully connected to a server.
Basically use it to report successes.

Log.d: Use this for debugging purposes. If you want to print out a bunch of messages so you can log the exact flow of
your program, use this. If you want to keep a log of variable values, use this.

Log.v: Use this when you want to go absolutely nuts with your logging. If for some reason you've decided to log every
little thing in a particular part of your app, use the Log.v tag.

*/

package com.zwsi.gblib

import java.util.logging.Logger

internal object GBLog {

    // Legacy: 0 Warnings/Errors/Info only. 1: Show Debug, 2: Show verbose
    // Two reasons:
    // 1. Studio+Emulator doesn't show below info, so upgrading those to info
    // 2. Logging is expensive, with this I am not even logging when debug level is higher

    val DEBUG_LEVEL = 5
    val LOG_LEVEL = 5

    val Log = Logger.getLogger("GB")

    fun e(message: String) {
        if (LOG_LEVEL > 0) Log.severe(message)
    }

    fun w(message: String) {
        if (LOG_LEVEL > 1) Log.warning(message)
    }

    fun i(message: String) {
        if (LOG_LEVEL > 2) Log.info(message)
    }

    fun d(message: String) {
        if (DEBUG_LEVEL >= 1) {
            if (LOG_LEVEL > 3) Log.info(message) // Android Studio + Emulator doesn't show below info....
        }
    }

    fun v(message: String) {
        if (DEBUG_LEVEL >= 2) {
            if (LOG_LEVEL > 4) Log.info(message) // Android Studio + Emulator doesn't show below info....
        }
    }

    var assertionsEnabled = true

    inline fun gbAssert(message: String = "Assertion failure", test: () -> Boolean) {
        if (assertionsEnabled && !test()) AssertionError(message)
    }

    // Could inline, but Kotlin compiler says it's not worth it...
    fun gbAssert(message: String = "Assertion failure", test: Boolean) {
        if (assertionsEnabled && !test) AssertionError(message)
    }
}
















