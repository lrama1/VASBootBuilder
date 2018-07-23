#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React, { Component } from 'react';
import axios from 'axios';

class ${domainClassName}List extends Component{
state = {
headers : [
#set($index = 0)
#foreach($key in $attrs.keySet() )
#if($index == 0) '${key}' #else , '${key}'#end
#set($index = $index + 1)
#end
],
${domainObjectName}s : []
};

constructor(props) {
	super(props);
	console.log(props);
	console.log(process.env);
}

componentDidMount() {
	axios.get("/${domainObjectName}s?page=1&per_page=10").
	then(response => {
		console.log(response);
		this.setState({${domainObjectName}s: response.data.rows});
	});
}

summaryClickedHandler = (id) => {
	this.props.history.push({pathname: '/${domainObjectName}/' + id});
}

render() {
	const listOfHeaders = this.state.headers.map((header, index) => {
		return (
			{
				key: header,
				label: header
			}
		)
	});
		
    //add an edit button for each row
    listOfHeaders.push(
		{
			key: 'action',
			label: 'Actions',
			emptyValue: (item) => (
			<div>
				<button
				onClick={() => this.summaryClickedHandler(item.${domainClassIdAttributeName})}	>
				Edit Row
				</button>
			</div>
		  )
        }
    );
    
    const dataRows = this.state.${domainObjectName}s.map(($domainObjectName, index)=> {
 	   return (
 	     <tr key= {$domainObjectName.${domainClassIdAttributeName}}>
 			 <td><button onClick={() => this.summaryClickedHandler($domainObjectName.${domainClassIdAttributeName})}>Edit</button></td>
 			#foreach($key in $attrs.keySet() )
 			  <td>{$domainObjectName.$key}</td>
 			#end
 		 </tr>
 		)
 		}
 	);
			
	
			
return(
	<div>
	<table className="table">
	  <thead>
	    
	  </thead>
	  <tbody>
	  {dataRows}
	  </tbody>
	</table>
	</div>
	);
  }
}
export default ${domainClassName}List;