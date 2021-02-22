#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})

import React from 'react';
jest.mock('../actions/${domainObjectName.toLowerCase()}')
import {fetch${domainClassName}, ${domainConstantName}S_CHANGE_PAGE, ${domainObjectName}sChangePage} from '../actions/${domainObjectName.toLowerCase()}';
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
    
    it('invokes changePage action', ()=> {
        ${domainObjectName}sChangePage.mockImplementation(() => {
            return{
                type: ${domainConstantName}S_CHANGE_PAGE,
                first: 0,
                rowsPerPage: 10,
                pageNumber: 1
            }
        })

        mapDispatchToProps(mockDispatch).on${domainClassName}sChangePage({first:0, rows: 10, page:1})
        expect(mockDispatch).toBeCalledWith({type: ${domainConstantName}S_CHANGE_PAGE,  first: 0, pageNumber: 1, rowsPerPage: 10})
    })
});