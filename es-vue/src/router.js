import { createRouter, createWebHistory } from 'vue-router'
// 导入父路由组件（Home）和子路由组件（个人中心、发布商品等）

import { api } from './api/index.js'
import register from './components/user/register.vue'
import login from './components/user/login.vue'
import home from './components/user/home.vue'


const routes = [
    { path: '/login', component: login },
    { path: '/register', component: register },
    { path: '/home', component: home },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})



export default router