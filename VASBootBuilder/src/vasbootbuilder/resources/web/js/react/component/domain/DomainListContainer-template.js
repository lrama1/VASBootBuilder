#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

import {connect} from 'react-redux';
import ${domainClassName}List from '../components/${domainClassName}List'
import {fetch${domainClassName}, fetchAll${domainClassName}s, ${domainObjectName}sChangePage, ${domainObjectName}sSort, createNew${domainClassName}} from '../actions/${domainObjectName}';


export const mapStateToProps = (state) => {
    console.log(state);
    return {
        ${domainObjectName}s: state.${domainObjectName}s.records,
        totalRecords: state.${domainObjectName}s.totalRecords,
        first: state.${domainObjectName}s.first,
        sortSettings: state.${domainObjectName}s.sortSettings
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
        },
        onSort({sortField, sortOrder}){
            dispatch(${domainObjectName}sSort(sortField, sortOrder))
            dispatch(fetchAll${domainClassName}s())
        },
        createNew${domainClassName}(){
        	dispatch(createNew${domainClassName}())
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(${domainClassName}List);