#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react';
import renderer from 'react-test-renderer';
import ${domainClassName}Edit from './${domainClassName}Edit';
import ReactDom from 'react-dom';

function changeInputValue(input, value){
    const nativeInputValueSetter = Object.getOwnPropertyDescriptor(
        window.HTMLInputElement.prototype,
        'value'
    ).set;
    nativeInputValueSetter.call(input, value);
    const event = new Event('input', { bubbles: true});
    event.simulated = true;
    input.dispatchEvent(event);
}

describe("${domainClassName}Edit", ()=> {
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
        const mockChangeHandler = jest.fn();

        const mockSelected${domainClassName} = {
            #foreach($key in $attrs.keySet() )
            #if($foreach.count == 1)
            ${key}: 'Sample${key}'
            #else
            ,${key}: 'Sample${key}'    
            #end
            #end
        }

        const componentToTest = <${domainClassName}Edit selected${domainClassName}={mockSelected${domainClassName}} onEdit${domainClassName}={mockChangeHandler}/>
        
        const rootDiv = document.createElement('div')
        ReactDom.render(componentToTest, rootDiv);
        document.body.appendChild(rootDiv);
        changeInputValue(document.querySelector("input[name='${domainClassIdAttributeName}'"), "TEST");
        expect(mockChangeHandler).toBeCalledTimes(1);
    })

})