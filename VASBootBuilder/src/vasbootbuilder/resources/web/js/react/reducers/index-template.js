#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})
import {combineReducers} from 'redux'

const ${domainObjectName}sReducer = (${domainObjectName}s = [], action) => {
    if(action.type === '${domainConstantName}S_FETCH_SUCCESS'){
        return action.${domainObjectName}s;
    }
     return ${domainObjectName}s;
}

const ${domainObjectName}FetchReducer = (${domainObjectName} = {}, action) => {
    if (action.type === '${domainConstantName}_FETCH_SUCCESS'){
        return action.${domainObjectName};
    }else if(action.type ==='${domainConstantName}_EDIT'){
        return action.${domainObjectName};
    }else if(action.type ==='${domainConstantName}_SAVE'){
        return action.${domainObjectName};
    }
    return ${domainObjectName};
}

/*
By combining reducers, you now have to use the namespace of the reducer
when mapping State-to-Props in your components
 */
export default combineReducers({
    ${domainObjectName}FetchReducer,
    ${domainObjectName}sReducer
});

 