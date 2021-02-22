#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})
jest.mock('../actions/${domainObjectName}')
import {mapStateToProps, mapDispatchToProps} from "./${domainClassName}EditContainer";
import {save${domainClassName}, edit${domainClassName}, ${domainConstantName}_EDIT, ${domainConstantName}_SAVE_SUCCESS} from "../actions/${domainObjectName}";


describe('${domainClassName}EditContainer', () => {

    const mockDispatch = jest.fn();
    const mockGetState = jest.fn();

    afterEach(() => {
        mockDispatch.mockClear();
    });

    it('returns the expected state', ()=> {
        const sampleState = {
            ${domainObjectName}: {
                field1: 'samplevalue1',
                field2: 'samplevalue2'

            }
        }
        const result = mapStateToProps(sampleState);
        expect(result).toEqual(
            {
                selected${domainClassName}: {
                    field1: 'samplevalue1',
                    field2: 'samplevalue2'
                }
            }
        )
    })

    it('invokes edit ${domainObjectName} action', ()=> {
        edit${domainClassName}.mockImplementation(()=> {
            return{
                type: ${domainConstantName}_EDIT,
                name: 'field1',
                value: 'dummyval'
            }
        })
        mapDispatchToProps(mockDispatch).onEdit${domainClassName}({target: {name: 'field1', value: 'dummyval'}})
        expect(mockDispatch).toBeCalledWith({
            type: ${domainConstantName}_EDIT,
            name: 'field1',
            value: 'dummyval'
        })
        expect(mockDispatch).toBeCalledTimes(1)
    })

    it('invokes save ${domainObjectName} action', ()=> {
        save${domainClassName}.mockImplementation(()=>{
            return {
                type: ${domainConstantName}_SAVE_SUCCESS
            }
        })

        mapDispatchToProps(mockDispatch).onSave${domainClassName}('/save', {})
        expect(mockDispatch).toBeCalledWith({type: ${domainConstantName}_SAVE_SUCCESS})
    })

});