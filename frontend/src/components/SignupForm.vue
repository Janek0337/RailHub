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
                console.log('Udane logowanie')
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