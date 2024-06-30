#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

import { DataTable } from "primereact/datatable";
import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Column } from "primereact/column";
import { Button } from "primereact/button";
import { fetch${domainClassName}s, pageChanged, sorted } from "./${domainObjectName}sSlice";
import { fetch${domainClassName} } from "./${domainObjectName}Slice";
import { useHistory } from "react-router-dom";

function ${domainClassName}List() {
  const dispatch = useDispatch();

  const {
    entities: ${domainObjectName}s,
    perPage,
    first,
    totalRecords,
    page,
    sortField,
    sortOrder,
  } = useSelector((state) => state.${domainObjectName}s);

  const history = useHistory();

  useEffect(() => {
    dispatch(fetch${domainClassName}s());
  }, [dispatch]);

  function on${domainClassName}sChangePage({ first, rows, page }) {
    dispatch(pageChanged({ first, rows, page }));
    dispatch(fetch${domainClassName}s());
  }

  function onSort({ sortField, sortOrder }) {
    dispatch(sorted({ sortField, sortOrder }));
    dispatch(fetch${domainClassName}s());
  }

  function buttonClicked(event) {
    const ${domainClassIdAttributeName} = event.target.value;
    dispatch(fetch${domainClassName}("${domainObjectName}/" + ${domainClassIdAttributeName}));
    history.push({ pathname: "/${domainObjectName}" });
  }

  function actionTemplate(rowData, column) {
    return (
      <Button
        id={rowData.${domainClassIdAttributeName}} 
        value={rowData.${domainClassIdAttributeName}} 
        onClick={buttonClicked}
      >
        Edit
      </Button>
    );
  }
  return (
	    <div className="layout-dashboard">
	        <div>
	          <Button>Add New</Button>    
	        </div>
	        <DataTable className="p-datatable-products" first={first} 
	        	paginator={true} value={${domainObjectName}s} 
	        	lazy={true} rows={10} 
	        	totalRecords={totalRecords}
	          onPage={on${domainClassName}sChangePage} 
	          selectionMode="single" 
	          responsiveLayout="stack" 
	          breakpoint="960px"
	          sortField={sortField} 
	          sortOrder={sortOrder} 
	          onSort={onSort}>
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
	);
}

export default ${domainClassName}List;
