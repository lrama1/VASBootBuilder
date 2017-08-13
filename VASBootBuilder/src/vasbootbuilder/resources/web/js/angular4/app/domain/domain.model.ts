export class ${domainClassName} {
  constructor(#foreach($key in $attrs.keySet())#if($foreach.index == 0)public ${key}: string#else, public ${key}: string#end#end) {}
}
