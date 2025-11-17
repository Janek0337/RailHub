<template>
    <ResourceView
        :resourceName="'typ biletu'"
        :resourceNamePlural="'Typy biletów'"
        :apiEndpoint="'http://localhost:6767/admin/ticket-types'"
        :fields="ticketTypeFields"
        :displayFields="ticketTypeDisplayFields"
        :idKey="'ticketTypeId'"
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
            ticketTypeFields: [
                { name: 'ticketName', label: 'Nazwa typu biletu', type: 'text' },
                { name: 'discountPercent', label: '% zniżki', type: 'number', min: 0, max: 100, step: 1 }
            ],
            ticketTypeDisplayFields: [
                { key: 'ticketName', label: 'Nazwa' },
                { key: 'discountPercent', label: 'Zniżka', suffix: '%' }
            ]
        };
    }
}
</script>

<style scoped>
/* Można tu zostawić specyficzne style, jeśli są potrzebne, lub je usunąć */
</style>