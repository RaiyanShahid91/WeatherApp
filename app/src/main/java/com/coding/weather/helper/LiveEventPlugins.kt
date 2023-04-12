package com.dil.singles.helper

import android.annotation.SuppressLint
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.Lifecycle

object LiveEventPlugins {

    var defaultActiveState = Lifecycle.State.STARTED
        set(value) {
            value.validateState()
            field = value
        }

    var defaultMaxSize = Int.MAX_VALUE
        set(value) {
            value.validateMaxSize()
            field = value
        }
}

internal fun Lifecycle.State.validateState() {
    if (this == Lifecycle.State.DESTROYED) {
        throw IllegalArgumentException("${Lifecycle.State.DESTROYED} cannot be used a active state.")
    }
}

internal fun Int.validateMaxSize() {
    if (this < 1) {
        throw IllegalArgumentException("Size must be at least 1.")
    }
}

internal val isMainThread
    @SuppressLint("RestrictedApi")
    get() = ArchTaskExecutor.getInstance().isMainThread

internal fun requireMainThread() {
    if (!isMainThread) {
        throw IllegalArgumentException("Must be called from the main thread")
    }
}