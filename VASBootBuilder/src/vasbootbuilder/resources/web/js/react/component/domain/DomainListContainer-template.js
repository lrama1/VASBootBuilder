#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

import {connect} from 'react-redux';
import ${domainClassName}List from '../components/${domainClassName}List'
import {fetch${domainClassName}, fetchAll${domainClassName}s, ${domainObjectName}sChangePage} from '../actions/${domainObjectName.toLowerCase()}';


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
        fetch${domainClassName}(url){
            dispatch(fetch${domainClassName}(url))
        },
        on${domainClassName}sChangePage({first, rows, page}){
            console.log('change page')
            dispatch(${domainObjectName}sChangePage(first, rows, page))
            dispatch(fetchAll${domainClassName}s())
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(${domainClassName}List);