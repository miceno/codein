#!/bin/bash -x


set -a

LANG=en_US.UTF-8
CLASSPATH=.:/home/onchan/orestes/pullfeed/lib/apache-log4j-1.2.16/log4j-1.2.16.jar:/home/onchan/orestes/pullfeed/lib/httpcomponents-client-4.0.1/lib/httpcore-4.0.1.jar:/home/onchan/orestes/pullfeed/lib/httpcomponents-client-4.0.1/lib/commons-logging-1.1.1.jar:/home/onchan/orestes/pullfeed/lib/httpcomponents-client-4.0.1/lib/httpclient-4.0.1.jar:/home/onchan/orestes/pullfeed/lib/httpcomponents-client-4.0.1/lib/httpmime-4.0.1.jar:/home/onchan/orestes/pullfeed/lib/httpcomponents-client-4.0.1/lib/apache-mime4j-0.6.jar:/home/onchan/orestes/pullfeed/lib/httpcomponents-client-4.0.1/lib/commons-codec-1.3.jar:/home/onchan/orestes/pullfeed/lib/rome-1.0.jar:/home/onchan/orestes/pullfeed/lib/httpcomponents-core-4.0.1/lib/httpcore-4.0.1.jar:/home/onchan/orestes/pullfeed/lib/httpcomponents-core-4.0.1/lib/httpcore-nio-4.0.1.jar

SCALA_HOME=/home/onchan/orestes/scala-2.8.0.final

PATH=$PATH:$SCALA_HOME/bin

EXPRESSION="source->//channel/title,link->//item/guid,title->//item/title"
FILE="http://feeds.mashable.com/Mashable?format=xml"
TEMPLATE="{source}: {title} {link}"
USER=feedreader

EXEC_HOME=$(dirname $0)

cd "$EXEC_HOME"
scala PullFeed "$FILE" "$EXPRESSION" "$USER" "$TEMPLATE"

