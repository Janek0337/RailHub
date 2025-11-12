<template>
    <div class="ticket-types-container">
        <h1>Typy biletów</h1>
        <button @click="openAddModal" class="add-button">Dodaj typ biletu</button>
        <div v-for="tt in ticket_types" :key="tt.ticketTypeId" class="ticket-type-card">
            <div class="ticket-info">
                <span class="ticket-name">Bilet {{ tt.ticketName }}</span>
                <span>Zniżka: {{ tt.discountPercent }}%</span>
            </div>
            <div class="ticket-actions">
                <button class="edit-button" @click="editTT(tt)">Edytuj</button>
                <button class="delete-button" @click="deleteItem(tt)">Usuń</button>
            </div>
        </div>
    </div>

    <div v-if="isModalVisible" class="modal-backdrop">
        <div class="modal-content">
            <h2>{{ modalMode === 'create' ? 'Dodaj nowy typ biletu' : 'Edytuj typ biletu' }}</h2>
            <ResourceForm
                :key="modalMode + (editingTicket.id || 'new')"
                :fields="ticketTypeFields"
                :initial-data="editingTicket"
                :mode="modalMode"
                :api-endpoint="apiEndpoint"
                @item-created="handleItemCreated" @item-updated="handleItemUpdated"
                @submission-failed="closeModal"
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
    data() {
        return {
            ticket_types: [],
            isModalVisible: false,
            editingTicket: {},
            modalMode: 'create', // 'create' lub 'edit'
            apiEndpoint: 'http://localhost:6767/admin/ticket-types',
            ticketTypeFields: [
                { name: 'ticketName', label: 'Nazwa typu biletu', type: 'text' },
                { name: 'discountPercent', label: '% zniżki', type: 'number', min: 0, max: 100, step: 1 }
            ]
        }
    },
    methods: {
        openAddModal() {
            this.modalMode = 'create';
            this.editingTicket = {};
            this.isModalVisible = true;
        },
        editTT(ticket) {
            this.modalMode = 'edit';
            this.editingTicket = { ...ticket, id: ticket.ticketTypeId };
            this.isModalVisible = true;
        },
        closeModal() {
            this.isModalVisible = false;
            this.editingTicket = {};
        },
        handleItemCreated(newItem) {
            this.ticket_types.push(newItem);
            this.closeModal();
        },
        handleItemUpdated(updatedTicket) {
            const index = this.ticket_types.findIndex(t => t.ticketTypeId === updatedTicket.ticketTypeId);
            if (index !== -1) {
                this.ticket_types[index] = updatedTicket;
            }
            this.closeModal();
        },
        deleteItem(itemToDelete) {
            if (!confirm(`Czy na pewno chcesz usunąć "${itemToDelete.ticketName}"?`)) {
                return;
            }

            const jwtToken = localStorage.getItem('user_token');
            const url = `${this.apiEndpoint}/${itemToDelete.ticketTypeId}`;

            fetch(url, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${jwtToken}`
                },
                body: JSON.stringify({
                  'id': itemToDelete.ticketTypeId
                })
            })
            .then(res => {
                if (!res.ok) {
                    throw new Error('Nie udało się usunąć elementu');
                }
                this.ticket_types = this.ticket_types.filter(t => t.ticketTypeId !== itemToDelete.ticketTypeId);
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
            this.ticket_types = data;
            console.log('Pobrane typy biletów:', this.ticket_types);
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
.ticket-types-container {
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
  background-color: #6c757d; /* Szary kolor */
  margin-top: 15px;
}

.close-button:hover {
  background-color: #5a6268;
}

.ticket-types-container h1 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
}

.ticket-type-card {
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

.ticket-type-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.ticket-info {
  display: flex;
  flex-direction: column;
}

.ticket-name {
  font-size: 1.2em;
  font-weight: bold;
  color: #2c3e50;
}

.edit-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

.edit-button:hover {
  background-color: #0056b3;
}

.delete-button {
  background-color: #dc3545; /* Czerwony */
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s ease;
  margin-left: 10px;
}

.delete-button:hover {
  background-color: #c82333;
}

.add-button {
  background-color: #28a745; /* Zielony kolor */
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s ease;
  margin-bottom: 25px; /* Odstęp od listy poniżej */
  font-size: 1em;
}

.add-button:hover {
  background-color: #218838; /* Ciemniejszy zielony po najechaniu */
}
</style>