import { defineConfig, loadEnv } from "vite";
import vue from "@vitejs/plugin-vue";
import svgLoader from "vite-svg-loader";
import vueJsx from "@vitejs/plugin-vue-jsx";
import path from "path";

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  return {
    base: "./",
    resolve: { alias: { "@": path.resolve(__dirname, "./src") } },
    plugins: [vue(), vueJsx(), svgLoader()],
    server: {
      port: 18081,
      host: "localhost",
      proxy: {
        // 开发代理保留业务前缀，交给网关路由。
        "/api": {
          target: env.API_PROXY_TARGET || "http://localhost:10010",
          changeOrigin: true,
          rewrite: (url) => url.replace(/^\/api/, ""),
        },
        "/img-tx": {
          target: env.IMAGE_PROXY_TARGET || "http://localhost:10010",
          changeOrigin: true,
        },
      },
    },
  };
});
