@echo off
chcp 65001 > nul

REM 设置项目根目录
set PROJECT_ROOT=%~dp0

REM 编译项目
echo [%date% %time%] 开始编译项目...
call mvn clean compile -X -Dorg.slf4j.simpleLogger.defaultLogLevel=info
if %errorlevel% neq 0 (
    echo [%date% %time%] 编译失败
    exit /b 1
)
echo [%date% %time%] 编译成功完成

REM 运行测试
echo [%date% %time%] 开始运行测试...
call mvn test -X -Dorg.slf4j.simpleLogger.defaultLogLevel=info
if %errorlevel% neq 0 (
    echo [%date% %time%] 测试失败
    exit /b 1
)
echo [%date% %time%] 测试运行完成

echo 测试成功完成