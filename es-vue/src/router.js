import { createRouter, createWebHistory } from 'vue-router'
// 导入父路由组件（Home）和子路由组件（个人中心、发布商品等）

import { api } from './api/index.js'
import register from './components/user/register.vue'
import login from './components/user/login.vue'
import home from './components/user/home.vue'
import publishGoods from './components/product/publishGoods.vue'
import myProduct from './components/product/myProduct.vue'
//import updateProduct from './components/product/update.vue'
import searchProduct from './components/product/searchProduct.vue'
import productDetail from './components/product/productDetail.vue'
import favoriteList from './components/user/favoriteList.vue'



const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', component: login },
    { path: '/register', component: register },
    { path: '/home', component: home },
    { path: '/publishGoods', component: publishGoods },
    { path: '/myProduct', component: myProduct },
   // { path: '/updateProduct/:id', component: updateProduct },
    { path: '/searchProduct', component: searchProduct },
    { path: '/productDetail/:id', component: productDetail },
    { path: '/favoriteList', component: favoriteList },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})



export default router