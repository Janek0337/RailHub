<template>
    <form @submit.prevent="handleSubmit">
        <div v-for="field in fields" :key="field.name" class="form-group">
            <label :for="field.name">{{ field.label }}</label>
            <input 
                :type="field.type || 'text'"
                :id="field.name"
                v-model="localData[field.name]"
                :min="field.min"
                :max="field.max"
                :step="field.step"
                required
            >
        </div>
        <div class="form-actions">
            <button type="submit">{{ mode === 'create' ? 'Utwórz' : 'Zapisz' }}</button>
        </div>
    </form>
</template>

<script>
export default {
    props: {
        // Konfiguracja pól formularza
        fields: {
            type: Array,
            required: true
        },
        // Dane początkowe (dla edycji)
        initialData: {
            type: Object,
            default: () => ({})
        },
        // Tryb 'create' lub 'edit'
        mode: {
            type: String,
            default: 'create'
        },
        // Endpoint API do wysłania danych
        apiEndpoint: {
            type: String,
            required: true
        }
    },
    data() {
        return {
            // Lokalna, modyfikowalna kopia danych
            localData: this.fields.reduce((acc, field) => {
                // Sprawdzamy, czy initialData ma daną właściwość, aby poprawnie obsłużyć wartości takie jak 0 lub false
                const initialValue = this.initialData.hasOwnProperty(field.name) 
                    ? this.initialData[field.name] 
                    : (field.defaultValue || '');
                acc[field.name] = initialValue;
                return acc;
            }, {})
        };
    },
    methods: {
        handleSubmit() {
            const jwtToken = localStorage.getItem('user_token');
            const isEditMode = this.mode === 'edit';
            
            // W trybie edycji dodajemy ID do endpointu
            const url = isEditMode 
                ? `${this.apiEndpoint}/${this.initialData.id}` 
                : this.apiEndpoint;

            const method = isEditMode ? 'PUT' : 'POST';

            // Przygotowujemy dane do wysłania
            const payload = { ...this.localData };
            if (isEditMode) {
                payload.ticketTypeId = this.initialData.id; // Używamy nazwy pola z encji Javy
            }

            fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwtToken}`
                },
                body: JSON.stringify(payload)
            })
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Błąd operacji ${method}`);
                }
                return res.json();
            })
            .then(responseData => {
                console.log(`Operacja ${method} zakończona sukcesem`, responseData);
                // Emitujemy odpowiednie zdarzenie
                this.$emit(isEditMode ? 'item-updated' : 'item-created', responseData);
            })
            .catch(err => {
                console.error(err);
                this.$emit('submission-failed');
            });
        }
    }
}
</script>

<style scoped>
.form-group {
    margin-bottom: 15px;
}
label {
    display: block;
    margin-bottom: 5px;
}
input {
    width: 100%;
    padding: 8px;
    box-sizing: border-box;
}
.form-actions {
    text-align: right;
    margin-top: 20px;
}
</style>
