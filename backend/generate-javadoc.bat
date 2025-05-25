@echo off
set SRC_PATH=src\main\java
set OUTPUT_DIR=doc
set PACKAGE=org.pipproject.pip_project

javadoc -d %OUTPUT_DIR% -sourcepath %SRC_PATH% -subpackages %PACKAGE%

echo ✅ Javadoc generat în folderul %OUTPUT_DIR%
pause
