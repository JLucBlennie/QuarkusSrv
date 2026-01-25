#!/bin/sh

SERVER_URL_BASE="http://localhost:9090/ctr"
# TSI a Lorient
ID_EVENT="7c35cd6c-e663-4988-871c-a44db592161d" 
ID_MONO="327d3458-ab53-44a4-b685-079181d46c49" 

# Fonction pour afficher le menu
afficher_menu() {
    clear
    echo "=== Menu Principal ==="
    echo "1. Recueperation des evenements"
    echo "2. Recuperation de l'evenement $ID_EVENT"
    echo "3. Valider l'evenement $ID_EVENT"
    echo "4. Refuser l'evenement $ID_EVENT"
    echo "5. Test de Mail"
    echo "6. Nb Evenements pour moniteur $ID_MONO"
    echo "7. Mise à jour du moniteur $ID_MONO"
    echo "8. Recuperation du moniteur $ID_MONO"
    echo "q. Quitter"
    echo "======================"
    echo -n "Choisissez une option [1-7] ou q : "
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

# Fonction pour le nb d'evenements avec l'ID du moniteur
nb_event_by_moniteur_by_id() {
	echo "nb d'evenements avec l'ID du moniteur=$ID_MONO"
	curl "$SERVER_URL_BASE/evenements/moniteur/$ID_MONO"
}

# Fonction Modification du moniteur ID
update_moniteur_by_id() {
	echo "Mise à jour du moniteur=$ID_MONO"
	curl -X PUT -d '{"uuid":"327d3458-ab53-44a4-b685-079181d46c49","firstname":"Yvonnick","lastname":"LE PEUTREC","niveau":"IN"}' -H "Content-Type: application/json" "$SERVER_URL_BASE/moniteurs/"
}

# Fonction pour recuperer l'evenement avec l'ID
recup_moniteur_by_id() {
	echo "Recuperation d'un moniteur id=$ID_MONO"
	curl "$SERVER_URL_BASE/moniteurs/$ID_MONO"
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
        6)
            nb_event_by_moniteur_by_id
			read
            ;;
        7)
            update_moniteur_by_id
			read
            ;;
        8)
            recup_moniteur_by_id
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
