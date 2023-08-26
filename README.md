# ktreduced

## Description
This is small and lightweight lib I made which allows redux-like state management in Kotlin that I used for my JavaFX university projects.

## Installing


## Usage Example

Defining state:
```kotlin
data class CounterState(val current: Int, val error: String? = null) : ReducedState {
    companion object {
        val initialState = CounterState(0)
    }
}

sealed class Action : ReducedAction {
    object Initial : Action()
    data class Increase(val amount: Int) : Action()
    data class Decrease(val amount: Int) : Action()
    data class Error(val message: String) : Action()
}

fun reducer(state: CounterState, action: Action): CounterState = when (action) {
    is Action.Initial -> CounterState.initialState
    is Action.Increase -> CounterState(state.current + action.amount)
    is Action.Decrease -> CounterState(state.current - action.amount)
    is Action.Error -> CounterState(state.current, action.message)
}

typealias AppStore = Store<CounterState, Action>
```

Creating custom middleware:
```kotlin
class LoggerMiddleware : ReducedMiddleware<CounterState, Action, ReducedStore<CounterState, Action>>() {
    override fun preDispatch(store: ReducedStore<CounterState, Action>, action: Action) {
        println("Log[Old state: ${store.getState()}, action: $action]")
    }

    override fun postDispatch(store: ReducedStore<CounterState, Action>, action: Action) {
        println("Log[New state: ${store.getState()}]")
    }
}
```

Combining ktreduced with JavaFX to make nice declarative UI definition:
```kotlin
class CounterView(val store: AppStore) {

    private val txtAmount = TextField()

    val root = VBox().apply {
        spacing = 10.0
        padding = Insets(30.0)

        children.addAll(
            HBox().apply {
                spacing = 20.0
                children.addAll(
                    Label().apply {
                        store.subscribe {
                            text = "Current state: ${it.current}"
                        }
                    },
                    txtAmount,
                    Button("Increase").apply {
                        setOnAction { onIncrease() }
                    },
                    Button("Decrease").apply {
                        setOnAction { onDecrease() }
                    }
                )
            },
            Label().apply {
                textFill = Paint.valueOf("RED")
                store.subscribe {
                    text = it.error ?: ""
                }
            }
        )
    }

    private fun onIncrease() {
        try {
            val amount = Integer.parseInt(txtAmount.text)
            store.dispatch(Action.Increase(amount))
        } catch (ex: NumberFormatException) {
            store.dispatch(Action.Error("Input must be number"))
        }
    }

    private fun onDecrease() {
        try {
            val amount = Integer.parseInt(txtAmount.text)
            store.dispatch(Action.Decrease(amount))
        } catch (ex: NumberFormatException) {
            store.dispatch(Action.Error("Input must be number"))
        }
    }
}
```
Application Initialization:
```kotlin
class App : Application() {

    override fun start(primaryStage: Stage?) {

        val store = AppStore(CounterState.initialState, ::reducer)
        store.addMiddleware(LoggerMiddleware())

        if (primaryStage != null) {

            val view = CounterView(store)
            val scene = Scene(view.root, 640.0, 480.0)
            primaryStage.title = "CounterApp"
            primaryStage.scene = scene
            primaryStage.show()

        }

        store.dispatch(Action.Initial)
    }
}

fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}
```
