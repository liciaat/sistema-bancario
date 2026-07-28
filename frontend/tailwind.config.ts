import type { Config } from 'tailwindcss'

export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#090909',
        panel: '#141414',
        'panel-soft': '#1b1b1b',
        brand: '#ffde00',
        'brand-muted': '#d7bb00',
      },
      boxShadow: {
        brand: '0 16px 45px -22px rgba(255, 222, 0, 0.72)',
        card: '0 16px 40px rgba(0, 0, 0, 0.2)',
      },
    },
  },
  plugins: [],
} satisfies Config
