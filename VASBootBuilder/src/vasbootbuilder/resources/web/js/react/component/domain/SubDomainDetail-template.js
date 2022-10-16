
#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

import React from 'react'

import { InputText } from 'primereact/inputtext';
import { Calendar } from 'primereact/calendar';
import {Button} from 'primereact/button';

function ${domainClassName}Edit({${domainObjectName}, onEdit${domainClassName}, onAdd${domainClassName}, onRemove${domainClassName}, index}){
	return(
	  <div>
	    #foreach($key in $attrs.keySet() )
	      <div>
	        <label htmlFor="${key}">${key}</label>
	        #if($attrs.get(${key}) == 'java.util.Date')
		      <Calendar dateFormat="mm-dd-yy"  id="${key}" name="${key}" value={${domainObjectName}.${key}}
	        onChange={(e) => onEdit${domainClassName}(e, index)}></Calendar>	  
		    #else	  
		      <InputText id="${key}" name="${key}" value={${domainObjectName}.${key}}
	           onChange={(e) => onEdit${domainClassName}(e, index)}/>
            #end	        
	      </div>
	    #end
	    <div>
		<Button onClick={()=>onRemove${domainClassName}(index)}>Remove</Button>
	    </div>
	  </div>
	)
}

export default ${domainClassName}Edit;