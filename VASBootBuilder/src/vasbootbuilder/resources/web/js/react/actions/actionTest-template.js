#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})

import {getRequest, putRequest} from "../utils/authority";
import {
    ${domainConstantName}_FETCH_SUCCESS,
    ${domainConstantName}_SAVE_SUCCESS,
    ${domainConstantName}S_FETCH_SUCCESS,
    ${domainConstantName}_SAVE_ERROR,
    ${domainConstantName}S_FETCH_ERROR,
    ${domainConstantName}_FETCH_ERROR,
    fetchAll${domainClassName}s,
    fetch${domainClassName},
    save${domainClassName}
} from "./${domainObjectName.toLowerCase()}";

jest.mock('../utils/authority')

describe('${domainObjectName} (action)', () => {
    
    const mockDispatch = jest.fn();
    const mockGetState= () => {
        return {
            ${domainObjectName}s: {
            	records: [],
                sortSettings: {}
            }
        }
    }

    jest.spyOn(window, 'alert').mockImplementation(() => {});
    
    afterEach(() => {
        jest.clearAllMocks();
    });

    it('invokes success when list of ${domainObjectName}s are returned', async () => {
        /* obtain reference to thunk*/
        const thunk = fetchAll${domainClassName}s('/mockurl', 1);

        /**/
        const response = Promise.resolve({rows:[], totalRecords: 0})
        getRequest.mockImplementation(() => response);

        const result = await thunk(mockDispatch, mockGetState);
        expect(mockDispatch).toBeCalledWith({type: ${domainConstantName}S_FETCH_SUCCESS, ${domainObjectName}s: [], totalRecords: 0})
    })

    it('invokes error when an error occured in the service', async () => {
        /* obtain reference to thunk*/
        const thunk = fetchAll${domainClassName}s('/mockurl', 1);

        /**/
        const response = Promise.reject('Error Occured')
        getRequest.mockImplementation(() => response);

        const result = await thunk(mockDispatch, mockGetState);
        expect(mockDispatch).toBeCalledWith({type: ${domainConstantName}S_FETCH_ERROR, error: 'Error Occured'})
    })
    
    it('invokes success when a single ${domainObjectName} is returned', async () => {
        /* obtain reference to thunk*/
        const thunk = fetch${domainClassName}('/mockurl');

        /**/
        const mockObjectToReturn = {
                #foreach($key in $attrs.keySet() )
                #if($foreach.count == 1)
                ${key}: 'Sample${key}'
                #else
                ,${key}: 'Sample${key}'    
                #end
                #end
            }
        
        const response = Promise.resolve(mockObjectToReturn)
        getRequest.mockImplementation(() => response);

        const result = await thunk(mockDispatch, mockGetState);
        expect(mockDispatch).toBeCalledWith({type: ${domainConstantName}_FETCH_SUCCESS, ${domainObjectName}: mockObjectToReturn})
    })
    
    it('invokes error when a single ${domainObjectName} fetch returned error', async () => {
        /* obtain reference to thunk*/
        const thunk = fetch${domainClassName}('/mockurl');
        
        const response = Promise.reject()
        getRequest.mockImplementation(() => response);

        const result = await thunk(mockDispatch, mockGetState);
        expect(mockDispatch).toBeCalledWith({type: ${domainConstantName}_FETCH_ERROR, error: true})
    })

    it('invokes success when a record is saved', async () => {
        /* obtain reference to thunk*/
        const thunk = save${domainClassName}('/mockurl', {});

        /**/
        const mockObjectToReturn = {
                #foreach($key in $attrs.keySet() )
                #if($foreach.count == 1)
                ${key}: 'Sample${key}'
                #else
                ,${key}: 'Sample${key}'    
                #end
                #end
            }
        const response = Promise.resolve(mockObjectToReturn)
        putRequest.mockImplementation(() => response);

        const result = await thunk(mockDispatch, mockGetState);
        expect(mockDispatch).toBeCalledWith({type: ${domainConstantName}_SAVE_SUCCESS, ${domainObjectName}: mockObjectToReturn})
    })
    
    it('invokes error when save errors out', async () => {
        /* obtain reference to thunk*/
        const thunk = save${domainClassName}('/mockurl', {});

        const response = Promise.reject('Error saving')
        putRequest.mockImplementation(() => response);

        const result = await thunk(mockDispatch, mockGetState);
        expect(window.alert).toBeCalledWith("\"Error saving\"")
    })

})