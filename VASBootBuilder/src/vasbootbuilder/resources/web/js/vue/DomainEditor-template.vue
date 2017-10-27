#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

<template>
	<div class="row">
		#foreach($key in $attrs.keySet() )
        <div class="col-md-12">
          <div class="form-group">
	    	<label for="${key}">${key}</label>
	    	#if ($fieldTypes.get($key) == "TextField")
	    	<input type="text" class="form-control" id="${key}" name="${key}" v-validate="'required'" data-vv-as="${key.toUpperCase()}" v-model="${domainObjectName}.${key}">
	    	#elseif ($fieldTypes.get($key) == "DropDown")
	    	<select class="form-control" v-model="${domainObjectName}.${key}">
              <option v-for="option in ${key.toLowerCase()}s" v-bind:value="option.name">{{option.value}}</option>
            </select>
	    	#elseif ($fieldTypes.get($key) == "TextArea")
	    	<textarea rows="4" cols="60" class="form-control"  id="${key}" name='${key}' v-model="${domainObjectName}.${key}"></textarea>
	    	#end
	    	<span v-show="errors.has('${key}')">{{ errors.first('${key}') }}</span>
          </div>
        </div>
	  	#end
        <div class="col-md-12"> 
          <div>
            <button type="button" class="btn btn-primary" v-bind:disabled="saveDisabled" @click="save${domainClassName}">Save</button>
            <button type="button" class="btn btn-primary" v-bind:disabled="saveDisabled" @click="update${domainClassName}">Update</button>
          </div>
        </div>
	  	
	</div>
</template>

<script>
  export default {
    data () {
      let ${domainObjectName} = {
        firstName: '',
        lastName: ''
      }
      #foreach($key in $attrs.keySet() )		
      #if ($fieldTypes.get($key) == "DropDown")
	  let ${key.toLowerCase()}s	= []
      #end
      #end	
      return {
        ${domainObjectName}
        #foreach($key in $attrs.keySet() )
        #if ($fieldTypes.get($key) == "DropDown")
		, ${key.toLowerCase()}s	
		#end
        #end
      }
    },
    created () {
      console.log(this.$route.params.id)
      if (this.$route.params.id) {
        console.log('found a value')
        var resource = this.$resource('/${projectName}/${domainObjectName.toLowerCase()}/' + this.$route.params.id)
        resource.query().then((response) => {
          this.${domainObjectName} = response.data
        }, (response) => {
        // error callback
          console.log('Error:' + response.statusText)
        })
      }
      #foreach($key in $attrs.keySet() )		
		#if ($fieldTypes.get($key) == "DropDown")
			var resource${foreach.index} = this.$resource('/${projectName}/${key.toLowerCase()}s')
	        resource${foreach.index}.query().then((response) => {
	          this.${key.toLowerCase()}s = response.data
	        }, (response) => {
	        // error callback
	          console.log('Error:' + response.statusText)
	        })
		#end
	  #end
    },
    computed: {
    	saveDisabled(){
    		return this.errors.any()
    	}
    },
    methods: {
      save${domainClassName} () {
        console.log(this.${domainObjectName})
        var resource = this.$resource('/${projectName}/${domainObjectName.toLowerCase()}')
        resource.save(this.${domainObjectName}).then((response) => {
        // success callback
        }, (response) => {
        // error callback
          console.log('Error:' + response.statusText)
        })
      },
      update${domainClassName} () {
        console.log(this.${domainObjectName})
        var resource = this.$resource('/${projectName}/${domainObjectName.toLowerCase()}/' + this.$domainObjectName.$domainClassIdAttributeName)
        resource.update(this.${domainObjectName}).then((response) => {
        // success callback
        }, (response) => {
        // error callback
        })
      }
    }
  }
</script>