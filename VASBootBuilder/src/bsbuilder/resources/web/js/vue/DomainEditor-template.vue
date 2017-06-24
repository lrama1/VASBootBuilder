#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})

<template>
	<div class="row">
		#foreach($key in $attrs.keySet() )
		<div class="form-group">
	    	<label for="${key}">${key}</label>
	    	<input type="text" class="form-control" id="${key}" name="${key}" v-validate="'required'" data-vv-as="${key.toUpperCase()}" v-model="${domainObjectName}.${key}">
	    	<span v-show="errors.has('${key}')">{{ errors.first('${key}') }}</span>
	  	</div>
	  	#end 
	  	<div>
	  		<button type="button" class="btn btn-primary" v-bind:disabled="saveDisabled" @click="save${domainClassName}">Save</button>
	  		<button type="button" class="btn btn-primary" v-bind:disabled="saveDisabled" @click="update${domainClassName}">Update</button>
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
      return {
        ${domainObjectName}
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