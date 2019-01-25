#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})
import {combineReducers} from 'redux'
import { ${domainObjectName}sReducer, ${domainObjectName}FetchReducer } from './${domainObjectName}Reducer';

/*
By combining reducers, you now have to use the namespace of the reducer
when mapping State-to-Props in your components
 */
export default combineReducers({
    ${domainObjectName}FetchReducer,
    ${domainObjectName}sReducer
});

 