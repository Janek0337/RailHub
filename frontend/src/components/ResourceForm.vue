<template>
    <form @submit.prevent="handleSubmit">
        <div v-if="mode === 'edit' && initialData.id" class="form-group">
            <label for="id">ID</label>
            <input type="text" id="id" :value="initialData.id" disabled>
        </div>
        <div v-for="field in fields" :key="field.name" class="form-group">
            <label :for="field.name">{{ field.label }}</label>
            <select
                v-if="field.type === 'select'"
                :id="field.name"
                v-model="localData[field.name]"
                required
            >
                <option v-for="option in field.options" :key="option.value" :value="option.value">
                    {{ option.text }}
                </option>
            </select>
            <input
                v-else
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
        },
        // Klucz identyfikatora
        idKey: {
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
                payload[this.idKey] = this.initialData.id;
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
                if (res.ok) {
                    return res.json()
                } else {
                    return res.json().then(errorBody => {
                        const error = new Error(errorBody.message || `Błąd operacji ${method}`)
                        throw error
                    })
                }
            })
            .then(responseData => {
                console.log(`Operacja ${method} zakończona sukcesem`, responseData);
                // Emitujemy odpowiednie zdarzenie
                this.$emit(isEditMode ? 'item-updated' : 'item-created', responseData);
            })
            .catch(err => {
                console.error("Błąd:", err.message);
                this.$emit('submission-failed', err.message);
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
input:disabled {
    background-color: #eeeeee;
    cursor: not-allowed;
    border: 1px solid #ccc;
    color: #555;
}
.form-actions {
    text-align: right;
    margin-top: 20px;
}
</style>
