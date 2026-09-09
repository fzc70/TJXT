# Tianji Admin

天机学堂管理台，使用 Vue 3、Vite、Element Plus 和 Pinia，提供课程、用户、营销、订单及运营管理页面。

```bash
npm ci
cp .env.example .env.local
npm run dev
```

访问 [localhost:18081](http://localhost:18081)。PowerShell 使用 `Copy-Item .env.example .env.local`。

| 命令 | 作用 |
| --- | --- |
| `npm run dev` | 启动开发服务器 |
| `npm run build` | 构建静态资源到 `dist/` |
| `npm run preview` | 本地预览构建产物 |

开发代理目标由 `.env.local` 的 `API_PROXY_TARGET` 指定，默认 `http://localhost:10010`。部署静态产物时需自行配置 `/api` 反向代理；预览服务器不提供后端服务。

完整架构、运行前提和配置方式见[仓库 README](../README.md)与[配置说明](../docs/configuration.md)。
