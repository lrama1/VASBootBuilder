export async function postRequest(url, payload){
    const response = await fetch(url, {
        method: 'POST',
        body: JSON.stringify(payload),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    });
    const data = await response.json();
    if(response.ok){
        return Promise.resolve(data)
    }else{
        return Promise.reject(data)
    }
}

export async function putRequest(url, payload){
    const response = await fetch(url, {
        method: 'PUT',
        body: JSON.stringify(payload),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    });
    const data = await response.json();
    if(response.ok){
        return Promise.resolve(data)
    }else{
        return Promise.reject(data)
    }
}

export async function getRequest(url){
    const response = await fetch(url, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    });
    const data = await response.json();
    if(response.ok){
        return Promise.resolve(data)
    }else{
        return Promise.reject(data)
    }
}