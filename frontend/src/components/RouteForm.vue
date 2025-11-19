<template>
  <div class="modal-container">
    <div class="modal-header">
      <h3>Zdefiniuj stacje na trasie</h3>
    </div>
    <div class="modal-body-scrollable">
      <draggable v-model="stations" item-key="stationId" tag="div" class="list-group">
        <template #item="{element}">
          <div class="list-group-item">
            <div>
              <label>Wybierz stację:</label>
              <VueMultiselect
                v-model="element.selectedStation"
                :options="allStations"
                :searchable="true"
                :close-on-select="true"
                label="stationName"
                track-by="stationId"
                placeholder="Wpisz, aby wyszukać..."
                :allow-empty="false"
              />
          </div>

            Przyjazd: <input type="time" step="60" v-model="element.arrivalTime">
            Odjazd: <input type="time" v-model="element.departureTime">
            Kilometr: <input type="number" v-model="element.routeKilometer">
            <button @click="deleteStation(element.stationId)">Usuń</button>
          </div>
        </template>
      </draggable>
    </div>
    <div class="modal-footer">
      <button @click="addNewStation" class="add-station-button">Dodaj stację</button>
      <button @click="handleSave">Zapisz</button>
    </div>
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import VueMultiselect from 'vue-multiselect'
import 'vue-multiselect/dist/vue-multiselect.css'

export default {
  name: 'RouteForm',
  components: {
    draggable, VueMultiselect
  },
  props: ['routeId'],
  data() {
    return {
      stations: [],
      error: null,
      allStations: []
    }
  },
  async mounted() {
    const jwtToken = localStorage.getItem('user_token');
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwtToken}`
    };
    const url = `http://localhost:6767/admin/route-stations/${this.routeId}`
    const urlStations = `http://localhost:6767/admin/stations`

    try {
        const [resStations, resRouteStations] = await Promise.all([
            fetch(urlStations, { method: 'GET', headers: headers }),
            fetch(url, { method: 'GET', headers: headers })
        ])

        if (!resStations.ok) throw new Error(`Błąd pobierania stacji: ${resStations.status}`)
        if (!resRouteStations.ok) throw new Error(`Błąd pobierania stacji dla trasy: ${resRouteStations.status}`)

        this.allStations = await resStations.json()
        const routeStationsData = await resRouteStations.json()

        this.stations = routeStationsData.map(routeStation => {
            
            const matchingStation = this.allStations.find(
                s => s.stationId === routeStation.stationId
            )

            return {
                ...routeStation,
                selectedStation: matchingStation || null 
            }
        })

    } catch (err) {
        this.error = err.message;
        console.error("Błąd podczas ładowania danych:", err)
    }
  },
  methods: {
    addNewStation() {
      const newStation = {
        routeId: this.routeId,
        stationId: -Date.now(),
        stationName: null,
        arrivalTime: "00:00",
        departureTime: "00:00",
        stopOrder: this.stations.length,
        routeKilometer: 0,
        selectedStation: null
      }

      this.stations.push(newStation)
    },
    deleteStation(stationId) {
      this.stations = this.stations.filter(station => station.stationId !== stationId)
    },
    handleSave() {

    }
  }
}
</script>

<style scoped>
.list-group {
  margin-top: 15px;
}

.list-group-item {
  padding: 10px 15px;
  margin: 5px 0;
  border: 1px solid #ddd;
  background-color: #f9f9f9;
  cursor: grab;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-group-item:active {
  cursor: grabbing;
}

.error-message {
  color: #dc3545;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  padding: 10px 15px;
  border-radius: 5px;
  margin-bottom: 15px;
}

.add-station-button {
  /* Podstawowy wygląd */
  background-color: #007bff; /* Typowy niebieski */
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px 20px;
  margin-top: 20px;
  cursor: pointer; /* Zmienia kursor na dłoń, kluczowy element interaktywności */
  font-size: 16px;
  transition: background-color 0.2s ease, transform 0.1s ease; /* Płynne przejścia */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* Delikatny cień dodający głębi */
}

.add-station-button:hover {
  /* Efekt najechania myszą (hover) */
  background-color: #0056b3; /* Przyciemnienie koloru */
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15);
}

.add-station-button:active {
  background-color: #004085;
  transform: translateY(2px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.modal-container {
  /* Ograniczamy wysokość całego okna */
  max-height: 85vh; 
  display: flex;        /* Włączamy flexbox */
  flex-direction: column; /* Układ pionowy */
}

.modal-header, 
.modal-footer {
  /* Nagłówek i stopka nie mogą się kurczyć */
  flex-shrink: 0; 
  padding: 15px;
  background: white;
  z-index: 1; /* Żeby były nad przewijaną treścią */
}

.modal-body-scrollable {
  /* To kluczowe: ta sekcja zajmuje pozostałe miejsce i scrolluje */
  flex-grow: 1; 
  overflow-y: auto; 
  padding: 15px;
  min-height: 0; /* Ważne dla Flexboxa w Firefoxie */
}
</style>