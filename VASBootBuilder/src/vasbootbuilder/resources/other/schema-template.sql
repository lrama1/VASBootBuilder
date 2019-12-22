#set($domainVar = $domainClassName.substring(0,1).toLowerCase() + $domainClassName.substring(1))
CREATE TABLE ${domainVar} (
  #foreach($key in $attrs.keySet() )
    #set($columnName = "")
    #if(${prepForHSQL} == true)
      #set($columnName = $oracleNames.get(${key}))
    #else
      #set($columnName = $key)
    #end
    #if($key == ${domainClassIdAttributeName})
      $columnName VARCHAR(64) PRIMARY KEY
    #else
       ,$columnName VARCHAR(64)
    #end
  #end
  );
  