import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {

    cors: {
      // origin: "http://127.0.0.1:9000",
      origin: "*",
      credentials: true,
    },
  },
})
