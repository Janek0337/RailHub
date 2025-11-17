<template>
    <ResourceView
        :resourceName="'pociąg'"
        :resourceNamePlural="'Pociągi'"
        :apiEndpoint="'http://localhost:6767/admin/trains'"
        :fields="trainFields"
        :displayFields="trainDisplayFields"
        :idKey="'id'"
    />
</template>

<script>
import ResourceView from './ResourceView.vue';

export default {
    components: {
        ResourceView
    },
    data() {
        return {
            trainFields: [
                { name: 'trainName', label: 'Nazwa pociągu', type: 'text' },
                { name: 'capacity', label: 'Liczba miejsc', type: 'number' },
            ],
            trainDisplayFields: [
                { key: 'trainName', label: 'Nazwa' },
                { key: 'capacity', label: 'Liczba miejsc' },
            ]
        };
    }
}
</script>

<style scoped>
/* Style specyficzne dla widoku pociągów, jeśli potrzebne */
</style>