#!/bin/bash

# Configuration de base (adapte l'URL selon ton environnement)
BASE_URL="http://localhost:8080"  # Remplace par l'URL de ton VPS si nécessaire (ex: http://ton-domaine.com:8080)

# Couleurs pour une meilleure lisibilité
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction pour afficher les en-têtes de section
section() {
  echo -e "${BLUE}=== $1 ===${NC}"
}

# Fonction pour afficher les erreurs
error() {
  echo -e "${RED}Erreur: $1${NC}"
}

# Fonction pour afficher les succès
success() {
  echo -e "${GREEN}$1${NC}"
}

# --- GET ---
section "Requêtes GET"

# 1. Récupérer tous les événements
echo "GET /evenements"
curl -s -X GET "$BASE_URL/evenements" | jq
echo -e "\n"

# 2. Récupérer un événement par ID
read -p "Entrez l'ID de l'événement à récupérer (ex: 1) : " event_id
echo "GET /evenements/$event_id"
curl -s -X GET "$BASE_URL/evenements/$event_id" | jq
echo -e "\n"

# 3. Récupérer tous les types d'événements
echo "GET /types-evenements"
curl -s -X GET "$BASE_URL/types-evenements" | jq
echo -e "\n"

---

# --- POST ---
section "Requêtes POST"

# 1. Créer un nouvel événement (exemple avec un JSON)
echo "POST /evenements"
curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"titre": "Nouvel événement", "nbparticipants": 0, "typeEvenement": {"id": 1}}' \
  "$BASE_URL/evenements" | jq
echo -e "\n"

# 2. Créer un nouveau type d'événement
echo "POST /types-evenements"
curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"nom": "Conférence", "description": "Type pour les conférences"}' \
  "$BASE_URL/types-evenements" | jq
echo -e "\n"

---

# --- PUT ---
section "Requêtes PUT"

# 1. Mettre à jour un événement
read -p "Entrez l'ID de l'événement à mettre à jour (ex: 1) : " update_id
echo "PUT /evenements/$update_id"
curl -s -X PUT \
  -H "Content-Type: application/json" \
  -d '{"titre": "Événement mis à jour", "nbparticipants": 10}' \
  "$BASE_URL/evenements/$update_id" | jq
echo -e "\n"

---

# --- DELETE ---
section "Requêtes DELETE"

# 1. Supprimer un événement
read -p "Entrez l'ID de l'événement à supprimer (ex: 1) : " delete_id
echo "DELETE /evenements/$delete_id"
curl -s -X DELETE "$BASE_URL/evenements/$delete_id"
success "Événement $delete_id supprimé (si le serveur a retourné une réponse vide, c'est normal)."
echo -e "\n"

# 2. Supprimer un type d'événement
read -p "Entrez l'ID du type d'événement à supprimer (ex: 1) : " type_delete_id
echo "DELETE /types-evenements/$type_delete_id"
curl -s -X DELETE "$BASE_URL/types-evenements/$type_delete_id"
success "Type d'événement $type_delete_id supprimé."
echo -e "\n"

---

# --- Requêtes personnalisées ---
section "Requêtes personnalisées"

# Exemple : Récupérer les événements avec leurs relations (via EntityGraph)
echo "GET /evenements/with-all"
curl -s -X GET "$BASE_URL/evenements/with-all" | jq
echo -e "\n"

---

echo -e "${GREEN}=== Fin des tests ===${NC}"
