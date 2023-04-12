package com.dil.singles.helper

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import java.util.*

open class LiveEvent<T>(val maxsize: Int = LiveEventPlugins.defaultMaxSize) {

    private val lock = Any()
    private val pendingEvents = LinkedList<T>()
    val hasConsumer get() = _consumer != null
    private var _consumer: OwnerWithConsumer<T>? = null

    fun getPendingEvents(): Array<out Any>? {
        return pendingEvents.toArray()
    }

    private fun offerInternal(event: T) {
        requireMainThread()

        synchronized(lock) {
            pendingEvents.add(event)

            while (pendingEvents.size > maxsize) {
                pendingEvents.poll()
            }
        }

        dispatchPendingEvents()
    }

    @MainThread
    fun consume(owner: LifecycleOwner, consumer: (T) -> Unit) {
        consume(owner, LiveEventPlugins.defaultActiveState, consumer)
    }

    @MainThread
    fun consume(owner: LifecycleOwner, activeState: Lifecycle.State, consume: (T) -> Unit) {
        requireMainThread()
        if (hasConsumer) {
            removeConsumer()
            Log.d("", "Only one consumer at a time allowed. Removing previous consumer.")
        }

        _consumer = OwnerWithConsumer(owner, consume, activeState)
        _consumer!!.start()
    }

    @MainThread
    fun removeConsumer() {
        requireMainThread()
        _consumer!!.stop()
        _consumer = null
    }

    private inner class OwnerWithConsumer<T>(
        val owner: LifecycleOwner,
        val action: (T) -> Unit,
        val activeState: Lifecycle.State
    ) {
        val isActive get() = owner.lifecycle.currentState.isAtLeast(activeState)

        private val lifecycleObserver = GenericLifecycleObserver { source, _ ->
            if (isActive) {
                dispatchPendingEvents()
            }

            if (source.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                removeConsumer()
            }
        }

        fun start() {
            owner.lifecycle.addObserver(lifecycleObserver)
        }

        fun stop() {
            owner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    private fun dispatchPendingEvents() {
        requireMainThread()

        if (pendingEvents.isEmpty()) return

        val consumer = _consumer ?: return

        if (!consumer.isActive) return

        synchronized(lock) {
            while (pendingEvents.isNotEmpty()) {
                val event = pendingEvents.poll()
                consumer.action.invoke(event)
            }
        }
    }


    fun offer(event: T) {
        if (isMainThread) {
            offerInternal(event)
        } else {
            Handler(Looper.getMainLooper()).post { offerInternal(event) }
        }
    }
}

class SingleEvent : LiveEvent<Boolean>(LiveEventPlugins.defaultMaxSize) {
    fun actionOccured() {
        super.offer(true)
    }
}
