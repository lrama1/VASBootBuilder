#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})

import {${domainConstantName}_FETCH_SUCCESS , ${domainConstantName}_EDIT, ${domainConstantName}_SAVE_SUCCESS, ${domainConstantName}_SAVE_ERROR, ${domainConstantName}_CREATE_NEW,
    ${domainConstantName}S_FETCH_SUCCESS, ${domainConstantName}S_CHANGE_PAGE, ${domainConstantName}S_SORT} from '../actions/${domainObjectName}';
import _ from "lodash";    

const initial${domainClassName}s = {
     records: [],
     totalRecords: 0,
     first: 0,
     rowsPerPage: 10,
     pageNumber: 0,
     sortSettings: {
         sortField: '',
         sortOrder: ''
     }
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
    }else if(action.type === ${domainConstantName}S_SORT){
        return{
            ...state,
            sortSettings: {
                sortField: action.sortField,
                sortOrder: action.sortOrder
            }
        }
    }
    return state;
}

const initial${domainClassName} = {
#foreach($key in $attrs.keySet() )
    #if($foreach.count == 1)
    	#if($attrs.get(${key}) == 'java.util.Date')	
    		${key}: new Date()    
    	#else
    		${key}: ''	
    	#end	
    
    #else
    	#if($attrs.get(${key}) == 'java.util.Date')	
    		,${key}: new Date()    
    	#else
    		,${key}: ''	
    	#end    
    #end    
#end
}

export const ${domainObjectName} = (state = initial${domainClassName}, action) => {
    if (action.type === ${domainConstantName}_FETCH_SUCCESS){
        return{
        	...state,
        	...action.${domainObjectName}
        	#foreach($key in $attrs.keySet() )
        	#if($attrs.get(${key}) == 'java.util.Date')	
	    		,${key}: new Date(Date.parse(action.${domainObjectName}.${key}))    
	    	#end
        	#end
        }
        
    }else if(action.type === ${domainConstantName}_EDIT){
        /*return {
        	...state,
        	[action.name]: action.value
        	}*/
    	const newState = _.set(state, action.name, action.value)
        return {...newState}
    }else if(action.type === ${domainConstantName}_SAVE_SUCCESS){
        return {
        	...state,
        	...action.${domainObjectName}
        	#foreach($key in $attrs.keySet() )
        	#if($attrs.get(${key}) == 'java.util.Date')	
	    		,${key}: new Date(Date.parse(action.${domainObjectName}.${key}))    
	    	#end
        	#end
        }
    }else if(action.type === ${domainConstantName}_SAVE_ERROR){
        alert(action.error)
        return state;
    }else if(action.type === ${domainConstantName}_CREATE_NEW){
        return{
            ...initial${domainClassName}
        }
    }
    return state;
}
