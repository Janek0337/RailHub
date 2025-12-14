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
        <p>Departure time</p>
        <input type="time" v-model="selectedTime">
        <button @click="handleSearch">Search</button>
    </div>
    <div v-if="searched" class="resource-container">
        <h1>Wyniki wyszukiwania:</h1>
        <div v-for="route in routes" :key="route.routeId" class="resource-card">
            <h1>-----------------------------------------------------</h1>
            <div class="resource-info">
                <h3>{{ route.startStationName }} ({{ route.departureTime }}) -> {{ route.endStationName }} ({{route.arrivalTime}})</h3>
                <h4>Pociąg: {{ route.trainName }}</h4>
                <h4>Zajętość miejsc: {{ route.takenTicketCount }}/{{ route.ticketCount }}</h4>
            </div>
            <button class="delete-button" @click="handleBuy(route.routeId)">Kup bilet</button>
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
                    console.log("Pobrane stacje (sprawdź nazwy pól):", data);
                    this.allStations = data;
                })
                .catch(err => console.error("Błąd podczas pobierania stacji:", err));
        },
        handleSearch() {
            if (!this.selectedStationFrom || !this.selectedStationTo) {
                alert("Proszę wybrać obie stacje.");
                return;
            }
            const headers = { 'Content-Type': 'application/json' };
            const body = {
                "stationFromId": this.selectedStationFrom.stationId,
                "stationToId": this.selectedStationTo.stationId,
                "time": this.selectedTime || "00:00"
            };
            const urlRoutes = `http://localhost:6767/browse/routes`;

            fetch(urlRoutes, { method: 'POST', headers: headers, body: JSON.stringify(body) })
                .then(res => {
                    if (!res.ok) throw new Error('Nie udało się pobrać tras');
                    return res.json();
                })
                .then(data => {
                    this.searched = true;
                    const availabilityPromises = data.map(routePath => {
                        if (!routePath || routePath.length === 0) return Promise.resolve(null);
                        const startNode = routePath.find(s => s.stationId === this.selectedStationFrom.stationId) || routePath[0];
                        const url = `http://localhost:6767/browse/availability?from=${this.selectedStationFrom.stationId}&to=${this.selectedStationTo.stationId}&route=${startNode.routeId}`;
                        return fetch(url).then(res => res.json());
                    });

                    Promise.all(availabilityPromises).then(availabilities => {
                        this.routes = data.map((routePath, index) => {
                            if (!routePath || routePath.length === 0) return null;

                            const startNode = routePath.find(s => s.stationId === this.selectedStationFrom.stationId) || routePath[0];
                            const endNode = routePath.find(s => s.stationId === this.selectedStationTo.stationId) || routePath[routePath.length - 1];

                            const startStation = this.allStations.find(s => s.stationId === startNode.stationId);
                            const endStation = this.allStations.find(s => s.stationId === endNode.stationId);
                            const availability = availabilities[index];

                            return {
                                routeId: startNode.routeId,
                                startStationName: startStation ? startStation.stationName : 'Stacja ' + startNode.stationId,
                                endStationName: endStation ? endStation.stationName : 'Stacja ' + endNode.stationId,
                                departureTime: startNode.departureTime,
                                arrivalTime: endNode.arrivalTime,
                                trainName: startNode.trainName,
                                ticketCount: availability ? availability.capacity : 0,
                                takenTicketCount: availability ? availability.ticketsSold : 0
                            };
                        }).filter(r => r !== null);
                    });
                })
                .catch(err => console.error("Błąd podczas wyszukiwania tras:", err));
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