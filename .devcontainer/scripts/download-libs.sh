#!/bin/bash
# download-libs.sh
# Maven Central から必要なJARをlib/へダウンロードする（冪等: 既存ファイルはスキップ）

set -e

MAVEN="https://repo1.maven.org/maven2"
LIB_DIR="${1:-/app/lib}"

mkdir -p "$LIB_DIR"

# ファイルが存在しサイズが1KB以上なら有効とみなしてスキップ
download_jar() {
    local filename="$1"
    local url="$2"
    local dest="$LIB_DIR/$filename"

    if [ -f "$dest" ] && [ "$(stat -c%s "$dest" 2>/dev/null || echo 0)" -gt 1000 ]; then
        echo "  [SKIP] $filename"
        return 0
    fi
    echo "  [DOWN] $filename"
    curl -sL -o "$dest" "$url"
}

echo "=== Downloading library JARs to $LIB_DIR ==="

# --- Struts 1.3.10 ---
download_jar "struts-core-1.3.10.jar"   "$MAVEN/org/apache/struts/struts-core/1.3.10/struts-core-1.3.10.jar"
download_jar "struts-taglib-1.3.10.jar" "$MAVEN/org/apache/struts/struts-taglib/1.3.10/struts-taglib-1.3.10.jar"
download_jar "struts-extras-1.3.10.jar" "$MAVEN/org/apache/struts/struts-extras/1.3.10/struts-extras-1.3.10.jar"
download_jar "struts-el-1.3.10.jar"     "$MAVEN/org/apache/struts/struts-el/1.3.10/struts-el-1.3.10.jar"

# --- Servlet API (compile-only, WARには含めない) ---
download_jar "servlet-api-2.5.jar" "$MAVEN/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar"
download_jar "jsp-api-2.1.jar"     "$MAVEN/javax/servlet/jsp/jsp-api/2.1/jsp-api-2.1.jar"

# --- Apache Commons ---
download_jar "commons-beanutils-1.8.0.jar"   "$MAVEN/commons-beanutils/commons-beanutils/1.8.0/commons-beanutils-1.8.0.jar"
download_jar "commons-digester-1.8.jar"      "$MAVEN/commons-digester/commons-digester/1.8/commons-digester-1.8.jar"
download_jar "commons-logging-1.1.1.jar"     "$MAVEN/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar"
download_jar "commons-validator-1.3.1.jar"   "$MAVEN/commons-validator/commons-validator/1.3.1/commons-validator-1.3.1.jar"
download_jar "commons-chain-1.1.jar"         "$MAVEN/commons-chain/commons-chain/1.1/commons-chain-1.1.jar"
download_jar "commons-fileupload-1.2.2.jar"  "$MAVEN/commons-fileupload/commons-fileupload/1.2.2/commons-fileupload-1.2.2.jar"
download_jar "commons-collections-3.2.1.jar" "$MAVEN/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar"

# --- Hibernate 3.6.10 ---
download_jar "hibernate-core-3.6.10.Final.jar" \
    "$MAVEN/org/hibernate/hibernate-core/3.6.10.Final/hibernate-core-3.6.10.Final.jar"
download_jar "hibernate-jpa-2.0-api-1.0.1.Final.jar" \
    "$MAVEN/org/hibernate/javax/persistence/hibernate-jpa-2.0-api/1.0.1.Final/hibernate-jpa-2.0-api-1.0.1.Final.jar"
download_jar "hibernate-commons-annotations-3.2.0.Final.jar" \
    "$MAVEN/org/hibernate/hibernate-commons-annotations/3.2.0.Final/hibernate-commons-annotations-3.2.0.Final.jar"

# --- Hibernate 依存ライブラリ ---
download_jar "antlr-2.7.6.jar"            "$MAVEN/antlr/antlr/2.7.6/antlr-2.7.6.jar"
download_jar "dom4j-1.6.1.jar"            "$MAVEN/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar"
download_jar "javassist-3.12.0.GA.jar"    "$MAVEN/javassist/javassist/3.12.0.GA/javassist-3.12.0.GA.jar"
download_jar "jta-1.1.jar"                "$MAVEN/javax/transaction/jta/1.1/jta-1.1.jar"
download_jar "slf4j-api-1.6.1.jar"        "$MAVEN/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar"
download_jar "slf4j-simple-1.6.1.jar"     "$MAVEN/org/slf4j/slf4j-simple/1.6.1/slf4j-simple-1.6.1.jar"
download_jar "jboss-logging-3.1.0.GA.jar" "$MAVEN/org/jboss/logging/jboss-logging/3.1.0.GA/jboss-logging-3.1.0.GA.jar"

# --- その他 ---
download_jar "oro-2.0.8.jar"                      "$MAVEN/oro/oro/2.0.8/oro-2.0.8.jar"
download_jar "log4j-1.2.17.jar"                   "$MAVEN/log4j/log4j/1.2.17/log4j-1.2.17.jar"
download_jar "mysql-connector-java-5.1.49.jar"    "$MAVEN/mysql/mysql-connector-java/5.1.49/mysql-connector-java-5.1.49.jar"

JAR_COUNT=$(ls "$LIB_DIR"/*.jar 2>/dev/null | wc -l)
echo "=== Library download complete ($JAR_COUNT JARs in $LIB_DIR) ==="
