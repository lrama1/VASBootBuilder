//DomainDetail-template.js
#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

import React from 'react'
import {connect} from 'react-redux'
import { dispatch } from 'react-redux'
import { edit${domainClassName}, save${domainClassName}} from "../actions";

function ${domainClassName}Edit(props){
    const handler = (event) =>{
        const {name, value} = event.target;
        props.onEdit${domainClassName}({...props.selected${domainClassName}, [name] : value});
    }

    const buttonEventHandler = (event) => {
        props.onSave${domainClassName}('/${domainObjectName}/' + props.selected${domainClassName}.${domainClassIdAttributeName},
        		props.selected${domainClassName});
        event.preventDefault();
    }

    return(
      <div>
          <form>
            #foreach($key in $attrs.keySet() )
		    <div className="form-group">
		    <label for="${key}">${key}</label>
		    <input className="form-control" id="${key}" name="${key}" value={props.selected${domainClassName}.${key}}
		        onChange={handler}/>
		    </div>
		    #end
		    
            <button onClick={buttonEventHandler}>Save</button>
          </form>
      </div>
    );
}

const mapStateToProps = (state) => {
    console.log(state);
    return {
        selected${domainClassName}: state.${domainObjectName}FetchReducer
    };
};

const mapDispatchToProps = (dispatch) => {
    return{
        onEdit${domainClassName}: (${domainObjectName}) => {
            dispatch(edit${domainClassName}(${domainObjectName}))
        },
        onSave${domainClassName}: (url, ${domainObjectName}) => {
            dispatch(save${domainClassName}(url, ${domainObjectName}))
        }
    }
}
export default connect(mapStateToProps, mapDispatchToProps)(${domainClassName}Edit);