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
        <p>Godzina odjazdu</p>
        <input type="time" v-model="selectedTime">
        <button @click="handleSearch">Szukaj</button>
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
    <div v-if="selectedRouteForPurchase" class="modal-overlay">
        <div class="modal-content">
            <h2>Kup bilet</h2>
            <h3>Trasa: {{ selectedRouteForPurchase.startStationName }} -> {{ selectedRouteForPurchase.endStationName }}</h3>
            <div v-for="ticketType in ticketTypes" :key="ticketType.ticketTypeId" class="ticket-type-selector">
                <label>{{ ticketType.ticketName }} ({{ ticketType.discountPercent }}% zniżki):</label>
                <input type="number" v-model.number="ticketQuantities[ticketType.ticketTypeId]" min="0" />
            </div>
            <div class="total-price">
                <h3>Suma: {{ totalPrice.toFixed(2) }} zł</h3>
            </div>
            <div class="modal-actions">
                <button @click="confirmPurchase">Kup</button>
                <button @click="cancelPurchase">Anuluj</button>
            </div>
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
            routes: [],
            selectedRouteForPurchase: null,
            ticketTypes: [],
            ticketQuantities: {}
        }
    },
    computed: {
        totalPrice() {
            if (!this.selectedRouteForPurchase) return 0;
            const costPerKilometer = 2;
            const distance = this.selectedRouteForPurchase.kilometerTo - this.selectedRouteForPurchase.kilometerFrom;

            return this.ticketTypes.reduce((total, ticketType) => {
                const quantity = this.ticketQuantities[ticketType.ticketTypeId] || 0;
                const pricePerTicket = (costPerKilometer * distance) * (1 - (ticketType.discountPercent / 100));
                return total + (quantity * pricePerTicket);
            }, 0);
        }
    },
    mounted() {
        this.fetchStations();
        this.fetchTicketTypes();
    },
    methods: {
        fetchTicketTypes() {
            const headers = { 'Content-Type': 'application/json' };
            const url = `http://localhost:6767/browse/ticket-types`;
            fetch(url, { method: 'GET', headers: headers })
                .then(res => {
                    if (!res.ok) throw new Error('Nie udało się pobrać typów biletów');
                    return res.json();
                })
                .then(data => {
                    this.ticketTypes = data;
                    this.ticketTypes.forEach(t => this.ticketQuantities[t.ticketTypeId] = 0);
                })
                .catch(err => console.error("Błąd podczas pobierania typów biletów:", err));
        },
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

                    if (data.length === 0) {
                        this.routes = [];
                        return;
                    }

                    const availabilityRequests = data.map(routePath => {
                        if (!routePath || routePath.length === 0) return null;
                        const startNode = routePath.find(s => s.stationId === this.selectedStationFrom.stationId) || routePath[0];
                        return {
                            stationFromId: this.selectedStationFrom.stationId,
                            stationToId: this.selectedStationTo.stationId,
                            routeId: startNode.routeId
                        };
                    }).filter(req => req !== null);

                    const url = `http://localhost:6767/browse/availability`;
                    const headers = { 'Content-Type': 'application/json' };

                    fetch(url, { method: 'POST', headers: headers, body: JSON.stringify(availabilityRequests) })
                        .then(res => res.json())
                        .then(availabilities => {
                            const availableRouteIds = new Set(availabilities.map(a => a.routeId));
                            const availabilityMap = new Map(availabilities.map(a => [a.routeId, a]));

                            const availableRoutesData = data.filter(routePath => {
                                if (!routePath || routePath.length === 0) return false;
                                const startNode = routePath.find(s => s.stationId === this.selectedStationFrom.stationId) || routePath[0];
                                return availableRouteIds.has(startNode.routeId);
                            });

                            this.routes = availableRoutesData.map(routePath => {
                                const startNode = routePath.find(s => s.stationId === this.selectedStationFrom.stationId) || routePath[0];
                                const endNode = routePath.find(s => s.stationId === this.selectedStationTo.stationId) || routePath[routePath.length - 1];

                                const startStation = this.allStations.find(s => s.stationId === startNode.stationId);
                                const endStation = this.allStations.find(s => s.stationId === endNode.stationId);
                                const availability = availabilityMap.get(startNode.routeId);

                                return {
                                    routeId: startNode.routeId,
                                    startStationName: startStation ? startStation.stationName : 'Stacja ' + startNode.stationId,
                                    endStationName: endStation ? endStation.stationName : 'Stacja ' + endNode.stationId,
                                    departureTime: startNode.departureTime,
                                    arrivalTime: endNode.arrivalTime,
                                    trainName: startNode.trainName,
                                    ticketCount: availability.capacity,
                                    takenTicketCount: availability.ticketsSold,
                                    kilometerFrom: startNode.routeKilometer,
                                    kilometerTo: endNode.routeKilometer
                                };
                            });
                        });
                })
                .catch(err => console.error("Błąd podczas wyszukiwania tras:", err));
        },
        handleBuy(routeId) {
            this.selectedRouteForPurchase = this.routes.find(r => r.routeId === routeId);
        },
        cancelPurchase() {
            this.selectedRouteForPurchase = null;
            this.ticketQuantities = {};
        },
        confirmPurchase() {
            const ticketsToBuy = Object.entries(this.ticketQuantities)
                .filter(([, quantity]) => quantity > 0)
                .map(([ticketTypeId, quantity]) => ({ ticketTypeId: Number(ticketTypeId), quantity }));

            if (ticketsToBuy.length === 0) {
                alert("Proszę wybrać przynajmniej jeden bilet.");
                return;
            }

            const body = {
                routeId: this.selectedRouteForPurchase.routeId,
                stationFromId: this.selectedStationFrom.stationId,
                stationToId: this.selectedStationTo.stationId,
                tickets: ticketsToBuy
            };

            const headers = { 'Content-Type': 'application/json' };
            const url = `http://localhost:6767/browse/tickets`;

            fetch(url, { method: 'POST', headers: headers, body: JSON.stringify(body) })
                .then(res => {
                    if (!res.ok) {
                        return res.text().then(text => { throw new Error(text || 'Nie udało się kupić biletów') });
                    }
                    alert("Bilety zostały pomyślnie zakupione!");
                    this.cancelPurchase();
                    this.handleSearch();
                })
                .catch(err => {
                    console.error("Błąd podczas zakupu biletów:", err);
                    alert(`Wystąpił błąd: ${err.message}`);
                });
        }
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

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal-content {
    background: white;
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.26);
}

.ticket-type-selector {
    margin-bottom: 10px;
}

.total-price {
    margin-top: 20px;
    font-weight: bold;
}

.modal-actions {
    margin-top: 20px;
    text-align: right;
}
</style>