//DomainDetail-template.js
#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react'

function ${domainClassName}Edit({selected${domainClassName}, onEdit${domainClassName}, onSave${domainClassName}}){

    function buttonEventHandler(event){
        onSave${domainClassName}('${domainObjectName.toLowerCase()}/' + selected${domainClassName}.${domainClassIdAttributeName},
        		selected${domainClassName});
        event.preventDefault();
    }

    return(
      <div>
          <form>
            #foreach($key in $attrs.keySet() )
            <div className="form-group">
		      <label htmlFor="${key}">${key}</label>
		      <input className="form-control" id="${key}" name="${key}" value={selected${domainClassName}.${key}}
		          onChange={onEdit${domainClassName}}/>
		    </div>
		    #end
		    
            <button id="saveButton" onClick={buttonEventHandler}>Save</button>
          </form>
      </div>
    );
}

export default ${domainClassName}Edit;