#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})

export const ${domainObjectName}sReducer = (${domainObjectName}s = [], action) => {
    if(action.type === '${domainConstantName}S_FETCH_SUCCESS'){
        return action.${domainObjectName}s;
    }
     return ${domainObjectName}s;
}

export const ${domainObjectName}FetchReducer = (${domainObjectName} = {}, action) => {
    if (action.type === '${domainConstantName}_FETCH_SUCCESS'){
        return action.${domainObjectName};
    }else if(action.type ==='${domainConstantName}_EDIT'){
        return action.${domainObjectName};
    }else if(action.type ==='${domainConstantName}_SAVE'){
        return action.${domainObjectName};
    }
    return ${domainObjectName};
}
