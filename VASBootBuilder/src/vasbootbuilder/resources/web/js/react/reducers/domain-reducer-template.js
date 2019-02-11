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

const initial${domainClassName} = {
#foreach($key in $attrs.keySet() )
    #if($foreach.count == 1)
    ${key}: ''    
    #else
    ,${key}: ''    
    #end    
#end
}

export const ${domainObjectName}FetchReducer = (state = {${domainObjectName}: initial${domainClassName}}, action) => {
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
