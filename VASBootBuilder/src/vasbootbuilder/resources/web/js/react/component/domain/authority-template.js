export async function postRequest(url, payload){
    const response = await fetch(url, {
        method: 'POST',
        redirect: 'follow',
        body: JSON.stringify(payload),
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
        const stringToCheck = 'Login';
        const data = await response.text();
        if(data.indexOf(stringToCheck) > -1){
            const home = window.location.protocol + "//" + window.location.hostname + "/${projectName}";
        }        
    }
}

export async function putRequest(url, payload){
    const response = await fetch( url, {
        method: 'PUT',
        redirect: 'follow',
        body: JSON.stringify(payload),
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
        const stringToCheck = 'Login';
        const data = await response.text();
        if(data.indexOf(stringToCheck) > -1){
            const home = window.location.protocol + "//" + window.location.hostname + "/${projectName}";
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
        const stringToCheck = 'Login';
        const data = await response.text();
        if(data.indexOf(stringToCheck) > -1){
            const home = window.location.protocol + "//" + window.location.hostname + "/${projectName}";
        }        
    }
}