#set($domainObjectName = ${domainClassName.substring(0,1).toLowerCase()} + ${domainClassName.substring(1)})
import Vue from 'vue'
import App from './App.vue'
import Home from './components/Home.vue'
import ${domainClassName} from './components/${domainClassName}.vue'
import ${domainClassName}s from './components/${domainClassName}s.vue'

import VueRouter from 'vue-router'
import VueResource from 'vue-resource'
import VeeValidate from 'vee-validate'

// We want to apply VueResource and VueRouter
// to our Vue instance
Vue.use(VueResource)
Vue.use(VueRouter)
Vue.use(VeeValidate)

const routes = [
  {path: '/home', component: Home},
  {path: '/${domainObjectName}/:id', component: ${domainClassName}},
  {path: '/${domainObjectName}s', component: ${domainClassName}s}
]

const router = new VueRouter({
  routes // short for routes: routes
})

const App1 = Vue.extend(App)
new App1({router}).$mount('#app')