package com.example.getdriver.utils

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import org.json.JSONObject

class AndroidBus {
    companion object {
        val behaviorSubject = PublishSubject.create<JSONObject>()
    }
}