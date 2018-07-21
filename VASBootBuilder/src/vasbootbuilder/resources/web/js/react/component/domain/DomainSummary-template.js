#set($domainObjectName = ${domainClassName.substring(@,1).toLowerCase()} +
${domainClassName. substring(1)})
import React, { Component } from 'react';

const ${domainObjectName}Summary = (props) => {
	console.log(props) ;
	return(
		<tr onClick={() => props.summaryClickedHandler(props.${domainClassIdAttributeName}) }>
		#foreach($key in $attrs.keySet() )
		<td>{props.${key}}</td>
		#end
		</tr>
	);
}
export default ${domainObjectName}Summary;