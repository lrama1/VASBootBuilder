#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import React from 'react'
import {DataTable} from 'primereact/components/datatable/DataTable'
import {Column} from 'primereact/components/column/Column'

function ${domainClassName}List({history, fetch${domainClassName}, fetchAll${domainClassName}s, ${domainObjectName}s, first, totalRecords}){

    function pageAction({first,rows, page}){
        console.log(first)
        fetchAll${domainClassName}s('${domainObjectName}s?per_page=' + rows + '&page=' + (page+1), first )
    }
    
    function buttonClicked(event){
        fetch${domainClassName}('/${domainObjectName}/' + event.target.value)
        //tell route to display the Edit screen
        history.push({pathname: '/${domainObjectName}'});
    }

    function actionTemplate(rowData, column){
        return (
            <button id={rowData.${domainClassIdAttributeName}} value={rowData.${domainClassIdAttributeName}} onClick={buttonClicked}>Edit</button>
        )
    }
       
    /*
    render a table component
     */
    return (
        <div>
        <DataTable first={first} paginator={true} value={${domainObjectName}s} lazy={true} rows={10} totalRecords={totalRecords}
            onPage={pageAction} selectionMode="single">
        #foreach($key in $attrs.keySet() )
        <Column field="$key" header="${key.toUpperCase()}"/>
        #end
        <Column body={actionTemplate}/>
        </DataTable>
        </div>
    )
};

export default ${domainClassName}List;