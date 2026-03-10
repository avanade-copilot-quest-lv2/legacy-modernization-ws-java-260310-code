#!/bin/bash
# download-libs.sh — Download JAR dependencies for legacy Struts app
# Based on lib/HIBERNATE_LIBRARIES.md
set -euo pipefail

LIB_DIR="${1:-.}"
MAVEN_BASE="https://repo1.maven.org/maven2"

download() {
  local url="$1"
  local dest="$LIB_DIR/$(basename "$url")"
  if [ ! -f "$dest" ]; then
    echo "Downloading: $(basename "$url")"
    curl -fsSL -o "$dest" "$url"
  fi
}

# --- Hibernate 3.6 Core ---
download "$MAVEN_BASE/org/hibernate/hibernate-core/3.6.10.Final/hibernate-core-3.6.10.Final.jar"
download "$MAVEN_BASE/org/hibernate/javax/persistence/hibernate-jpa-2.0-api/1.0.1.Final/hibernate-jpa-2.0-api-1.0.1.Final.jar"
download "$MAVEN_BASE/org/hibernate/common/hibernate-commons-annotations/4.0.1.Final/hibernate-commons-annotations-4.0.1.Final.jar"

# --- Hibernate Dependencies ---
download "$MAVEN_BASE/antlr/antlr/2.7.6/antlr-2.7.6.jar"
download "$MAVEN_BASE/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar"
download "$MAVEN_BASE/javassist/javassist/3.12.0.GA/javassist-3.12.0.GA.jar"
download "$MAVEN_BASE/javax/transaction/jta/1.1/jta-1.1.jar"
download "$MAVEN_BASE/org/jboss/logging/jboss-logging/3.1.0.GA/jboss-logging-3.1.0.GA.jar"

# --- SLF4J Logging ---
download "$MAVEN_BASE/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar"
download "$MAVEN_BASE/org/slf4j/slf4j-simple/1.6.1/slf4j-simple-1.6.1.jar"

# --- MySQL JDBC Driver ---
download "$MAVEN_BASE/mysql/mysql-connector-java/5.1.49/mysql-connector-java-5.1.49.jar"

# --- Struts 1.3.10 ---
download "$MAVEN_BASE/org/apache/struts/struts-core/1.3.10/struts-core-1.3.10.jar"
download "$MAVEN_BASE/org/apache/struts/struts-taglib/1.3.10/struts-taglib-1.3.10.jar"
download "$MAVEN_BASE/org/apache/struts/struts-extras/1.3.10/struts-extras-1.3.10.jar"
download "$MAVEN_BASE/org/apache/struts/struts-el/1.3.10/struts-el-1.3.10.jar"

# --- Apache Commons ---
download "$MAVEN_BASE/commons-beanutils/commons-beanutils/1.8.0/commons-beanutils-1.8.0.jar"
download "$MAVEN_BASE/commons-digester/commons-digester/1.8/commons-digester-1.8.jar"
download "$MAVEN_BASE/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar"
download "$MAVEN_BASE/commons-validator/commons-validator/1.3.1/commons-validator-1.3.1.jar"
download "$MAVEN_BASE/commons-chain/commons-chain/1.1/commons-chain-1.1.jar"
download "$MAVEN_BASE/commons-fileupload/commons-fileupload/1.2.2/commons-fileupload-1.2.2.jar"
download "$MAVEN_BASE/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar"
download "$MAVEN_BASE/commons-io/commons-io/2.4/commons-io-2.4.jar"
download "$MAVEN_BASE/oro/oro/2.0.8/oro-2.0.8.jar"

# --- Logging ---
download "$MAVEN_BASE/log4j/log4j/1.2.17/log4j-1.2.17.jar"

# --- Servlet / JSP API (compile only) ---
download "$MAVEN_BASE/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar"
download "$MAVEN_BASE/javax/servlet/jsp-api/2.0/jsp-api-2.0.jar"

echo "All libraries downloaded to $LIB_DIR"
