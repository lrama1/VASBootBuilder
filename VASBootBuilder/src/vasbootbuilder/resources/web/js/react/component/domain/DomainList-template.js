#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react'
import {connect, dispatch} from 'react-redux'

import { fetch${domainClassName} } from '../actions';

function ${domainClassName}List(props){

    function select${domainClassName}(${domainObjectName}){
        //dispatch an action to fetch the selected ${domainObjectName}
        props.fetch${domainClassName}('/${domainObjectName}/' + ${domainObjectName}.${domainClassIdAttributeName})
        //tell route to display the Edit screen
        props.history.push({pathname: '/${domainObjectName}'});
    }


    /*
    Iterate thru rows of ${domainObjectName}s and create a tr component for each
    NOTE: look up examples of the 'map' function on the web if you're unfamiliar with it
     */
    const rows = props.${domainObjectName}s.map((${domainObjectName}) => {
        return (
            <tr key={${domainObjectName}.${domainClassIdAttributeName}}>
                #foreach($key in $attrs.keySet() )
   			    <td>{$domainObjectName.$key}</td>
   			    #end
                <td>
                    <button className="btn btn-primary" onClick={() => select${domainClassName}(${domainObjectName})}>Select</button>
                </td>
            </tr>
        );
    });

    /*
    render a table component
     */
    return (
        <div>
            <table className="table">                
                <tbody>
                {rows}
                </tbody>
            </table>
        </div>
    )
};

const mapStateToProps = (state) => {
    console.log(state);
    return {
        ${domainObjectName}s: state.${domainObjectName}sReducer
    };
};

const mapDispatchToProps = (dispatch) => {
    return{
        onSelect${domainClassName}: (${domainObjectName}) => dispatch(fetch${domainClassName}(${domainObjectName})),
        fetch${domainClassName}: (url) => dispatch(fetch${domainClassName}(url))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(${domainClassName}List);