// import { defineConfig } from 'vite'
// import react from '@vitejs/plugin-react'

// // https://vitejs.dev/config/
// export default defineConfig({
//   plugins: [react()],
//   server: {
//     host: '0.0.0.0', // Listen on all IPv4 interfaces
//   },
//   build: {
//     sourcemap: false, // Ensure source maps are generated
//   },
// })



import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import dotenv from 'dotenv';

// Load environment variables from .env files based on the mode
export default defineConfig(({ mode }) => {
  const envFiles: { [key: string]: string } = {
    development: '.env.development',
    production: '.env.production'
  };

  dotenv.config({ path: envFiles[mode] });

  return {
    plugins: [react()],
    server: {
      host: '0.0.0.0', // Listen on all IPv4 interfaces
      port: Number(process.env.VITE_PORT) || 3000 // Default to 3000 if VITE_PORT is not defined
    },
    build: {
      sourcemap: false, // Ensure source maps are generated
    },
    define: {
      'process.env': {
        VITE_API_URL: process.env.VITE_API_URL,
        VITE_DNS: process.env.VITE_DNS,
        NODE_ENV: mode, 
      }
    }
  };
});
