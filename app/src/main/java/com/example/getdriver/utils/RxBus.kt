package com.example.getdriver.utils

import io.reactivex.rxjava3.subjects.BehaviorSubject


class RxBus {
    companion object {
        val subject = BehaviorSubject.create<String>()
    }
}

