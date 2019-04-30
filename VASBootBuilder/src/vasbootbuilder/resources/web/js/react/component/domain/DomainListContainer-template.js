#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react';
import {connect, dispatch} from 'react-redux';
import ${domainClassName}List from '../components/${domainClassName}List'
import {fetch${domainClassName}} from '../actions';


const mapStateToProps = (state) => {
    console.log(state);
    return {
        ${domainObjectName}s: state.${domainObjectName}s
    };
};

const mapDispatchToProps = (dispatch) => {
    return{
        onSelect${domainClassName}(${domainObjectName}){
            dispatch(fetch${domainClassName}(${domainObjectName}))
        },
        fetch${domainClassName}(url){
            dispatch(fetch${domainClassName}(url))
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(${domainClassName}List);