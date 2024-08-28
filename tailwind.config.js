/** @type {import('tailwindcss').Config} */

const { scanClojure } = require("@multiplyco/tailwind-clj");

module.exports = {
    content: {
        files: ["./src/**/*.{clj,cljs,cljc}"],
    },
    safelist: [
        {
            pattern: /col-start-+/
        },
        {
            pattern: /col-end-+/
        },
        {
            pattern: /row-start-+/
        },
        {
            pattern: /row-end-+/
        }
    ],
    extract: {
        clj: scanClojure,
        cljs: scanClojure,
        cljc: scanClojure,
    },
    theme: {
        extend: {
            minWidth: {
                '160': '40rem'
            },
            gridTemplateColumns: {
                '25': '8fr repeat(24, minmax(0, 1fr))'
            },
            gridColumnStart: {
                '13': '13',
                '14': '14',
                '15': '15',
                '16': '16',
                '17': '17',
                '18': '18',
                '19': '19',
                '20': '20',
                '21': '21',
                '22': '22',
                '23': '23',
                '24': '24',
                '25': '25',
                '26': '26'
            },
            gridColumnEnd: {
                '13': '13',
                '14': '14',
                '15': '15',
                '16': '16',
                '17': '17',
                '18': '18',
                '19': '19',
                '20': '20',
                '21': '21',
                '22': '22',
                '23': '23',
                '24': '24',
                '25': '25',
                '26': '26'
            }
        },
    },
    plugins: [],
};
