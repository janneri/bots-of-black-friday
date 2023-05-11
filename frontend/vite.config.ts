import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import glsl from 'vite-plugin-glsl'
import { resolve } from 'path'

const rootDir = resolve(__dirname)
const envDir = rootDir
const publicDir = resolve(rootDir, 'public')
const srcDir = resolve(rootDir, 'src')

// https://vitejs.dev/config/
export default defineConfig({
  root: srcDir,
  envDir,
  publicDir,
  build: {
    outDir: '../../server/src/main/resources/static',
    emptyOutDir: true,
    manifest: false,
    rollupOptions: {
      input: {
        main: resolve(srcDir, 'index.html')
      }
    }
  },
  resolve: {
    alias: {
      '@src': srcDir
    }
  },
  plugins: [
    react(),
    glsl()
  ],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: resolve(srcDir, 'tests', 'unitTestSetup.ts'),
    // Disable CSS if tests get slow
    css: true
  }
})
