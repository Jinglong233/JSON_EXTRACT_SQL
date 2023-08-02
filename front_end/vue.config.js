const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
        client: {
            overlay: false
        },
        proxy: {
            '/api': {//请求称号
                target: 'http://localhost:8088', //请求的接口
                changeOrigin: true,//允许跨域
            }
        }
    },
})
