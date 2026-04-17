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

    },
    order:{

    }

}