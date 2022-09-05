#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})
/*
Refactor opportunities
1.  create separate files for each logical group of action creators
2.  combine the separate action creator files here an export them
 */
import {getRequest, putRequest, postRequest} from "../utils/authority";

export const ${domainConstantName}_FETCH_SUCCESS = '${domainConstantName}_FETCH_SUCCESS';
export function ${domainObjectName}FetchSuccess(${domainObjectName}){
    console.log('DISPATCHING SUCCESS', ${domainObjectName} );
    return {
        type: ${domainConstantName}_FETCH_SUCCESS,
        ${domainObjectName}: ${domainObjectName}
    }
}

export const ${domainConstantName}_FETCH_ERROR = '${domainConstantName}_FETCH_ERROR';
export function ${domainObjectName}FetchError(error){
    return {
        type: ${domainConstantName}_FETCH_ERROR,
        error: error
    }
}

export function fetch${domainClassName}(url){
    console.log('Fetch of single ${domainObjectName} Invoked');
    return async dispatch => {
        try{
            const data = await getRequest(url);
            dispatch(${domainObjectName}FetchSuccess(data))
        }catch (e) {
            dispatch(${domainObjectName}FetchError(true))
        }   
    }
}

export const ${domainConstantName}_EDIT = '${domainConstantName}_EDIT';
export function edit${domainClassName}(name, value){    
    return {
        type: ${domainConstantName}_EDIT,
        name,
        value
    }
}

export const ${domainConstantName}_SAVE_SUCCESS = '${domainConstantName}_SAVE_SUCCESS';
export function save${domainClassName}Success(${domainObjectName}){
    return {
        type: ${domainConstantName}_SAVE_SUCCESS,
        ${domainObjectName}: ${domainObjectName}
    }
}

export const ${domainConstantName}_SAVE_ERROR = '${domainConstantName}_SAVE_ERROR';
export function save${domainClassName}Error(error){
    return {
        type: ${domainConstantName}_SAVE_ERROR,
        error
    }
}

export function save${domainClassName}(url, ${domainObjectName}){
    return async dispatch => {
        try {
            //const data = await putRequest(url, ${domainObjectName})
        	const data = ${domainObjectName}.${domainClassIdAttributeName} === ''? await postRequest(url, ${domainObjectName}):
        		await putRequest(url, ${domainObjectName})
            dispatch(save${domainClassName}Success(data))
        }catch (e){
            alert(JSON.stringify(e))
        }
    }
}

export const ${domainConstantName}_CREATE_NEW = '${domainConstantName}_CREATE_NEW'
export function createNew${domainClassName}(){
    return {
        type: ${domainConstantName}_CREATE_NEW
    }
}

/*---------------------------------------------------------*/

export const ${domainConstantName}S_FETCH_SUCCESS = '${domainConstantName}S_FETCH_SUCCESS';
export function ${domainObjectName}sFetchSuccess(${domainObjectName}s, totalRecords, lastPage){
    console.log('DISPATCHING SUCCESS', ${domainObjectName}s );
    return {
        type: ${domainConstantName}S_FETCH_SUCCESS,
        ${domainObjectName}s: ${domainObjectName}s,
        totalRecords,
        lastPage
    }
}

export const ${domainConstantName}S_FETCH_ERROR = '${domainConstantName}S_FETCH_ERROR';
export function ${domainObjectName}sFetchError(error){
    return {
        type: ${domainConstantName}S_FETCH_ERROR,
        error: error
    }
}

const ${domainConstantName}S_URI = '${domainObjectName}s'
export function fetchAll${domainClassName}s(){
    console.log('Fetch Invoked');
    return async (dispatch, getState) => {
        const {first, rowsPerPage, pageNumber, sortSettings} = getState().${domainObjectName}s
        try {
            const data = await getRequest(${domainConstantName}S_URI + '?page=' + (pageNumber + 1) + '&per_page=' + rowsPerPage +
                    '&sort_by=' + sortSettings.sortField + '&order=' + sortSettings.sortOrder);
            dispatch(${domainObjectName}sFetchSuccess(data.rows, data.totalRecords, data.lastPage))
        }catch (e) {
            dispatch(${domainObjectName}sFetchError(e))
        }
    }
}

export const ${domainConstantName}S_CHANGE_PAGE = '${domainConstantName}S_CHANGE_PAGE'
	export function ${domainObjectName}sChangePage(first, rowsPerPage, pageNumber){
	  return {
	      type: ${domainConstantName}S_CHANGE_PAGE,
	      first,
	      rowsPerPage,
	      pageNumber
	
	  }
	}
    
export const ${domainConstantName}S_SORT = '${domainConstantName}S_SORT'
export function ${domainObjectName}sSort(sortField, sortOrder){
    return{
        type: ${domainConstantName}S_SORT,
        sortField,
        sortOrder
    }
}    