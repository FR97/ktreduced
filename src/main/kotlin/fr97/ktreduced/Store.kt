package fr97.ktreduced

/**
 * Basic implementation of [ReducedStore]
 *
 * Just enough logic to dispatch, subscribe and register middleware.
 *
 * Class is left open, so it is good starting point in case you want to extend store without implementing everything.
 *
 */
open class Store<S : ReducedState, A : ReducedAction>(
        state: S,
        override val reducer: (state: S, action: A) -> S)
    : ReducedStore<S, A> {

    protected var innerState: S = state

    private var dispatchWithMiddleware: (A) -> Unit = { innerState = reducer(innerState, it) }

    private var isDispatching = false

    private val subscribers = mutableListOf<Subscriber<S>>()
    override fun getState(): S = innerState

    override fun dispatch(action: A) {
        if (isDispatching) {
            throw IllegalStateException("Reducers can't call dispatch!")
        }
        isDispatching = true
        dispatchWithMiddleware(action) // dispatch
        isDispatching = false
        subscribers.forEach { it.invoke(innerState) } // notify change
    }

    /**
     *
     * Adds [ReducedMiddleware] implementation to store,
     * in this implementation preDispatch is called in order middleware is added,
     * while postDispatch is called in inverse order.
     *
     * Example:
     *
     * Calling addMiddleware(mw1,mw2,mw3) will result in following call order:
     *
     *  mw1.preDispatch(...)
     *  mw2.preDispatch(...)
     *  mw3.preDispatch(...)
     *  store.dispatch(...)
     *  mw3.postDispatch(...)
     *  mw2.postDispatch(...)
     *  mw1.postDispatch(...)
     *
     * @see ReducedStore
     *
     */
    override fun addMiddleware(vararg middleware: ReducedMiddleware<S, A, ReducedStore<S, A>>) {
        middleware.forEach {
            dispatchWithMiddleware = it.compose(this)(dispatchWithMiddleware)
        }

    }

    /**
     * Subscribes [Subscriber] to this store
     *
     * @param subscriber [Subscriber] to be notified when store changes
     *
     * @return Closure to call for unsubscribe
     */
    override fun subscribe(subscriber: Subscriber<S>): Subscription {
        if (isDispatching) throw IllegalStateException("Can't subscribe while dispatching")
        subscribers += subscriber

        return { subscribers.remove(subscriber) }
    }

}
