#!/bin/bash
set -e

BACKEND_DIR="/home/jan/projekty/RailHub/backend"
FRONTEND_DIR="/home/jan/projekty/RailHub/frontend"
DB_SERVICE_NAME="postgresql"

START_DB=0
START_BACKEND=0
START_FRONTEND=0

usage() {
    echo "Skrypt do uruchamiania środowiska RailHub"
    echo ""
    echo "Użycie: $0 [opcje]"
    echo ""
    echo "Opcje:"
    echo "  -d, --database   Uruchom/sprawdź bazę danych ($DB_SERVICE_NAME)"
    echo "  -b, --backend    Uruchom backend Spring Boot (z $BACKEND_DIR)"
    echo "  -f, --frontend   Uruchom frontend (z $FRONTEND_DIR)"
    echo "  -h, --help       Wyświetl tę pomoc"
    echo ""
    echo "Przykład: $0 -d -b  (Sprawdza bazę danych, a następnie uruchamia backend)"
    echo
    echo "UWAGA: Backend (-b) i Frontend (-f) to procesy blokujące terminal."
    exit 1
}

start_database() {
    if ! systemctl is-active --quiet $DB_SERVICE_NAME; then
        echo "Usługa $DB_SERVICE_NAME nie działa. Uruchamianie (wymagane sudo)..."
        
        if sudo systemctl start $DB_SERVICE_NAME; then
            echo "Baza danych ($DB_SERVICE_NAME) została pomyślnie uruchomiona."
        else
            echo "BŁĄD: Nie udało się uruchomić $DB_SERVICE_NAME."
            exit 1
        fi
    else
        echo "Baza danych ($DB_SERVICE_NAME) już działa."
    fi
}

start_backend() {
    if [ ! -d "$BACKEND_DIR" ]; then
        echo "BŁĄD: Nie znaleziono katalogu backendu: $BACKEND_DIR"
        exit 1
    fi
    
    cd "$BACKEND_DIR"

    if [ ! -f "mvnw" ]; then
        echo "BŁĄD: Nie znaleziono 'mvnw' w $BACKEND_DIR"
        exit 1
    fi
    
    chmod +x mvnw
    ./mvnw spring-boot:run
}


start_frontend() {
    
    if [ ! -d "$FRONTEND_DIR" ]; then
        echo "BŁĄD: Nie znaleziono katalogu frontendu: $FRONTEND_DIR"
        exit 1
    fi
    
    cd "$FRONTEND_DIR"
    npm run serve
}

if [ $# -eq 0 ]; then
    usage
fi

# Definiowanie opcji
# -o: krótkie opcje (d, b, f, h)
# --long: długie opcje (database, backend, frontend, help)
# -n: nazwa skryptu do logów błędów
# -- "$@": przekaż wszystkie argumenty skryptu
PARSED=$(getopt -o dbfh --long database,backend,frontend,help -n "$0" -- "$@")

if [ $? -ne 0 ]; then
    usage
fi
eval set -- "$PARSED"

while true; do
    case "$1" in
        -d|--database)
            START_DB=1
            shift
            ;;
        -b|--backend)
            START_BACKEND=1
            shift
            ;;
        -f|--frontend)
            START_FRONTEND=1
            shift
            ;;
        -h|--help)
            usage
            ;;
        --)
            shift
            break
            ;;
        *)
            echo "Błąd wewnętrzny!"
            exit 1
            ;;
    esac
done


if [ "$START_DB" -eq 1 ]; then
    start_database
fi

if [ "$START_BACKEND" -eq 1 ]; then
    start_backend
fi

if [ "$START_FRONTEND" -eq 1 ]; then
    start_frontend
fi