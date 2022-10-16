#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
#set($domainConstantName = ${domainClassName.toUpperCase()})
import {combineReducers} from 'redux'
import { ${domainObjectName}s, ${domainObjectName} } from './${domainObjectName}';

/*
By combining reducers, you now have to use the namespace of the reducer
when mapping State-to-Props in your components
 */
export default combineReducers({
    ${domainObjectName},
    ${domainObjectName}s
});

 