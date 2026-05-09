import request from './request.js'


export const api={
    user:{
        //注册
       register:(userData)=>request.post('/user/register',userData),
        //登录
       login:(loginData)=>request.post('/user/login',loginData),
        //修改密码
       updatePassword:(updatePasswordData)=>request.post('/user/updatePassword',updatePasswordData),
        //退出
       logout:()=>request.post('/user/logout'),
        //获取用户信息
       getInfo:()=>request.get('/user/info')

    },
    product:{
        // 发布商品
        publish:(formData)=>request.post('/product/publish', formData),
        // 更新商品
        update:(id, productData, config)=>request.post(`/product/update/${id}`, productData, config),
        // 下架或上架商品
        off:(id)=>request.post(`/product/off/${id}`),
        // 删除商品
        delete:(id)=>request.delete(`/product/delete/${id}`),
        // 商品详情
        detail:(id)=>request.get(`/product/detail/${id}`),
        // 我的商品列表
        my:(params)=>request.get('/product/my', { params }),
        // 商品搜索
        search:(searchData)=>request.post('/product/search', searchData)
    },
    order:{
        // 创建订单
        create:(productId)=>request.post(`/order/create?productId=${productId}`),
        //确认收货
        confirm:(orderId)=>request.post(`/order/confirm?orderId=${orderId}`),
        // 取消订单
        cancel:(orderId)=>request.post(`/order/cancel?orderId=${orderId}`),
        //支付订单
        pay:(orderId)=>request.post(`/order/pay?orderId=${orderId}`),
        //我买到的订单
        myBuy:()=>request.post('/order/myBuy'),
        //我卖出的订单
        mySell:()=>request.post('/order/mySell')
    },
    message:{
        //发送消息
        send:(messageData)=>request.post('/message/send', messageData),
        //接受消息
        receive:(currentUserId)=>request.get(`/message/receive/${currentUserId}`)
    },
    favorite:{
        // 添加收藏
        add: (productId) => request.post(`/favorite/add?productId=${productId}`),
        // 取消收藏
        cancel: (productId) => request.post(`/favorite/cancel?productId=${productId}`),
        // 判断是否收藏了商品
        isFavorite: (productId) => request.get(`/favorite/isFavorite?productId=${productId}`),
        // 收藏列表
        list: () => request.get('/favorite/list')
    },
    admin:{
        // 管理员登录
        login:(loginData)=>request.post('/admin/login',loginData),
        // 管理员注册
        register:(adminData)=>request.post('/admin/register',adminData),
        // 获取商品列表
        getProductList:(params)=>request.get('/admin/list', { params }),
        // 封禁/解封商品
        offProduct:(id)=>request.post(`/admin/off/${id}`),
        // 获取订单列表
        getOrderList:(params)=>request.get('/admin/order/list', { params }),
        // 获取订单详情
        getOrderDetail:(id)=>request.get(`/admin/order/detail/${id}`)
    }


}