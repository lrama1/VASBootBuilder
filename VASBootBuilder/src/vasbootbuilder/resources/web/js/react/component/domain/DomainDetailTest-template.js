#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react';
import renderer from 'react-test-renderer';
import ${domainClassName}Edit from './${domainClassName}Edit';
import ReactDom from 'react-dom';
import {changeInputValue, clickElement} from '../utils/TestUtils'

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
    const rootDiv = document.createElement('div')
    ReactDom.render(componentToTest, rootDiv);
    document.body.appendChild(rootDiv);
    
    it('Renders fields correctly', () =>{
        const props = {
            selected${domainClassName} : {
                #foreach($key in $attrs.keySet() )
                #if($foreach.count == 1)
                ${key}: 'Sample${key}'
                #else
                ,${key}: 'Sample${key}'    
                #end
                #end                
            }
        }
        const tree = renderer.create(<${domainClassName}Edit {...props}/>).toJSON();
        expect(tree).toMatchSnapshot();
    });
        
    it('dispatches input changes', ()=> {
        changeInputValue(document.querySelector("input[name='${domainClassIdAttributeName}'"), "TEST");
        expect(mockChangeHandler).toBeCalledTimes(1);
    })
    
    it('calls save function on click of Save button', () => {
        clickElement(document.querySelector("button[id='saveButton']"));
        expect(mockSaveHandler).toBeCalledTimes(1);
        expect(mockSaveHandler).toHaveBeenCalledWith('$domainObjectName/Sample${domainClassIdAttributeName}', mockSelected${domainClassName})
    })

})