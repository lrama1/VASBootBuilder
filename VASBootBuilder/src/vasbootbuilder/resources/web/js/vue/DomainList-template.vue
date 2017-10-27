#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import VueResource from 'vue-resource'
Import VuePaginator from 'vuejs-paginator'

<template>
	<div class="row">
		<div class="col-md-12">
			<h2>{{label}}</h2>
			<table class="table table-bordered table-striped">
				<thead>
				<tr>
				<th>Select</th>
				#foreach($key in $attrs.keySet() )
				<th>$key</th>
				#end
				</tr>
				</thead>
				<tbody>
					<tr v-for="${domainObjectName} in listOf${domainClassName}s">
				          <!--td><a v-bind:href="'#/${domainObjectName}/' + ${domainObjectName}.${domainClassIdAttributeName}">E</a></td-->
				          <td><a @click="editRecord(${domainObjectName}.${domainClassIdAttributeName})">Edit</a></td>
				          #foreach($key in $attrs.keySet() )
							<td>{{${domainObjectName}.${key}}}</td>
						  #end
					</tr>
				</tbody>	  
			</table>
		</div>
		<div class="col-md-12">
			<ul class="pager">
		      <li><a @click="previousPage()" v-if="currentPage > 1" >Previous</a></li>
		      <li><a @click="nextPage()" v-if="currentPage < lastPage">Next</a></li>
		    </ul>
	    </div>
	</div>
</template>

<script>
  export default {
    data () {
      return {
        listOf${domainClassName}s: [],
        someData: {title: 'This is a test'},
        currentPage : 1,
        lastPage : 1
      }
    },
    created () {
      var resource = this.$resource('/${projectName}/${domainObjectName.toLowerCase()}s')
      resource.query({page: 1, per_page: 4}).then((response) => {
        this.listOf${domainClassName}s = response.data.rows
        this.lastPage = response.data.lastPage
      }, (response) => {
      // error callback
        console.log('Error:' + response.statusText)
      })
    },
    props : ['label'],
    methods: {
      dummyFunction () {
        console.log('dummy')
      },
      editRecord(id){
          alert(id)
		  this.#[[$router]]#.push('/${domainObjectName}/' + id)
	  },
      previousPage () {
      	  this.currentPage = this.currentPage - 1
      	  var resource = this.$resource('/${projectName}/${domainObjectName.toLowerCase()}s')
      	  resource.query({page: this.currentPage, per_page: 4}).then((response) => {
      		this.listOf${domainClassName}s = response.data.rows
      	      }, (response) => {
      	      // error callback
      	        console.log('Error:' + response.statusText)
      	  })
        },
	  nextPage () {
	  	console.log('next page')
	  	this.currentPage = this.currentPage + 1
	  	var resource = this.$resource('/${projectName}/${domainObjectName.toLowerCase()}s')
	  	resource.query({page: this.currentPage, per_page: 4}).then((response) => {
	  	  this.listOf${domainClassName}s = response.data.rows
	  	}, (response) => {
	  	      // error callback
	  	        console.log('Error:' + response.statusText)
	  	 })
	  }
    }   
  }
</script>