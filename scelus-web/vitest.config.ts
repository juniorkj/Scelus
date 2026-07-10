import { defineConfig } from 'vitest/config';
import angular from '@analogjs/vite-plugin-angular';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig(({ mode }) => {
  return {
    plugins: [
      angular(),
    ],
    resolve: {
      alias: {
        '@tjma/angular-21': path.resolve(__dirname, './node_modules/@tjma/angular-21/fesm2022/tjma-angular-21.mjs'),
      },
    },
    test: {
      setupFiles: [path.resolve(__dirname, 'src/test-setup.ts')],
      globals: true,
      environment: 'happy-dom',
      include: ['src/**/*.spec.ts'],
      reporters: ['verbose'],
      server: {
        deps: {
          inline: [/@angular/, /@tjma/, /@analogjs/],
        },
      },
    },
  };
});
