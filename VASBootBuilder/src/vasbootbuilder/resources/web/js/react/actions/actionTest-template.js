#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})

import {getRequest, putRequest} from "../utils/authority";
import {
    ${domainConstantName}_FETCH_SUCCESS,
    ${domainConstantName}_SAVE_SUCCESS,
    ${domainConstantName}S_FETCH_SUCCESS,
    fetchAll${domainClassName}s,
    fetch${domainClassName},
    save${domainClassName}
} from "./${domainObjectName}";

jest.mock('../utils/authority')

describe('${domainObjectName} (action)', () => {

    it('invokes success when list of ${domainObjectName}s are returned', async () => {
        const mockDispatch = jest.fn();
        const mockGetState = jest.fn();

        /* obtain reference to thunk*/
        const thunk = fetchAll${domainClassName}s('/mockurl', 1);

        /**/
        const response = Promise.resolve({rows:[], totalRecords: 0, lastPage: 1})
        getRequest.mockImplementation(() => response);

        const result = await thunk(mockDispatch, mockGetState);
        expect(mockDispatch).toBeCalledWith({type: ${domainConstantName}S_FETCH_SUCCESS, ${domainObjectName}s: [], totalRecords: 0, lastPage: 1, first: 1})
    })

    it('invokes success when a single ${domainObjectName} is returned', async () => {
        const mockDispatch = jest.fn();
        const mockGetState = jest.fn();

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

    it('invokes success when a record is saved', async () => {
        const mockDispatch = jest.fn();
        const mockGetState = jest.fn();

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

})