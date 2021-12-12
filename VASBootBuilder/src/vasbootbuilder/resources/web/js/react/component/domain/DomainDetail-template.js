//DomainDetail-template.js
#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react';
import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";

function ${domainClassName}Edit({selected${domainClassName}, onEdit${domainClassName}, onSave${domainClassName}}){

    function buttonEventHandler(event){
        onSave${domainClassName}('${domainObjectName.toLowerCase()}/' + selected${domainClassName}.${domainClassIdAttributeName},
        		selected${domainClassName});
        event.preventDefault();
    }

    return(
      <div className="p-grid">
          <form>
            #foreach($key in $attrs.keySet() )
            <div className="p-col-4">
		      <label htmlFor="${key}">${key}</label>
		      <InputText id="${key}" name="${key}" value={selected${domainClassName}.${key}}
		          onChange={onEdit${domainClassName}}/>
		    </div>
		    #end
		    
            <Button id="saveButton" onClick={buttonEventHandler}>Save</Button>
          </form>
      </div>
    );
}

export default ${domainClassName}Edit;