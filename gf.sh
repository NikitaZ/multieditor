#!/bin/sh
#ps axww | grep java | grep Root| grep "`pwd`" | while read p r; do echo $p; done
jps | grep ASMain | while read p r; do echo $p; done
