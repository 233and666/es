import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router.js'
// 引入中文语言包
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import App from './components/cs.vue'

const app = createApp(App)
// 配置 Element Plus 使用中文
app.use(ElementPlus, {
  locale: zhCn
})
app.use(router)
app.mount('#app')