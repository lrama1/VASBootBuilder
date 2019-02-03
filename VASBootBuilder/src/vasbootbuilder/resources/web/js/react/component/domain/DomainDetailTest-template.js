#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react';
import renderer from 'react-test-renderer';
import Adapter from 'enzyme-adapter-react-16';
import ${domainClassName}Edit from './${domainClassName}Edit'
import { shallow, configure } from 'enzyme';


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

    it('dispatches input changes', () =>{
        configure({adapter: new Adapter()});

        const onEdit${domainClassName} = jest.fn();
        const onSave${domainClassName} = jest.fn();

        const props = {
            selected${domainClassName} : {
                #foreach($key in $attrs.keySet() )
                #if($foreach.count == 1)
                ${key}: 'Sample${key}'
                #else
                ,${key}: 'Sample${key}'    
                #end
                #end
            },
            onEdit${domainClassName},
            onSave${domainClassName}
        }

        const wrapper = shallow(<${domainClassName}Edit {...props} />);
        #foreach($key in $attrs.keySet() )
        wrapper.find('#${key}').simulate('change', {target: {name: '${key}', value: 'Sample${key}'}});
        expect(onEdit${domainClassName}).toHaveBeenCalledWith(['${key}'], 'Sample${key}');
        #end
                
        wrapper.find('#saveButton').simulate('click',{preventDefault: ()=>{}});
        expect(onSave${domainClassName}).toHaveBeenCalled();
    })

})