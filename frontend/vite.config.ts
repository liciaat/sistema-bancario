import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  // Reaproveita o arquivo de marca fornecido no repositório, sem duplicá-lo.
  publicDir: '../LOGO',
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
