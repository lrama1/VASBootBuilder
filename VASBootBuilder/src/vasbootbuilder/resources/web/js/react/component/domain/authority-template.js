export async function postRequest(url, payload){
    const csrf = getCsrfValues(['__csrfheader', '__csrftoken'], document)
    
    const response = await fetch(url, {
        method: 'POST',
        redirect: 'follow',
        body: JSON.stringify(payload),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            [csrf[0]]: csrf[1]
        }
    });
    
    const contentType = response.headers.get('Content-Type');
    if(contentType.indexOf('json') > -1){
        const data = await response.json();
        if(response.ok){
            return Promise.resolve(data)
        }else{
            return Promise.reject(data)
        }
    }else if(contentType.indexOf('html') > -1){
        const stringToCheck = 'form-signin';
        const data = await response.text();
        if(data.indexOf(stringToCheck) > -1){
            window.location.href = window.location.protocol + "//" + window.location.hostname;
        }        
    }
}

export async function putRequest(url, payload){
    const csrf = getCsrfValues(['__csrfheader', '__csrftoken'], document)
    const response = await fetch( url, {
        method: 'PUT',
        redirect: 'follow',
        body: JSON.stringify(payload),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            [csrf[0]]: csrf[1]
        }
    });
    const contentType = response.headers.get('Content-Type');
    if(contentType.indexOf('json') > -1){
        const data = await response.json();
        if(response.ok){
            return Promise.resolve(data)
        }else{
            return Promise.reject(data)
        }
    }else if(contentType.indexOf('html') > -1){
        const stringToCheck = 'form-signin';
        const data = await response.text();
        if(data.indexOf(stringToCheck) > -1){
            window.location.href = window.location.protocol + "//" + window.location.hostname;
        }        
    }
}

export async function getRequest(url){
    const response = await fetch(url, {
        method: 'GET',
        redirect: 'follow',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    });
    const contentType = response.headers.get('Content-Type');
    if(contentType.indexOf('json') > -1){
        const data = await response.json();
        if(response.ok){
            return Promise.resolve(data)
        }else{
            return Promise.reject(data)
        }
    }else if(contentType.indexOf('html') > -1){
        const stringToCheck = 'form-signin';
        const data = await response.text();
        if(data.indexOf(stringToCheck) > -1){
            window.location.href = window.location.protocol + "//" + window.location.hostname;
        }        
    }
}

const browserDocument = document;
export function getCsrfValues(tags = [], document = browserDocument){
    return (
        tags
            .map(id => document.getElementById(id))
            .map(meta => meta && meta.getAttribute('content'))
            .filter(i => !!i)
    )
}