import React from 'react'
import ReactDOM from 'react-dom'
import {createStore, applyMiddleware} from 'redux'
import thunk from 'redux-thunk'
import {Provider} from 'react-redux'

import AppContainer from './containers/AppContainer'
import reducers from './reducers'

const store = createStore(reducers, {}, applyMiddleware(thunk) )

ReactDOM.render(
    <Provider store={store}>
    <AppContainer/>
    </Provider>, 
    document.querySelector("#root"));