// Copyright 2018 Louis Perrochon. All rights reserved

// Mission Logic
//

package com.zwsi.gblib

class GBNews() {
    
    var digest = ArrayList<String>()

    fun addNews (news: String) {
        digest.add(news)
    }

    fun getNext(news: String) {
        digest.add(news)
    }

}