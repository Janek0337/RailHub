import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SignupForm from '@/components/SignupForm.vue'
import NotAuthorised from '@/views/NotAuthorised.vue'
import TicketTypesView from '../views/TicketTypesView.vue'
import StationsView from '../views/StationsView.vue'
import TrainsView from '../views/TrainsView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/login',
    name: 'login',
    component: SignupForm
  },
  {
    path: '/ticket-types',
    name: 'ticket-types',
    component: TicketTypesView
  },
  {
    path: '/stations',
    name: 'stations',
    component: StationsView
  },
  {
    path: '/trains',
    name: 'trains',
    component: TrainsView
  },
  {
    path: '/not-authorised',
    name: 'not-authorised',
    component: NotAuthorised
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
