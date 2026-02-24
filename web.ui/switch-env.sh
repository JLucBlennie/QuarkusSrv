#!/bin/bash
if [ "$1" = "prod" ]; then
  echo "Basculer en mode production..."
  cp .env.production .env.local
  npm run build
  npm run start
elif [ "$1" = "dev" ]; then
  echo "Basculer en mode développement..."
  cp .env.development .env.local
  npm run dev
else
  echo "Usage: $0 [prod|dev]"
fi
