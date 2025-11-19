<template>
  <div>
    <div v-if="error" class="error-message">{{ error }}</div>
    <h3>Zdefiniuj stacje na trasie</h3>
    <div v-for="stop in stations" :key="stop.stationId">
      <p>{{ stop.stationName }} | Przyjazd: {{ stop.arrivalTime }} |
            Odjazd: {{ stop.departureTime }} | Kilometr: {{ stop.routeKilometer }}
      </p>
    </div>
  </div>
</template>

<script>
import draggable from 'vue-draggable-next'

export default {
  name: 'RouteForm',
  components: {
    draggable,
  },
  props: ['routeId'],
  data() {
    return {
      stations: [],
      error: null
    }
  },
  mounted() {
        const jwtToken = localStorage.getItem('user_token')
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        }
        const url = `http://localhost:6767/admin/route-stations/${this.routeId}`

        fetch(url, {
                method: 'GET',
                headers: headers
            })
            .then(res => {
              if (!res.ok) {
                throw new Error('Nie udało się pobrać stacji dla trasy');
                }
              return res.json()
            })
            .then(data => {
                console.log('Dane stacji dla trasy:', data);
                this.stations = data
                this.error = null
            })
            .catch(err => console.error("Błąd podczas pobierania stacji dla trasy:", err))
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
</style>