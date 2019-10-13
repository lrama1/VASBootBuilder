#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

import {connect} from 'react-redux';
import ${domainClassName}List from '../components/${domainClassName}List'
import {fetch${domainClassName}, fetchAll${domainClassName}s} from '../actions/${domainObjectName}';


export const mapStateToProps = (state) => {
    console.log(state);
    return {
        ${domainObjectName}s: state.${domainObjectName}s.records,
        totalRecords: state.${domainObjectName}s.totalRecords,
        first: state.${domainObjectName}s.first
    };
};

export const mapDispatchToProps = (dispatch) => {
    return{
        onSelect${domainClassName}(${domainObjectName}){
            dispatch(fetch${domainClassName}(${domainObjectName}))
        },
        fetch${domainClassName}(url){
            dispatch(fetch${domainClassName}(url))
        },
        fetchAll${domainClassName}s(url, first){
            dispatch(fetchAll${domainClassName}s(url, first))
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(${domainClassName}List);