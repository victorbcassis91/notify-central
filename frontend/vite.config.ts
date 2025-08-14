import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
/*  optimizeDeps: {
      disabled: true,  // Temporary workaround
  },*/
  define: {
    global: "window",
  },
  server: {
    port: 5173,
    host: true,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      },
    }/*,
    watch: {
        usePolling: true  // Needed for Docker file watching
    }*/
  }
})
