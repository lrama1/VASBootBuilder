#set($domainVar = $domainClassName.substring(0,1).toLowerCase() + $domainClassName.substring(1))
#set($start = 0)
#set($end = 13)
#set($range = [$start..$end])
#set($newline = '\n')

INSERT INTO ${domainVar}
(
#foreach($key in $attrs.keySet() )
  #if($foreach.index == 0)
  ${key}
  #else
  ,${key}
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
   			#set($line = $line + "'Sample-" + ${key} + ${i} + "'")
   		#else##
   			#set($line =  $line + ',' + "'Sample-" + ${key} + ${i} + "'")
   		#end##
   		#set($index = $index + 1)
   	#end##
(${line.trim()})
#end##
;

  