#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {Button} from "primereact/button";

function ${domainClassName}List({history, fetch${domainClassName}, fetchAll${domainClassName}s, ${domainObjectName}s, first, totalRecords,
    on${domainClassName}sChangePage, sortSettings, onSort, createNew${domainClassName}}){

    /*function pageAction({first,rows, page}){
        console.log(first)
        fetchAll${domainClassName}s('${domainObjectName.toLowerCase()}s?per_page=' + rows + '&page=' + (page+1), first )
    }*/
    
    function buttonClicked(event){
        fetch${domainClassName}('${domainObjectName}/' + event.target.value)
        //tell route to display the Edit screen
        history.push({pathname: '/${domainObjectName}'});
    }

    function actionTemplate(rowData, column){
        return (
            <Button id={rowData.${domainClassIdAttributeName}} value={rowData.${domainClassIdAttributeName}} onClick={buttonClicked}>Edit</Button>
        )
    }
    
    function dateBody(rowData, {field}) {
        const date = new Date(Date.parse(rowData[field]))
        return (<>
            {date.toDateString()}
        </>)
    }
    
    function addNewRecord(){
      createNew${domainClassName}();	
      history.push({pathname: '/${domainObjectName}'});
    }
       
    /*
    render a table component
     */
    return (
        <div className="layout-dashboard">
	        <div>
	          <Button onClick={addNewRecord}>Add New</Button>    
	        </div>
	        <DataTable className="p-datatable-products" first={first} paginator={true} value={${domainObjectName}s} lazy={true} rows={10} totalRecords={totalRecords}
	            onPage={on${domainClassName}sChangePage} selectionMode="single" responsiveLayout="stack" breakpoint="960px"
	            sortField={sortSettings.sortField} sortOrder={sortSettings.sortOrder} onSort={onSort}>
	        #foreach($key in $attrs.keySet() )
	        #if($attrs.get(${key}) == 'java.util.Date')
	        <Column field="$key" header="${key.toUpperCase()}" sortable body={dateBody}/>
	        #else
	        <Column field="$key" header="${key.toUpperCase()}" sortable/>	
	        #end	
	        #end
	        <Column body={actionTemplate}/>
	        </DataTable>
        </div>
    )
};

export default ${domainClassName}List;