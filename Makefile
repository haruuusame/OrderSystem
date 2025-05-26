# ===== 共通設定 =====
JAVAC = javac
JAVA = java
OUT = out
CP = .:lib/sqlite-jdbc-3.30.1.jar
SRC = src/model/*.java src/view/**/*.java src/controller/**/*.java src/util/*.java src/app/*.java

# ===== ビルド =====
build:
	mkdir -p $(OUT)
	$(JAVAC) -cp $(CP) -d $(OUT) $(SRC)

# ===== 実行ターゲット =====
order: build
	$(JAVA) -cp $(CP):$(OUT) app.OrderMain

employee: build
	$(JAVA) -cp $(CP):$(OUT) app.EmployeeMain

display: build
	$(JAVA) -cp $(CP):$(OUT) app.CallDisplayAppMain

# ===== クリア =====
clean:
	rm -rf $(OUT)
