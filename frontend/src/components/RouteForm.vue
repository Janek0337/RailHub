<template>
  <div>
    <h3>Zdefiniuj stacje na trasie</h3>
    <draggable v-model="stations" item-key="stationId">
      <template #item="{element, index}">
        <div class="list-group-item">
          <span>{{ index + 1 }}. {{ element.stationId }} | Przyjazd: {{ element.arrivalTime }} | 
            Odjazd: {{ element.departureTime }} | Kilometr: {{ element.routeKilometer }}
          </span>
        </div>
      </template>
    </draggable>
  </div>
</template>

<script>
import draggable from 'vue-draggable-next'

export default {
  name: 'RouteForm',
  components: {
    draggable,
  },
  props: [routeId],
  data() {
    return {
      stations: []
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
                return res.json();
            })
            .then(data => {
                this.stations = data
            })
            .catch(err => console.error("Błąd podczas pobierania stacji dla trasy:", err))
    }
}
</script>

<style scoped>
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
</style>