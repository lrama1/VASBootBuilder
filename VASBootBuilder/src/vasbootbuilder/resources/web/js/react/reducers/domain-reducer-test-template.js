#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import {${domainObjectName}s, ${domainObjectName}} from "./${domainObjectName.toLowerCase()}";

describe('reducers/${domainObjectName}', ()=> {

    jest.spyOn(window, 'alert').mockImplementation(() => {});

    afterEach(() => {
        jest.clearAllMocks();
    });

    /* We are testing that when we give our reducer an action, it creates
    * an expected new state*/
    it('returns a state with ${domainObjectName}s', () => {
        const dummyAction = {
            type: '${domainClassName.toUpperCase()}S_FETCH_SUCCESS',
            ${domainObjectName}s: [

            ],
            totalRecords: 2
        }

        const expectedResults = {
            records: [],
            totalRecords: 2
        }

        const result = ${domainObjectName}s(null, dummyAction);
        expect(result).toEqual(expectedResults)
    })

    it('returns a state with ${domainObjectName}', () => {
        const dummyAction = {
            type: '${domainClassName.toUpperCase()}_FETCH_SUCCESS',
            ${domainObjectName}: {
                #foreach($key in $attrs.keySet() )
                #if($foreach.count == 1)
                ${key}: 'Sample${key}'
                #else
                ,${key}: 'Sample${key}'    
                #end
                #end
            }
        }

        const expectedResults = {
                #foreach($key in $attrs.keySet() )
                #if($foreach.count == 1)
                ${key}: 'Sample${key}'
                #else
                ,${key}: 'Sample${key}'    
                #end
                #end
        }

        const result = ${domainObjectName}(null, dummyAction);
        expect(result).toEqual(result)
    })

    it('returns a state a field edited', () => {
        const dummyAction = {
            type: '${domainClassName.toUpperCase()}_EDIT',
            "${domainClassIdAttributeName}": 'ZZZ'
        }

        const expectedResults = {
            "${domainClassIdAttributeName}": "ZZZ",
            attr2: 'YYY'
        }

        const result = ${domainObjectName}({attr2: 'YYY'}, dummyAction);
        expect(result).toEqual(result)
    })

    it('returns saved ${domainObjectName}', () => {
        const dummyAction = {
            type: '${domainClassName.toUpperCase()}_SAVE_SUCCESS',
            "${domainObjectName}": {
                #foreach($key in $attrs.keySet() )
                #if($foreach.count == 1)
                ${key}: 'Sample${key}'
                #else
                ,${key}: 'Sample${key}'    
                #end
                #end
            }
        }

        const expectedResult = {
                #foreach($key in $attrs.keySet() )
                #if($foreach.count == 1)
                ${key}: 'Sample${key}'
                #else
                ,${key}: 'Sample${key}'    
                #end
                #end
        }

        const result = ${domainObjectName}(null, dummyAction);

        expect(result).toEqual(expectedResult)
    })

    it('pops up alert on error', () => {
        const dummyAction = {
            type: '${domainClassName.toUpperCase()}_SAVE_ERROR',
            error: 'Error saving'
        }

        const result = ${domainObjectName}(null, dummyAction);
        expect(window.alert).toBeCalledWith('Error saving')
    })
})