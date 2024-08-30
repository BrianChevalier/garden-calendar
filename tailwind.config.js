/** @type {import('tailwindcss').Config} */

const { scanClojure } = require("@multiplyco/tailwind-clj");

const gridRowMap = {};

for (i = 13; i < 100; i++) {
     gridRowMap[i] = i.toString();
}

const gridColMap = {};

for (i = 13; i < 30; i++) {
     gridColMap[i] = i.toString();
}

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
            gridColumnStart: gridColMap,
            gridColumnEnd: gridColMap,
            gridRowStart: gridRowMap,
            gridRowEnd: gridRowMap
        },
    },
    plugins: [],
};
