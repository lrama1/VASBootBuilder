#set($domainVar = $domainClassName.substring(0,1).toLowerCase() + $domainClassName.substring(1))
CREATE TABLE ${domainVar} (
  #foreach($key in $attrs.keySet() )
    #if($key == ${domainClassIdAttributeName})
      $key VARCHAR(64) PRIMARY KEY
    #else
       ,$key VARCHAR(64)
    #end
  #end
  );
  