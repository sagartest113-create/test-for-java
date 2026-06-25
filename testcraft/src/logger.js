'use strict';

const C = {
  reset: '\x1b[0m', dim: '\x1b[2m', red: '\x1b[31m', green: '\x1b[32m',
  yellow: '\x1b[33m', blue: '\x1b[34m', cyan: '\x1b[36m', bold: '\x1b[1m'
};

const useColor = process.stdout.isTTY && !process.env.NO_COLOR;
function c(color, s) { return useColor ? color + s + C.reset : s; }

const log = {
  info: (m) => console.log(`${c(C.cyan, '[TestCraft]')} ${m}`),
  step: (m) => console.log(`${c(C.blue, '▸')} ${c(C.bold, m)}`),
  ok: (m) => console.log(`${c(C.green, '✓')} ${m}`),
  warn: (m) => console.log(`${c(C.yellow, '⚠')} ${m}`),
  err: (m) => console.log(`${c(C.red, '✗')} ${m}`),
  dim: (m) => console.log(c(C.dim, m)),
  raw: (m) => console.log(m)
};

module.exports = { log, c, C };
