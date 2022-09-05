#set($domainVar = $domainClassName.substring(0,1).toLowerCase() + $domainClassName.substring(1))
CREATE TABLE ${domainVar} (
  #foreach($key in $attrs.keySet() )
    #set($columnName = "")
    #set($datatype = "")
    #if(${prepForHSQL} == true)
      #set($columnName = $oracleNames.get(${key}))
    #else
      #set($columnName = $key)
    #end
    #if($attrs.get(${key}) == 'String')
      #set($datatype = "VARCHAR(64)")
    #elseif($attrs.get(${key}) == 'java.util.Date')
      #set($datatype = "DATE")
    #elseif($attrs.get(${key}) == 'Integer')
      #set($datatype = "NUMERIC")
    #end
    #if($key == ${domainClassIdAttributeName})
      $columnName $datatype PRIMARY KEY
    #else
       ,$columnName $datatype
    #end
  #end
  );
  