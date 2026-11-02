@echo off
chcp 65001 >nul
title EurekaBank SOAP .NET - Tunel Cloudflare (Opcional - GR08)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0INICIAR_TUNEL_CLOUD.ps1"
pause

