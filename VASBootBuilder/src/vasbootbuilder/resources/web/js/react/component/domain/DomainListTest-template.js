#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($start = 0)
#set($end = 13)
#set($range = [$start..$end])

import React from 'react';
import ReactDom from 'react-dom';
import renderer from 'react-test-renderer';
import {clickElement} from '../utils/TestUtils';
import ${domainClassName}List from "../components/${domainClassName}List";

describe("${domainClassName}List", () => {
    const props = {
        history: []
    }

    const mockFetch${domainClassName} = jest.fn();
    const mockFetchAll${domainClassName}s = jest.fn();
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
            fetchAll${domainClassName}s={mockFetchAll${domainClassName}s} ${domainObjectName}s={mock${domainClassName}s} first={0} totalRecords={11} />

        const rootDiv = document.createElement('div') ;
        ReactDom.render(componentToTest, rootDiv);
        document.body.appendChild(rootDiv);
        
        it('renders correctly', () => {
            const tree = renderer.create(componentToTest).toJSON();
            expect(tree).toMatchSnapshot();
        })
        
        it('displays the correct number of rows', () => {
            const numberOfRowsRendered = document.querySelectorAll('div.p-datatable-wrapper > table > tbody > tr').length;
            expect(numberOfRowsRendered).toBe(10)
        })

        it('invokes row action', () =>{
            clickElement(document.querySelector("button[id='Sample-${domainClassIdAttributeName}0']"))
            expect(mockFetch${domainClassName}).toBeCalledTimes(1)
        })
        
        it('invokes next page', () => {
            const selector = "div.p-paginator.p-component.p-unselectable-text.p-paginator-bottom > span > button:nth-child(2)";
            clickElement(document.querySelector(selector));
            expect(mockFetchAll${domainClassName}s).toBeCalledTimes(1);
        })
})