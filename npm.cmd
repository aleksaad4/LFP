@echo off
setlocal
set PATH=%~dp0/thirdparty/node/;%PATH%
node thirdparty/node/node_modules/npm/bin/npm-cli.js %*
@echo on