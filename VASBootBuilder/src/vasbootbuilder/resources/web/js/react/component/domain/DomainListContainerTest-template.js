#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

import React from 'react';
jest.mock('../actions/${domainObjectName.toLowerCase()}')
import {fetch${domainClassName}, fetchAll${domainClassName}s} from '../actions/${domainObjectName.toLowerCase()}';
import {mapStateToProps, mapDispatchToProps} from "./${domainClassName}ListContainer";

describe('${domainClassName}ListContainer', () => {
    const mockDispatch = jest.fn();
    
    afterEach(() => {
        mockDispatch.mockClear();
    });
    
    it('returns the expected state', ()=> {
        const sampleState = {
            ${domainObjectName}s: {
                records: [],
                totalRecords: 0,
                first: 1
            }
        }
        const result = mapStateToProps(sampleState);
        expect(result).toMatchSnapshot();
    })
    
    it('dispatches fetch${domainClassName}', () => {
        fetch${domainClassName}.mockImplementation(()=> {
            return {param: 'SomeValue'}
        });
        
        mapDispatchToProps(mockDispatch).fetch${domainClassName}('mockurl', 1)
        expect(mockDispatch).toBeCalledWith({param:'SomeValue'})
    })
    
    it('dispatches fetchAll${domainClassName}s', () => {
        fetchAll${domainClassName}s.mockImplementation(()=> {
            return {param: 'SomeValue'}
        });
        
        mapDispatchToProps(mockDispatch).fetchAll${domainClassName}s('mockurl', 1)
        expect(mockDispatch).toBeCalledWith({param:'SomeValue'})
    })
    
});