//DomainDetail-template.js
#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React, {Component} from 'react';
import axios from 'axios';

class ${domainClassName}Detail extends Component{

	state = {
	  ${domainObjectName}ToEdit : null
	};

	constructor(props){
	  super(props) ;
	  this.handleChange = this.handleChange.bind(this) ;
	}

	componentDidMount () {
	  console.log('Loading: ' + this.props.match.params.id);
	  axios.get('/${domainObjectName}/' + this.props.match.params. id)
	  .then(response => {
	      this.setState({${domainObjectName}ToEdit : response.data});
	  });
	}

	save${domainClassName} = () => {
	  console. log(this.state.${domainObjectName}ToEdit) ;
	  axios.put('/${domainObjectName}/' + this.props.match.params. id, this. state.${domainObjectName}ToEdit) ;
	}

	handleChange(event){
	  console.log(event.target.name);
	  const fieldName = event.target.name;
	  let tempObject = {...this.state.${domainObjectName}ToEdit};
	  tempObject[fieldName] = event.target.value;
	  this.setState({${domainObjectName}ToEdit : tempObject});
	}

	render () {
	  let screen = <div>Loading</div>;
	  if(this.state.${domainObjectName}ToEdit !== null) {
	    screen = (
		<div className="row">
		  <div className="col-md-12">
		    <form>
		    #foreach($key in $attrs.keySet() )
		    <div className="form-group">
		    <label for="${key}">${key}</label>
		    <input className="form-control" id="${key}" name="${key}"
		        value={this.state.${domainObjectName}ToEdit.${key}}
		        onChange={this.handleChange}/>
		    </div>
		    #end
		    <div>
		    <button onClick={() => this. save${domainClassName}()}>Save</button>
		    </div>
		    </form>
		  </div>
		</div>);
	}
	return screen;
	}
}
export default ${domainClassName}Detail;
