#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($start = 0)
#set($end = 13)
#set($range = [$start..$end])

import React from 'react';
import ${domainClassName}List from "../components/${domainClassName}List";
import {render, screen, fireEvent} from '@testing-library/react'

describe("${domainClassName}List", () => {
    const props = {
        history: []
    }

    const mockFetch${domainClassName} = jest.fn();
    const mockFetchAll${domainClassName}s = jest.fn();
    const mockOnChangePage = jest.fn();
    const mockOnSort = jest.fn();
    const mock${domainClassName}s =
        [
        #foreach($i in $range)
            #if($i >= 1)
            ,
            #end##
             #set($line = '')
             #set($index = 0)
             #foreach($key in $attrs.keySet() ) 
                 #if($index == 0)
                     #set($line = $line + $key + ": " +"'Sample-" + ${key} + ${i} + "'")
                 #else##
                     #set($line =  $line + ',' + $key + ": "+ "'Sample-" + ${key} + ${i} + "'")
                 #end##
                 #set($index = $index + 1)
             #end##
             
          {${line.trim()}}
        #end##
        ]

        const componentToTest = <${domainClassName}List history={props.history} fetch${domainClassName}={mockFetch${domainClassName}}
            fetchAll${domainClassName}s={mockFetchAll${domainClassName}s} ${domainObjectName}s={mock${domainClassName}s} first={0} totalRecords={11} 
            on${domainClassName}sChangePage={mockOnChangePage} onSort={mockOnSort} sortSettings={{}}/>
        
        it('renders correctly', () => {            
            expect(componentToTest).toMatchSnapshot();
        })
        
        it('displays the correct number of rows', () => {
        	const {container} = render(componentToTest);
            const numberOfRowsRendered = container.querySelectorAll('div.p-datatable-wrapper > table > tbody > tr').length;
            expect(numberOfRowsRendered).toBe(10);
        })

        it('invokes row action', () =>{
            const {container} = render(componentToTest);
            fireEvent.click(container.querySelector("button[id='Sample-${domainClassIdAttributeName}0']"));
            expect(mockFetch${domainClassName}).toBeCalledTimes(1);
        })
        
        it('invokes next page', () => {
        	const {container} = render(componentToTest);
            const selector = "button.p-paginator-next.p-paginator-element.p-link";
        	fireEvent.click(container.querySelector(selector));
            expect(mockOnChangePage).toBeCalledTimes(1);
        })
})