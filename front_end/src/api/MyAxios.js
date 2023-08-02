import axios from "axios";

const MyAxios = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 60000,
})

// http request 请求拦截器
MyAxios.interceptors.request.use(config => {

    return config;
}, error => {
    Promise.reject(error);
});

// 响应拦截器
MyAxios.interceptors.response.use(res => {
    return res.data;
}, error => {
    Promise.reject(error);
})

export default MyAxios;
