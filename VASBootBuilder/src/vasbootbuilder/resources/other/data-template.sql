#set($domainVar = $domainClassName.substring(0,1).toLowerCase() + $domainClassName.substring(1))
#set($start = 0)
#set($end = 13)
#set($range = [$start..$end])
#set($newline = '\n')

INSERT INTO ${domainVar}
(
#foreach($key in $attrs.keySet() )
  #set($columnName = "")
  #if(${prepForHSQL} == true)
    #set($columnName = $oracleNames.get(${key}))
  #else
    #set($columnName = $key)
  #end    
  #if($foreach.index == 0)
  $columnName
  #else
  ,$columnName
  #end
#end
)
VALUES
#foreach($i in $range)
   #if($i >= 1)
   ,
   #end##
	#set($line = '')
	#set($index = 0)
   	#foreach($key in $attrs.keySet() ) 
   		#if($index == 0)
   		  #if($attrs.get(${key}) == 'String')
   			#set($line = $line + "'Sample-" + ${key} + ${i} + "'")
   		  #elseif($attrs.get(${key}) == 'java.util.Date')
   		    #set($line = $line + "'2018-08-21'")
   		  #elseif($attrs.get(${key}) == 'Number')
   		    #set($line = $line + "'1000'")
   		  #end##  
   		#else##   			
   		  #if($attrs.get(${key}) == 'String')
   			#set($line = $line + ',' + "'Sample-" + ${key} + ${i} + "'")
   		  #elseif($attrs.get(${key}) == 'java.util.Date')
   		    #set($line = $line + ',' + "'2018-08-21'")
   		  #elseif($attrs.get(${key}) == 'Integer')
   		    #set($line = $line + ',' + "'1000'")
   		  #end## 
   		#end##
   		#set($index = $index + 1)
   	#end##
(${line.trim()})
#end##
;

  