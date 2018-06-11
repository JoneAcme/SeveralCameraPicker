package com.jone.several.model.rxbus

interface SubjectListener {

    fun add(observerListener: ObserverListener)
    fun remove(observerListener: ObserverListener)
}
