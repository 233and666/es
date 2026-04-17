import axios from 'axios'

import { ElMessage } from 'element-plus' // 请根据你实际使用的 UI 库调整（如 vant 的 showToast）

// 1. 创建 Axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8080/api', // 后端统一前缀，需与后端 Controller 的 @RequestMapping("/api/xxx") 一致
    timeout: 10000,
    withCredentials: true // 与后端 CorsConfig.setAllowCredentials(true) 配套
})

// 2. 请求拦截器：自动添加 JWT Token
request.interceptors.request.use(

    config => {
        console.log('=== 请求拦截器开始 ===');
        console.log('config.baseURL:', config.baseURL);
        console.log('config.url:', config.url);
        console.log('拼接后完整URL:', config.baseURL + config.url);
        console.log('config.method:', config.method);

        // 跳过注册接口的认证（注册时用户还没有token）
        if (config.url && config.url.includes('/user/register')) {
            return config
        }
        // 从 localStorage 获取登录成功后保存的 token
        const token = localStorage.getItem('token')
        if (token) {
            // 按后端 JwtInterceptor 的要求，格式为 "Bearer <token>"
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 3. 响应拦截器：统一处理后端返回的 Result 格式
request.interceptors.response.use(
    (response) => {
        const res = response.data // 后端返回的 Result 对象
        // 根据你后端 Result 的定义：code=200 为成功
        if (res.code !== 200) {
            // 业务错误，给出提示并返回失败的 Promise
            ElMessage.error((res.message || '请求失败'))
            return Promise.reject(new Error(res.message || '请求失败'))
        }
        // 成功时直接返回 Result 对象，页面里可以直接用 res.data
        return res
    },
    error => {
        // 网络错误或 HTTP 错误（如 401）
        const message = error.response?.data?.message || error.message || '网络异常'
        ElMessage.error(message+'，网络异常6666')
        return Promise.reject(error)
    }
)

export default request





























