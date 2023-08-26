package fr97.ktreduced

abstract class ReducedMiddleware<S : ReducedState, A : ReducedAction, in STORE: ReducedStore<S, A>> {

    /**
     * Called before dispatching action[ReducedAction] to reducer
     */
    abstract fun preDispatch(store: STORE, action: A)

    /**
     * Called after dispatching action[ReducedAction] to reducer
     */
    abstract fun postDispatch(store: STORE, action: A)

    /**
     * Used for composing middleware so that it wraps dispatch function nicely
     */
    fun compose(store: STORE) : ((A) -> Unit) -> (A) -> Unit {
        return { dispatch: (A) -> Unit ->
            { action: A ->
                preDispatch(store, action)
                dispatch(action)
                postDispatch(store, action)
            }
        }
    }


}
