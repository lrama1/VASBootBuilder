#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})
/*
Refactor opportunities
1.  create separate files for each logical group of action creators
2.  combine the separate action creator files here an export them
 */
import {getRequest, putRequest} from "../utils/authority";

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
        /*
        fetch(url)
            .then(response => response.json())
            .then(data => {
                dispatch(${domainObjectName}FetchSuccess(data))
            })
            .catch(() => dispatch(${domainObjectName}FetchError(true)))
       */     
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
            const data = await putRequest(url, ${domainObjectName})
            dispatch(save${domainClassName}Success(data))
        }catch (e){
            alert(JSON.stringify(e))
        }
        /*fetch(url,{
          method: 'put',
          body: JSON.stringify(${domainObjectName}),
          headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/json'
          }
        })
            .then(
                function(response){
                    if (!response.ok) { throw response }
                    return response.json()
                }
               )
            .then(
                function(data){
                    console.log('Saved Data:', data)
                    dispatch(save${domainClassName}Success(data))
                }
               )
            .catch(
                function(error){
                    error.text().then(function (errorMessage){
                        dispatch(save${domainClassName}Error(errorMessage))
                    })
                })*/

    }
}

/*---------------------------------------------------------*/

export const ${domainConstantName}S_FETCH_SUCCESS = '${domainConstantName}S_FETCH_SUCCESS';
export function ${domainObjectName}sFetchSuccess(${domainObjectName}s, totalRecords, lastPage, first){
    console.log('DISPATCHING SUCCESS', ${domainObjectName}s );
    return {
        type: ${domainConstantName}S_FETCH_SUCCESS,
        ${domainObjectName}s: ${domainObjectName}s,
        totalRecords,
        lastPage,
        first
    }
}

export const ${domainConstantName}S_FETCH_ERROR = '${domainConstantName}S_FETCH_ERROR';
export function ${domainObjectName}sFetchError(error){
    return {
        type: ${domainConstantName}S_FETCH_ERROR,
        error: error
    }
}

export function fetchAll${domainClassName}s(url, first){
    console.log('Fetch Invoked');
    return async dispatch => {
        try {
            const data = await getRequest(url);
            dispatch(${domainObjectName}sFetchSuccess(data.rows, data.totalRecords, data.lastPage, first))
        }catch (e) {
            dispatch(${domainObjectName}sFetchError(true))
        }
        /*fetch(url)
        .then(response => response.json())
        .then(data => {            
            dispatch(${domainObjectName}sFetchSuccess(data.rows, data.totalRecords, data.lastPage, first))
        })
        .catch(() => dispatch(${domainObjectName}sFetchError(true)))*/
    }
}