<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <h2>Components</h2>
    <${vueComponentTagName}-list></${vueComponentTagName}-list>
  </div>
</template>

<script>
import ${domainClassName}s from './${domainClassName}s.vue'
export default {
  name: 'hello',
  components : {
	  '${vueComponentTagName}-list' : ${domainClassName}s
  },
  data () {
    return {
      msg: 'Welcome to Your Vue.js App'
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>