#!/bin/bash
# start-tomcat.sh
# WARをデプロイしてTomcatを起動する（コンテナ起動ごとに実行）

CATALINA_HOME="${CATALINA_HOME:-/opt/tomcat}"
APP_WAR="/app/dist/legacy-app.war"
WEBAPPS="$CATALINA_HOME/webapps"

echo "=== Tomcat Startup ==="

# Tomcatが既に起動中なら停止する
if [ -f "$CATALINA_HOME/bin/shutdown.sh" ]; then
    "$CATALINA_HOME/bin/shutdown.sh" 2>/dev/null || true
    sleep 2
fi

# WARをデプロイ (存在する場合)
if [ -f "$APP_WAR" ]; then
    echo "Deploying $APP_WAR to $WEBAPPS ..."
    cp "$APP_WAR" "$WEBAPPS/legacy-app.war"
    echo "WAR deployed."
else
    echo "No WAR found at $APP_WAR. Run 'ant clean build' first."
fi

# Tomcatを起動
export JAVA_HOME="${JAVA_HOME:-/usr/java/jdk1.5.0_22}"
export CATALINA_HOME
"$CATALINA_HOME/bin/startup.sh"
echo "Tomcat started. Access: http://localhost:8080/legacy-app/"
