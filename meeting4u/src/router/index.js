import Vue from 'vue'
import VueRouter from 'vue-router'
import HomePage from '../views/homepage.vue'
import Home from '../views/home.vue'
import HistoryPage from '../views/historypage.vue'
import MeetingPage from '../views/meetingpage.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home,
    redirect:'/home',
    children: [
      {
        path: '/home',
        name: 'HomePage',
        component: HomePage
      },
      {
        path: '/history',
        name: 'HistoryPage',
        component: HistoryPage
      },
      {
        path: '/meeting',
        name: 'MeetingPage',
        component: MeetingPage
      }
    ]

  },
]

const router = new VueRouter({
  routes
})

const whiteList = ["/history", "/meeting"]
router.beforeEach((to, from, next) => {
  let hasToken = window.sessionStorage.getItem("token")
  if (whiteList.indexOf(to.path) !== -1) {
    if (hasToken) {
      next()
    } else {
      next("/")
    }
  } else {
    next()
  }
})
export default router
