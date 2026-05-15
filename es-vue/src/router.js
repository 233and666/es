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
import favoriteList from './components/favorite/favoriteList.vue'
import myOrder from './components/order/myOrder.vue'
import profile from './components/user/profile.vue'
import service from './components/message/service.vue'

import adminLogin from './components/admin/a-login.vue'
import adminHome from './components/admin/a-home.vue'
import adminRegister from './components/admin/a-register.vue'
import adminProduct from './components/admin/a-product.vue'
import adminOrder from './components/admin/a-order.vue'
<<<<<<< HEAD
import adminUser from './components/admin/a-user.vue'
=======
>>>>>>> 34a88b57ccfc1c26f78e238321853fdce817fa40



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
    { path: '/myOrder', component: myOrder },
    { path: '/profile', component: profile },
    { path: '/service', component: service },

    { path: '/adminLogin', component: adminLogin },
    { path: '/adminHome', component: adminHome },
    { path: '/adminRegister', component: adminRegister },
    { path: '/adminProduct', component: adminProduct },
    { path: '/adminOrder', component: adminOrder },
<<<<<<< HEAD
    { path: '/adminUser', component: adminUser },

=======
>>>>>>> 34a88b57ccfc1c26f78e238321853fdce817fa40
]

const router = createRouter({
    history: createWebHistory(),
    routes
})



export default router