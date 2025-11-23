<template>
    <div class="resource-container">
        <h1>Trasy</h1>
        <div v-for="route in routes" :key="route.routeId" class="resource-card">
            <div class="resource-info">
                <h3>{{ route.startStationName }} -> {{ route.endStationName }}</h3>
                <h4>Pociąg: {{ route.trainName }}</h4>
            </div>
            <div class="resource-actions">
                <button class="edit-button" @click="handleEdit(route.routeId)">Edytuj</button>
                <button class="delete-button" @click="handleDelete(route.routeId)">Usuń</button>
            </div>
        </div>
    </div>

    <div v-if="isModalVisible" class="modal-backdrop">
        <div class="modal-content">
            <RouteForm v-if="selectedRouteId" :key="selectedRouteId" :routeId="selectedRouteId" />
            <button @click="closeModal" class="close-button">Zamknij</button>
        </div>
    </div>
</template>

<script>
import RouteForm from '@/components/RouteForm.vue';

export default {
    components: { RouteForm },
    data() {
        return {
            routes: [],
            isModalVisible: false,
            selectedRouteId: null,
        }
    },
    methods: {
        handleEdit(routeId) {
            this.selectedRouteId = routeId;
            this.isModalVisible = true;
        },
        handleDelete(routeId) {
            if (!confirm(`Czy na pewno chcesz usunąć trasę "${routeId}"?`)) {
                return
            }
            const url = `http://localhost:6767/admin/routes/${routeId}`
            const jwtToken = localStorage.getItem('user_token');
            const headers = {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwtToken}`
            }
            fetch(url, {
                method: 'DELETE',
                headers: headers
            })
            .then(res => {
                if (!res.ok) {
                    throw new Error('Nie udało się usunąć trasy');
                }
                this.routes = this.routes.filter(route => route.routeId !== routeId);
            })
            .catch(err => console.error("Błąd podczas usuwania trasy:", err));
        },
        closeModal() {
            this.isModalVisible = false;
            this.selectedRouteId = null;
        }
    },
    mounted() {
        const jwtToken = localStorage.getItem('user_token')
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${jwtToken}`
        }
        const url = 'http://localhost:6767/admin/routes'

        fetch(url, {
                method: 'GET',
                headers: headers
            })
            .then(res => {
                if (!res.ok) {
                    throw new Error('Nie udało się pobrać tras');
                }
                return res.json();
            })
            .then(data => {
                this.routes = data
            })
            .catch(err => console.error("Błąd podczas pobierania tras:", err));
    }
}
</script>

<style scoped>
.resource-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  font-family: Avenir, Helvetica, Arial, sans-serif;
}

.resource-container h1 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
}

.resource-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.resource-info h3 {
    margin: 0 0 5px 0;
}
.resource-info h4 {
    margin: 0;
    font-weight: normal;
    color: #555;
}

.resource-actions button {
    border: none;
    padding: 8px 16px;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.3s ease;
}

.resource-actions .edit-button {
  background-color: #007bff;
  color: white;
}

.resource-actions .delete-button {
  background-color: #dc3545;
  color: white;
  margin-left: 10px;
}

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px 40px;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  min-width: 500px;
  text-align: center;
}

.close-button {
  background-color: #6c757d;
  color: white;
  margin-top: 15px;
  border: none;
  padding: 8px 16px;
  border-radius: 5px;
  cursor: pointer;
}
</style>