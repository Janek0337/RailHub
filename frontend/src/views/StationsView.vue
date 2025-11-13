<template>
    <ResourceView
        :resourceName="'stacja'"
        :resourceNamePlural="'Stacje'"
        :apiEndpoint="'http://localhost:6767/admin/stations'"
        :formFields="stationFields"
        :displayFields="stationDisplayFields"
        :idKey="'stationId'"
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
            stationFields: [
                { name: 'stationName', label: 'Nazwa stacji', type: 'text' },
            ],
            stationDisplayFields: [
                { key: 'stationName', label: 'Nazwa' },
            ]
        };
    }
}
</script>

<style scoped>
/* Style specyficzne dla widoku stacji, jeśli potrzebne */
</style>
