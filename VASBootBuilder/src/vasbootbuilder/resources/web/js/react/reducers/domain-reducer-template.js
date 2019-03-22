#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})

export const ${domainObjectName}s = (state = [], action) => {
    if(action.type === '${domainConstantName}S_FETCH_SUCCESS'){
        return action.${domainObjectName}s        
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

export const ${domainObjectName} = (state = initial${domainClassName}, action) => {
    if (action.type === '${domainConstantName}_FETCH_SUCCESS'){
        return action.${domainObjectName}
        
    }else if(action.type ==='${domainConstantName}_EDIT'){
        return {
        	...state,
        	[action.name]: action.value
        	}
    }else if(action.type ==='${domainConstantName}_SAVE'){
        return action.${domainObjectName};
    }
    return state;
}
