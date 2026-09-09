// VITE_* 会进入浏览器产物，只能存放公开的地址配置。
const config = {
  host: import.meta.env.VITE_API_BASE_URL || "/api",
  cdn: import.meta.env.VITE_CDN_URL || "",
};

export default {
  development: config,
  production: config,
  test: config,
  product: config,
  pro: config,
  mock: config,
};
