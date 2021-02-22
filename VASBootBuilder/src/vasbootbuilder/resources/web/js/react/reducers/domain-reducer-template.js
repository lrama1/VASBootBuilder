#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})

import {${domainConstantName}_FETCH_SUCCESS , ${domainConstantName}_EDIT, ${domainConstantName}_SAVE_SUCCESS, ${domainConstantName}_SAVE_ERROR,
    ${domainConstantName}S_FETCH_SUCCESS, ${domainConstantName}S_CHANGE_PAGE} from '../actions/${domainObjectName.toLowerCase()}'

const initial${domainClassName}s = {
     records: [],
     totalRecords: 0,
     first: 0,
     rowsPerPage: 10,
     pageNumber: 0
} 

export const ${domainObjectName}s = (state = initial${domainClassName}s, action) => {
    if(action.type === '${domainConstantName}S_FETCH_SUCCESS'){
        return {
            ...state,
            records: action.${domainObjectName}s,
            totalRecords: action.totalRecords
        }
    }else if(action.type === ${domainConstantName}S_CHANGE_PAGE){
        return{
            ...state,
            rowsPerPage: action.rowsPerPage,
            pageNumber: action.pageNumber,
            first: action.first
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

export const ${domainObjectName} = (state = initial${domainClassName}, action) => {
    if (action.type === ${domainConstantName}_FETCH_SUCCESS){
        return action.${domainObjectName}
        
    }else if(action.type === ${domainConstantName}_EDIT){
        return {
        	...state,
        	[action.name]: action.value
        	}
    }else if(action.type === ${domainConstantName}_SAVE_SUCCESS){
        return action.${domainObjectName};
    }else if(action.type === ${domainConstantName}_SAVE_ERROR){
        alert(action.error)
        return state;
    }
    return state;
}
