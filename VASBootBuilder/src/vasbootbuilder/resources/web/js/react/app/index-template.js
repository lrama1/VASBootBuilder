#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})

import React from 'react'
import ReactDOM from 'react-dom'
import {Provider} from 'react-redux'
import { HashRouter } from 'react-router-dom'
import App from "./App";
import ${domainObjectName} from "./features/${domainObjectName}/${domainObjectName}Slice";
import ${domainObjectName}s from "./features/${domainObjectName}/${domainObjectName}sSlice";
import { configureStore } from "@reduxjs/toolkit";

//const store = createStore(reducers, {}, applyMiddleware(thunk) )
export const store = configureStore({
  reducer: {
    ${domainObjectName},
    ${domainObjectName}s,
  },
});

ReactDOM.render(
    <Provider store={store}>
    <HashRouter>
      <App/>
    </HashRouter>
    </Provider>, 
    document.querySelector("#root"));