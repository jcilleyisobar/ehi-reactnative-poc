import React, { Component } from "react"
import { createStore, applyMiddleware } from "redux"
import { default as thunkMiddleware } from "redux-thunk"
import { Provider } from "react-redux"
import { rootReducer } from "./js/reducers"

import { IntroScreen } from "./js/containers"

const store = createStore(rootReducer, {}, applyMiddleware(thunkMiddleware))

export const App = (props) => {
    return (
        <Provider store={store}>
            <IntroScreen />
        </Provider>
    )
}