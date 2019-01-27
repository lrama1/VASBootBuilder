#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})

export const ${domainObjectName}sReducer = (state = {${domainObjectName}s: []}, action) => {
    if(action.type === '${domainConstantName}S_FETCH_SUCCESS'){
        return {
        	...state,
        	${domainObjectName}s: action.${domainObjectName}s
        }
    }
    return state;
}

export const ${domainObjectName}FetchReducer = (state = {${domainObjectName}:  {}}, action) => {
    if (action.type === '${domainConstantName}_FETCH_SUCCESS'){
        return {
        	...state,
        	${domainObjectName}: action.${domainObjectName}
        }
    }else if(action.type ==='${domainConstantName}_EDIT'){
        return {
        	...state,
        	${domainObjectName}: {
        		...state.${domainObjectName},
        		[action.name]: action.value
        	}
        };
    }else if(action.type ==='${domainConstantName}_SAVE'){
        return action.${domainObjectName};
    }
    return state;
}
