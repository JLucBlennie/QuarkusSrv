#!/bin/sh
echo Lancement du Calendrier CTR en débug

cd calendrier-server
echo Lancement du Server
./gradlew quarkusDev &

cd ../web.ui
echo Lancement du Front End
npm run dev
cd ..