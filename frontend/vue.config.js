const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  productionSourceMap: process.env.GENERATE_SOURCEMAP !== 'false',
  configureWebpack: {
    devtool: process.env.NODE_ENV === 'development' ? 'eval-cheap-module-source-map' : 'source-map',
    optimization: {
      splitChunks: {
        chunks: 'all',
        cacheGroups: {
          vendor: {
            name: 'chunk-vendors',
            test: /[\\/]node_modules[\\/]/,
            priority: 10,
            chunks: 'initial'
          },
          charts: {
            name: 'chunk-charts',
            test: /[\\/]node_modules[\\/]chart\.js/,
            priority: 20,
            chunks: 'all'
          }
        }
      }
    }
  },
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      }
    }
  },
  pwa: {
    name: 'Campus Chapter Organizer',
    themeColor: '#0d6efd',
    msTileColor: '#000000',
    appleMobileWebAppCapable: 'yes',
    appleMobileWebAppStatusBarStyle: 'black',
    workboxPluginMode: 'InjectManifest',
    workboxOptions: {
      swSrc: 'src/sw.js',
    }
  }
})