<template>
    <form @submit.prevent="handleSubmit">
        <label>Login:</label>
        <input type="text" required v-model="login">

        <label>Password:</label>
        <input type="password" required v-model="password">
        <div v-if="passwordError" class="error">{{ passwordError }}</div>

        <div class="submit">
            <button>Log in</button>
        </div>
        
    </form>
</template>

<script>
export default {
    data() {
        return {
            login: '',
            password: '',
            passwordError: '',
        }
    },
    methods: {
        showBrowserNotification(title, options) {
            // 1. Sprawdź, czy przeglądarka w ogóle wspiera powiadomienia
            if (!("Notification" in window)) {
                console.warn("Ta przeglądarka nie wspiera powiadomień desktopowych.");
                // Możemy użyć naszego wewnętrznego systemu jako alternatywy
                this.emitter.emit('notify', { message: title, type: 'success' });
                return;
            }

            // 2. Sprawdź, czy mamy już pozwolenie
            if (Notification.permission === "granted") {
                // Jeśli tak, utwórz powiadomienie
                new Notification(title, options);
            } else if (Notification.permission !== "denied") {
                // Jeśli nie, poproś o pozwolenie
                Notification.requestPermission().then(permission => {
                    if (permission === "granted") {
                        new Notification(title, options);
                    }
                });
            }
            // Jeśli pozwolenie zostało odrzucone (denied), nic nie możemy zrobić.
        },
        handleSubmit() {
            const datataToSend = {
                'login': this.login,
                'password': this.password
            }
            
        fetch('http://localhost:6767/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datataToSend)
        })
        .then(res => {
            if (!res.ok) {
                this.passwordError = 'Invalid username or password'
                console.log('Nieudane logowanie')
                throw new Error('Authorisation error ' + res.status)
            }
            
            return res.json()
        })
        .then(data => {
            if (data && data.token) {
                localStorage.setItem('user_token', data.token)
                console.log('Udane logowanie');

                // Wywołaj powiadomienie przeglądarki
                this.showBrowserNotification('Logowanie udane!', { body: `Witaj, ${this.login}!` });
                // Możesz też zostawić powiadomienie w aplikacji dla spójności
                this.emitter.emit('notify', { message: 'Zalogowano pomyślnie!', type: 'success' });
            }
            else {
                throw new Error("No token")
            }
        })
        .catch(err => {
            if (!this.passwordError) {
                console.log('Error')
            }
        })
        }
    }
}
</script>

<style>
form {
    max-width: 420px;
    margin: 30px auto;
    background: white;
    text-align: left;
    padding: 40px;
    border-radius: 10px;
}

label {
    color: #aaa;
    display: inline-block;
    margin: 25px 0 15px;
    font-size: 0.6em;
    text-transform: uppercase;
    letter-spacing: 1px;
    font-weight: bold;
}

input, select {
    display: block;
    padding: 10px 6px;
    width: 100%;
    box-sizing: border-box;
    border: none;
    border-bottom: 5px solid #ddd;
    color: #555;
}

input[type='checkbox'] {
    display: inline-block;
    width: 16px;
    margin: 0 10px 0 0;
    position: relative;
    top: 2px;
}

.pil {
    display: inline-block;
    margin: 20px 10px 0 0;
    padding: 6px 12px;
    background: #eee;
    border-radius: 20px;
    font-size: 12px;
    letter-spacing: 1px;
    font-weight: bold;
    color: #777;
    cursor: pointer;
}

button {
    background: #0b6dff;
    border: 0;
    padding: 10px 20px;
    margin-top: 20px;
    color: white;
    border-radius: 20px;
}
.submit {
    text-align: center;
}

.error{
    color: #ff0062;
    margin-top: 10px;
    font-size: 0.8em;
    font-weight: bold;
}
</style>