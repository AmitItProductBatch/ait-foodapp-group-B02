// ==============================================================================
// Food Delivery API Service Layer
// Dynamic endpoint resolution and comprehensive error handling
// ==============================================================================

const resolveApiBase = () => {
  const envUrl = import.meta.env.VITE_API_BASE_URL
  if (envUrl && !envUrl.includes('localhost') && !envUrl.includes('127.0.0.1')) {
    // If env URL includes /api/users, extract base origin
    try {
      const u = new URL(envUrl)
      return `${u.protocol}//${u.host}`
    } catch {
      return 'http://194.242.57.93:8082'
    }
  }
  const hostname = typeof window !== 'undefined' && window.location.hostname ? window.location.hostname : 'localhost'
  return `http://${hostname}:8082`
}

export const API_HOST = resolveApiBase()

const request = async (path, options = {}) => {
  const url = `${API_HOST}${path}`
  const defaultHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json, text/plain, */*'
  }

  const config = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers
    }
  }

  if (config.body && typeof config.body === 'object') {
    config.body = JSON.stringify(config.body)
  }

  try {
    const response = await fetch(url, config)
    const text = await response.text()
    
    let data
    try {
      data = JSON.parse(text)
    } catch {
      data = text
    }

    if (!response.ok) {
      const errorMsg = (data && data.message) || (data && data.error) || (typeof data === 'string' && data) || `HTTP Error ${response.status}`
      throw new Error(errorMsg)
    }

    return data
  } catch (err) {
    console.error(`API Error on ${options.method || 'GET'} ${path}:`, err)
    throw err
  }
}

// ------------------------------------------------------------------------------
// Health & Observability
// ------------------------------------------------------------------------------
export const getHealth = () => request('/actuator/health')

// ------------------------------------------------------------------------------
// Users Management
// ------------------------------------------------------------------------------
export const getAllUsers = () => request('/api/users')
export const getUserById = (id) => request(`/api/users/${id}`)
export const createUser = (user) => request('/api/users', { method: 'POST', body: user })
export const updateUser = (id, user) => request(`/api/users/${id}`, { method: 'PUT', body: user })
export const deleteUser = (id) => request(`/api/users/${id}`, { method: 'DELETE' })

// ------------------------------------------------------------------------------
// User Addresses
// ------------------------------------------------------------------------------
export const saveUserAddress = (addr) => request('/api/user-address', { method: 'POST', body: addr })
export const getUserAddressById = (id) => request(`/api/user-address/${id}`)
export const getUserAddresses = (userId) => request(`/api/user-address/user/${userId}`)
export const deleteUserAddress = (id) => request(`/api/user-address/${id}`, { method: 'DELETE' })

// ------------------------------------------------------------------------------
// Restaurants
// ------------------------------------------------------------------------------
export const getAllRestaurants = () => request('/api/restaurants')
export const getRestaurantById = (id) => request(`/api/restaurants/${id}`)
export const createRestaurant = (rest) => request('/api/restaurants', { method: 'POST', body: rest })
export const updateRestaurant = (id, rest) => request(`/api/restaurants/${id}`, { method: 'PUT', body: rest })
export const searchRestaurantsByCity = (city) => request(`/api/restaurants/search?city=${encodeURIComponent(city)}`)

// ------------------------------------------------------------------------------
// Restaurant Addresses
// ------------------------------------------------------------------------------
export const getAllRestaurantAddresses = () => request('/api/restaurant-addresses')
export const saveRestaurantAddress = (addr) => request('/api/restaurant-addresses', { method: 'POST', body: addr })

// ------------------------------------------------------------------------------
// Food Items (Menu)
// ------------------------------------------------------------------------------
export const getAllFoodItems = () => request('/food/all')
export const getFoodItemById = (id) => request(`/food/${id}`)
export const createFoodItem = (item) => request('/food/add', { method: 'POST', body: item })
export const updateFoodItem = (id, item) => request(`/food/update/${id}`, { method: 'PUT', body: item })
export const deleteFoodItem = (id) => request(`/food/delete/${id}`, { method: 'DELETE' })

// ------------------------------------------------------------------------------
// Cart & Cart Items
// ------------------------------------------------------------------------------
export const getCartByUserId = (userId) => request(`/api/cart/${userId}`)
export const createCart = (userId, restaurantId) => request('/api/cart', { method: 'POST', body: { userId, restaurentId: restaurantId } })
export const deleteCart = (cartId) => request(`/api/cart/${cartId}`, { method: 'DELETE' })

export const addCartItem = (cartId, foodItemId, quantity = 1) => 
  request('/api/cart/items', { method: 'POST', body: { cartId, foodItemId, quantity } })
export const deleteCartItem = (cartItemId) => request(`/api/cart/items/${cartItemId}`, { method: 'DELETE' })

// ------------------------------------------------------------------------------
// Delivery Fee Calculation
// ------------------------------------------------------------------------------
export const calculateDeliveryFee = (restaurantAddressId, userAddressId, cartId) =>
  request('/api/prices/delivery-fee', {
    method: 'POST',
    body: { restaurantAddressId, userAddressId, cartId }
  })

// ------------------------------------------------------------------------------
// Orders
// ------------------------------------------------------------------------------
export const createOrder = (orderData) => request('/api/orders', { method: 'POST', body: orderData })
export const getAllOrders = () => request('/api/orders')
export const getOrderById = (id) => request(`/api/orders/${id}`)
export const updateOrderStatus = (id, status) => request(`/api/orders/${id}?status=${encodeURIComponent(status)}`, { method: 'PUT' })
export const cancelOrder = (id) => request(`/api/orders/${id}`, { method: 'DELETE' })

// ------------------------------------------------------------------------------
// Payments
// ------------------------------------------------------------------------------
export const createPayment = (paymentData) => request('/api/payments', { method: 'POST', body: paymentData })
export const getAllPayments = () => request('/api/payments')
export const updatePaymentStatus = (id, status) => request(`/api/payments/${id}?status=${encodeURIComponent(status)}`, { method: 'PUT' })

// ------------------------------------------------------------------------------
// Feedback & Reviews
// ------------------------------------------------------------------------------
export const createFeedback = (feedback) => request('/api/feedback', { method: 'POST', body: feedback })
export const getAllFeedback = () => request('/api/feedback')
export const getRestaurantFeedback = (restaurantId) => request(`/api/feedback/restaurant/${restaurantId}`)

// ------------------------------------------------------------------------------
// Delivery Pricing Rules
// ------------------------------------------------------------------------------
export const getAllDeliveryPricingRules = () => request('/delieverypricing')
export const saveDeliveryPricingRule = (rule) => request('/delieverypricing/add', { method: 'POST', body: rule })
