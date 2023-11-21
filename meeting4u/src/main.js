import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios'
import {library} from '@fortawesome/fontawesome-svg-core'

import {fas} from '@fortawesome/free-solid-svg-icons'
import { far } from '@fortawesome/free-regular-svg-icons'

import {FontAwesomeIcon}
  from '@fortawesome/vue-fontawesome'

library.add(fas,far)
Vue.component('font-awesome-icon', FontAwesomeIcon)

const service = axios.create({
  timeout: 5000
})

service.interceptors.request.use((config) => {
  if (config.url.indexOf("/meeting") == -1) {
    return config;
  }
  if (sessionStorage.getItem("token") != undefined) {
    config.headers.Token = sessionStorage.getItem("token");
  }
  return config;
}, error => {
  Promise.reject(error)
})

service.interceptors.response.use(
  response => {
    if (response.data.code == 401) {
      window.sessionStorage.removeItem("token")
      window.sessionStorage.removeItem("username")
      window.sessionStorage.removeItem("logined")
    }
    return response.data;
  },
  error => {
    return Promise.reject(error)
  }
)
Vue.prototype.$axios = service
Vue.use(ElementUI);
Vue.config.productionTip = false
Vue.prototype.$srsServerAPIURL = 'http://172.16.1.72:1985/';
Vue.prototype.$srsServerRTCURL = 'webrtc://172.16.1.72:8080/live/';
Vue.prototype.$meetingServerURL = "http://172.16.1.72:9898/meeting/"
Vue.prototype.$meetingWebsocketURL = "http://172.16.1.72:9999/"


new Vue({
  router,
  render: h => h(App),
}).$mount('#app')
