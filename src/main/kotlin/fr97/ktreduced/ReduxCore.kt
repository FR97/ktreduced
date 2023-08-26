package fr97.ktreduced

/**
 *
 * Empty interface, used to bound possible types,
 * and in case I remember to add something later.
 *
 * Example of using in application:
 *
 *      sealed class Action : ReducedAction {
 *         object Initial : Action()
 *         data class Increase(val amount: Int) : Action()
 *         data class Decrease(val amount: Int) : Action()
 *      }
 *
 * @author Filip
 */
interface ReducedAction


/**
 *
 * Empty interface, used to bound possible types,
 * and in case I remember to add something later.
 *
 * Application specific, but it state should always be immutable
 *
 * @author Filip
 */
interface ReducedState

open class ReducedActionCreator<S : ReducedState, A : ReducedAction, STORE : Store<S, A>>(private val store: STORE)

/**
 *
 * Store should keep current state ([ReducedState]),
 * transform state to next state using provided reducer
 * and notify subscribers when state changes.
 *
 * @property reducer - reducing function for the store
 * @author Filip
 */
interface ReducedStore<S : ReducedState, A : ReducedAction> {

    val reducer: (state: S, action: A) -> S

    /**
     * Returns state of this store - [ReducedState]
     *
     * @return [ReducedState]
     */
    fun getState(): S

    /**
     * Calls reducer function with passed action [ReducedAction]
     *
     * @param action - Example to dispatch [ReducedAction]
     */
    fun dispatch(action: A)

    /**
     * Used to register [ReducedMiddleware], called before and after dispatch
     */
    fun addMiddleware(vararg middleware: ReducedMiddleware<S, A, ReducedStore<S, A>>)

    /**
     * Subscribes subscriber to this store, subscriber gets notified when store changes
     *
     * @return Callback for canceling subscription
     */
    fun subscribe(subscriber: Subscriber<S>) : () -> Unit

}

