#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

import React from "react";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { useDispatch, useSelector } from "react-redux";
import { edited, save${domainClassName} } from "./${domainObjectName}Slice";

function ${domainClassName}Edit() {
  const dispatch = useDispatch();

  const selected${domainClassName} = useSelector((state) => state.${domainObjectName}.entity);

  function buttonEventHandler(event) {
    dispatch(save${domainClassName}(selected${domainClassName}));
    event.preventDefault();
  }

  function onEdit${domainClassName}(event) {
    const { name, value } = event.target;
    dispatch(edited({ name, value }));
  }

  return(
      <div className="p-grid">
          <form>
            #foreach($key in $attrs.keySet() )
            <div className="p-col-4">
		      <label htmlFor="${key}">${key}</label>
		      #if($attrs.get(${key}) == 'java.util.Date')
		      <Calendar dateFormat="mm-dd-yy"  id="${key}" name="${key}" value={selected${domainClassName}.${key}}
	          onChange={onEdit${domainClassName}}></Calendar>	  
		      #else	  
		      <InputText id="${key}" name="${key}" value={selected${domainClassName}.${key}}
		          onChange={onEdit${domainClassName}}/>
              #end
		      </div>
		    #end
		    
            <Button id="saveButton" onClick={buttonEventHandler}>Save</Button>
          </form>
      </div>
    );
}

export default ${domainClassName}Edit;
