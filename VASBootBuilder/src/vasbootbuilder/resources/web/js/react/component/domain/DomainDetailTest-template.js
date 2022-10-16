#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react';
import ${domainClassName}Edit from './${domainClassName}Edit';
import {render, fireEvent} from '@testing-library/react'

describe("${domainClassName}Edit", ()=> {
    const mockChangeHandler = jest.fn();
    const mockSaveHandler = jest.fn();

    const mockSelected${domainClassName} = {
        #foreach($key in $attrs.keySet() )
        #if($foreach.count == 1)
        ${key}: 'Sample${key}'
        #else
        ,${key}: 'Sample${key}'    
        #end
        #end
    }

    const componentToTest = <${domainClassName}Edit selected${domainClassName}={mockSelected${domainClassName}} onEdit${domainClassName}={mockChangeHandler}
                        onSave${domainClassName}={mockSaveHandler}/>    
    
    it('Renders fields correctly', () =>{        
        expect(componentToTest).toMatchSnapshot();
    });
        
    it('dispatches input changes', ()=> {
        const {container} = render(componentToTest)
        fireEvent.change(container.querySelector("input[name='${domainClassIdAttributeName}'"), {target: {value: 'TEST'}})
        expect(mockChangeHandler).toBeCalledTimes(1);
    })
    
    it('calls save function on click of Save button', () => {
        const {container} = render(componentToTest)
        fireEvent.click(container.querySelector("button[id='saveButton']"))
        expect(mockSaveHandler).toBeCalledTimes(1);
        expect(mockSaveHandler).toHaveBeenCalledWith('${domainObjectName}/Sample${domainClassIdAttributeName}', mockSelected${domainClassName})
    })

})