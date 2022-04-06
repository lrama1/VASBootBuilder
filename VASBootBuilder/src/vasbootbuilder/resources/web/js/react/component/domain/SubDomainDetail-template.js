
#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

import React from 'react'

import { InputText } from 'primereact/inputtext';

function ${domainClassName}Edit({${domainObjectName}, onEdit${domainClassName}, onAdd${domainClassName}, onRemove${domainClassName}, index}){
	return(
	  <div>
	    #foreach($key in $attrs.keySet() )
	      <div>
	        <label htmlFor="${key}">${key}</label>
	        <InputText id="${key}" name="${key}" value={${domainObjectName}.${key}} onChange={(e) => onEdit${domainClassName}(e, index)} />
	      </div>
	    #end
	  </div>
	)
}

export default ${domainClassName}Edit;