package com.jone.sevral.model.rxbus

interface SubjectListener {

    fun add(observerListener: ObserverListener)
    fun remove(observerListener: ObserverListener)
}
