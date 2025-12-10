<template>
    <div class="browser-container">
        <h2>Dokąd jedziemy?</h2>
        <VueMultiselect
            v-model="selectedStationFrom"
            :options="allStations"
            :searchable="true"
            :close-on-select="true"
            label="stationName"
            track-by="stationId"
            placeholder="Wpisz, aby wyszukać stację początkową..."
            :allow-empty="false"
        />
        <VueMultiselect
            v-model="selectedStationTo"
            :options="allStations"
            :searchable="true"
            :close-on-select="true"
            label="stationName"
            track-by="stationId"
            placeholder="Wpisz, aby wyszukać stację końcową..."
            :allow-empty="false"
        />
        <p v-if="arrival">Departure time</p>
        <input type="time">
        <button @click="handleSearch">Search</button>
    </div>
    <div v-if="searched" class="resource-container">
        <h1>Wyniki wyszukiwania</h1>
        <div v-for="route in routes" :key="route.routeId" class="resource-card">
            <div class="resource-info">
                <h3>{{ route.startStationName }} ({{ route.departureTime }}) -> {{ route.endStationName }} ({{route.arrivalTime}})</h3>
                <h4>Pociąg: {{ route.trainName }}</h4>
            </div>
            <button class="delete-button" @click="handleDelete(route.routeId)">Usuń</button>
        </div>
    </div>
</template>

<script>
import VueMultiselect from 'vue-multiselect'
import 'vue-multiselect/dist/vue-multiselect.css'
export default {
    components: { VueMultiselect },
    data() {
        return {
            allStations: [],
            selectedStationFrom: null,
            selectedStationTo: null,
            selectedTime: null,
            searched: false,
            routes: []
        }
    },
    methods: {
        fetchStations() {
            const headers = {
                'Content-Type': 'application/json'
            }
            const urlStations = `http://localhost:6767/browse/stations`;
            fetch(urlStations, { method: 'GET', headers: headers })
                .then(res => {
                    if (!res.ok) {
                        throw new Error('Nie udało się pobrać stacji');
                    }
                    return res.json();
                })
                .then(data => {
                    this.allStations = data;
                })
                .catch(err => console.error("Błąd podczas pobierania stacji:", err));
        },
        handleSearch() {
            const headers = {
                'Content-Type': 'application/json'
            }
            const body = {
                "stationFromId" : this.stationFrom.stationId,
                "stationToId" : this.stationTo.stationId,
                "time" : this.selectedTime
            }
            const urlRoutes = `http://localhost:6767/browse/routes`;
            fetch(urlStations, { method: 'GET', headers: headers, body: body })
                .then(res => {
                    if (!res.ok) {
                        throw new Error('Nie udało się pobrać stacji');
                    }
                    return res.json();
                })
                .then(data => {
                    this.routes = data;
                })
                .catch(err => console.error("Błąd podczas pobierania stacji:", err));
        }
    },
    mounted() {
        this.fetchStations();
    }
}
</script>

<style scoped>
.browser-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  font-family: Avenir, Helvetica, Arial, sans-serif;
}

.browser-container h2 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
}
</style>