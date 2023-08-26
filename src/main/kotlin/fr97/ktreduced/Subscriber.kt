package fr97.ktreduced

typealias Subscriber<S> = (state:S) -> Unit

typealias Subscription = () -> Unit