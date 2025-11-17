<template>
    <h1>Routes</h1>
    <div v-for="route in routes" :key="route.routeId">
        <h3>{{ route.startStationName }} -> {{ route.endStationName }}</h3>
        <h4>Pociąg: {{ route.trainName }}</h4>
        <button @click="handleEdit(route.routeId)">Edytuj</button>
        <button @click="handleDelete(route.routeId)">Usuń</button>
    </div>
</template>

<script>
export default {
    data() {
        return {
            routes: [],
        }
    },
    methods: {
        handleEdit(routeId) {

        },
        handleDelete(routeId) {

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
</style>