#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})
/*
Refactor opportunities
1.  create separate files for each logical group of action creators
2.  combine the separate action creator files here an export them
 */

export function ${domainObjectName}FetchSuccess(${domainObjectName}){
    console.log('DISPATCHING SUCCESS', ${domainObjectName} );
    return {
        type: '${domainConstantName}_FETCH_SUCCESS',
        ${domainObjectName}: ${domainObjectName}
    }
}

export function ${domainObjectName}FetchError(error){
    return {
        type: '${domainConstantName}_FETCH_ERROR',
        error: error
    }
}

export function fetch${domainClassName}(url){
    console.log('Fetch of single ${domainObjectName} Invoked');
    return dispatch => {
        fetch(url)
            .then(response => response.json())
            .then(data => {
                dispatch(${domainObjectName}FetchSuccess(data))
            })
            .catch(() => dispatch(${domainObjectName}FetchError(true)))
    }
}

export function edit${domainClassName}(name, value){    
    return {
        type: '${domainConstantName}_EDIT',
        name,
        value
    }
}

export function save${domainClassName}Success(${domainObjectName}){
    return {
        type: '${domainConstantName}_SAVE_SUCCESS',
        ${domainObjectName}: ${domainObjectName}
    }
}

export function save${domainClassName}Error(${domainObjectName}){
    return {
        type: '${domainConstantName}_SAVE_ERROR',
        ${domainObjectName}: ${domainObjectName}
    }
}

export function save${domainClassName}(url, ${domainObjectName}){
    return dispatch => {
        fetch(url,{
          method: 'put',
          body: JSON.stringify(${domainObjectName}),
          headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/json'
          }
        })
            .then(response => response.json())
            .then(data => {
                dispatch(save${domainClassName}Success(data))
            })
            .catch(() => dispatch(save${domainClassName}Error(true)))
    }
}

/*---------------------------------------------------------*/
export function ${domainObjectName}sFetchSuccess(${domainObjectName}s){
    console.log('DISPATCHING SUCCESS', ${domainObjectName}s );
    return {
        type: '${domainConstantName}S_FETCH_SUCCESS',
        ${domainObjectName}s: ${domainObjectName}s
    }
}

export function ${domainObjectName}sFetchError(error){
    return {
        type: '${domainConstantName}S_FETCH_ERROR',
        error: error
    }
}

export function fetchAll${domainClassName}s(url){
    console.log('Fetch Invoked');
    return dispatch => {
        fetch(url)
        .then(response => response.json())
        .then(data => {            
            dispatch(${domainObjectName}sFetchSuccess(data.rows))
        })
        .catch(() => dispatch(${domainObjectName}sFetchError(true)))
    }
}