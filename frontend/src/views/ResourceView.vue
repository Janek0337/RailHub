<template>
    <div class="resource-container">
        <h1>{{ resourceNamePlural }}</h1>
        <button @click="openAddModal" class="add-button">Dodaj {{ resourceName }}</button>
        <div v-for="item in items" :key="item[idKey]" class="resource-card">
            <div class="resource-info">
                <div v-for="field in displayFields" :key="field.key" class="info-item">
                    <span class="info-label">{{ field.label }}:</span>
                    <span class="info-value">{{ getItemValue(item, field) }}</span>
                </div>
            </div>
            <div class="resource-actions">
                <button class="edit-button" @click="editItem(item)">Edytuj</button>
                <button class="delete-button" @click="deleteItem(item)">Usuń</button>
            </div>
        </div>
    </div>

    <div v-if="isModalVisible" class="modal-backdrop">
        <div class="modal-content">
            <h2>{{ modalMode === 'create' ? 'Dodaj nowy ' + resourceName : 'Edytuj ' + resourceName }}</h2>
            <ResourceForm
                :key="modalMode + (editingItem[idKey] || 'new')"
                :fields="fields"
                :initial-data="editingItem"
                :mode="modalMode"
                :api-endpoint="apiEndpoint"
                :idKey="idKey"
                @item-created="handleItemCreated"
                @item-updated="handleItemUpdated"
                @submission-failed="handleSubmissionFailed"
            />
            <button @click="closeModal" class="close-button" style="float: left;">Anuluj</button>
        </div>
    </div>
</template>

<script>
import ResourceForm from '@/components/ResourceForm.vue';

export default {
    components: {
        ResourceForm
    },
    props: {
        resourceName: { type: String, required: true },
        resourceNamePlural: { type: String, required: true },
        apiEndpoint: { type: String, required: true },
        fields: { type: Array, required: true },
        displayFields: { type: Array, required: true },
        idKey: { type: String, required: true }
    },
    data() {
        return {
            items: [],
            isModalVisible: false,
            editingItem: {},
            modalMode: 'create', // 'create' or 'edit'
        }
    },
    methods: {
        getItemValue(item, field) {
            const value = item[field.key];
            const fieldDefinition = this.fields.find(f => f.name === field.key);

            if (fieldDefinition && fieldDefinition.type === 'select' && fieldDefinition.options) {
                const option = fieldDefinition.options.find(o => o.value === value);
                return option ? option.text : value;
            }

            if (field.suffix) {
                return `${value}${field.suffix}`;
            }
            return value;
        },
        openAddModal() {
            this.modalMode = 'create';
            this.editingItem = {};
            this.isModalVisible = true;
        },
        editItem(item) {
            this.modalMode = 'edit';
            this.editingItem = { ...item, id: item[this.idKey] };
            this.isModalVisible = true;
        },
        closeModal() {
            this.isModalVisible = false;
            this.editingItem = {};
        },
        handleItemCreated(newItem) {
            this.items.push(newItem);
            this.closeModal();
        },
        handleItemUpdated(updatedItem) {
            const index = this.items.findIndex(i => i[this.idKey] === updatedItem[this.idKey]);
            if (index !== -1) {
                this.items[index] = updatedItem;
            }
            this.closeModal();
        },
        handleSubmissionFailed(errorMessage) {
            alert(errorMessage);
        },
        deleteItem(itemToDelete) {
            const mainDisplayField = this.displayFields[0];
            const itemName = this.getItemValue(itemToDelete, mainDisplayField);
            if (!confirm(`Czy na pewno chcesz usunąć "${itemName}"?`)) {
                return;
            }

            const jwtToken = localStorage.getItem('user_token');
            const url = `${this.apiEndpoint}/${itemToDelete[this.idKey]}`;

            fetch(url, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${jwtToken}`
                },
                 body: JSON.stringify({
                  'id': itemToDelete[this.idKey]
                })
            })
            .then(res => {
                if (!res.ok) {
                    throw new Error('Nie udało się usunąć elementu');
                }
                this.items = this.items.filter(i => i[this.idKey] !== itemToDelete[this.idKey]);
            })
            .catch(err => console.error("Błąd podczas usuwania:", err));
        }
    },
    mounted() {
        const jwtToken = localStorage.getItem('user_token');
        fetch(this.apiEndpoint, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwtToken}`
            }
        })
        .then(res => {
            if (res.status === 403) {
                this.$router.push('/not-authorised');
                return Promise.reject(new Error('Forbidden: 403'));
            }
            return res.json()
        })
        .then(data => {
            this.items = data;
        })
        .catch(err => {
            if (err.message !== 'Forbidden: 403') {
                console.error("Wystąpił błąd podczas pobierania danych:", err);
            }
        });
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
  min-width: 300px;
  text-align: left;
}

.modal-content h2 {
  text-align: center;
  margin-top: 0;
  margin-bottom: 25px;
}

.close-button {
  background-color: #6c757d;
  margin-top: 15px;
}

.close-button:hover {
  background-color: #5a6268;
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
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}

.resource-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.resource-info {
  display: flex;
  flex-direction: column;
}

.info-item {
    display: flex;
    align-items: center;
    margin-bottom: 5px;
}

.info-label {
    font-weight: bold;
    color: #555;
    margin-right: 8px;
}

.info-value {
    font-size: 1.1em;
    color: #2c3e50;
}

.resource-actions .edit-button {
  background-color: #007bff;
  color: white;
}

.resource-actions .edit-button:hover {
  background-color: #0056b3;
}

.resource-actions .delete-button {
  background-color: #dc3545;
  color: white;
  margin-left: 10px;
}

.resource-actions .delete-button:hover {
  background-color: #c82333;
}

.add-button {
  background-color: #28a745;
  color: white;
  margin-bottom: 25px;
}

.add-button:hover {
  background-color: #218838;
}

.resource-actions button, .add-button {
    border: none;
    padding: 8px 16px;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.3s ease;
}
</style>
