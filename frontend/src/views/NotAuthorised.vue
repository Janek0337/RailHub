<template>
  <div class="not-authorized">
    <div class="content">
      <h1 class="title">Brak Dostępu</h1>
      <p class="message">
        Nie masz uprawnień, aby wyświetlić tę stronę lub Twoja sesja wygasła.
      </p>
      <button @click="goToLogin" class="btn btn-primary">
        Przejdź do strony logowania
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotAuthorized',
  methods: {
    goToLogin() {
      this.$router.push('/login'); 
    }
  }
}
</script>

<style scoped>
.not-authorized {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh; 
  text-align: center;
  padding: 20px;
}

.content {
  max-width: 400px;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

.title {
  color: #dc3545; /* Czerwony kolor dla ostrzeżenia */
  font-size: 2.5em;
  margin-bottom: 15px;
}

.message {
  color: #6c757d;
  font-size: 1.1em;
  margin-bottom: 25px;
}

.btn-primary {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-primary:hover {
  background-color: #0056b3;
}
</style>