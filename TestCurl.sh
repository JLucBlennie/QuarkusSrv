#!/bin/sh

SERVER_URL_BASE="http://localhost:9090/ctr"
# TSI a Lorient
ID_EVENT="7c35cd6c-e663-4988-871c-a44db592161d" 

#!/bin/bash

# Fonction pour afficher le menu
afficher_menu() {
    clear
    echo "=== Menu Principal ==="
    echo "1. Recueperation des evenements"
    echo "2. Recuperation de l'evenement $ID_EVENT"
    echo "3. Valider l'evenement $ID_EVENT"
    echo "4. Refuser l'evenement $ID_EVENT"
    echo "5. Test de Mail"
    echo "q. Quitter"
    echo "======================"
    echo -n "Choisissez une option [1-5] ou q : "
}

# Fonction pour recuperer les evenements
recup_events() {
	echo "Recuperation des evenements"
	curl "$SERVER_URL_BASE/evenements"
}

# Fonction pour recuperer l'evenement avec l'ID
recup_event_by_id() {
	echo "Recuperation d'un evenement id=$ID_EVENT"
	curl "$SERVER_URL_BASE/evenements/$ID_EVENT"
}

# Fonction pour valider un evenement avec l'ID
validate_event_by_id() {
	echo "Validation d'un evenement id=$ID_EVENT"
	curl -X PUT "$SERVER_URL_BASE/evenements/validate?id=$ID_EVENT"
}

# Fonction pour refuser un evenement avec l'ID
refuse_event_by_id() {
	echo "Validation d'un evenement id=$ID_EVENT"
	curl -X PUT "$SERVER_URL_BASE/evenements/refuse?id=$ID_EVENT"
}

test_mail() {
	echo "Test du mail"
	curl "$SERVER_URL_BASE/test-mail"
}

# Boucle principale du menu
while true; do
    afficher_menu
    read choix

    case $choix in
        1)
            recup_events
			read
            ;;
        2)
            recup_event_by_id
			read
            ;;
        3)
            validate_event_by_id
			read
            ;;
        4)
            refuse_event_by_id
			read
            ;;
        5)
            test_mail
			read
            ;;
        q)
            echo "Au revoir !"
            exit 0
            ;;
        *)
            echo "Option invalide. Veuillez réessayer."
            sleep 1
            ;;
    esac
done

# Avec autorisation
#curl -H "Authorization: Bearer TON_TOKEN" https://api.example.com/protected
# Avec utilisateur
#curl -u utilisateur:motdepasse https://api.example.com/protected

# POST avec données
#curl -X POST -d '{}' -H "Content-Type:application/json" http://localhost:9090/ctr/
