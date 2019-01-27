//DomainDetail-template.js
#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

import React from 'react'

function ${domainClassName}Edit(props){
    const changeHandler = (event) =>{
        const {name, value} = event.target;
        props.onEdit${domainClassName}([name], value);
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
		        onChange={changeHandler}/>
		    </div>
		    #end
		    
            <button onClick={buttonEventHandler}>Save</button>
          </form>
      </div>
    );
}

export default ${domainClassName}Edit;